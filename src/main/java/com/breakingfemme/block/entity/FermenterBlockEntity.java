package com.breakingfemme.block.entity;

import java.util.Optional;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.FermenterControllerBlock;
import com.breakingfemme.block.FermenterHeaterBlock;
import com.breakingfemme.block.FermenterMixerBlock;
import com.breakingfemme.block.FermenterPanelBlock;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.datagen.ModBlockTagProvider;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.recipe.FermentingRecipe;
import com.breakingfemme.screen.FermenterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class FermenterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory { //could be transitioned to SimpleInventory for less hassle/not have an extra class?
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);
    public static final int STAGE_NOT_IN_USE = 5; //5 stages 0 to 4, and this value to stop when its done
    private static final int OUTPUT_SLOT_BEGIN = 4; //output slots must be right after all input slots
    private static final int MULTIBLOCK_CHECK_PERIOD = 40; //checking integrity and outside temp every 40 ticks
    private static final int MULTIBLOCK_CHECK_STATIC_DELTA = 17; //must be relatively prime with MULTIBLOCK_CHECK_PERIOD and below it, and such that its first few multiples cover [[0; MULTIBLOCK_CHECK_PERIOD]] as evenly as possible

    protected final PropertyDelegate propertyDelegate; //the PropertyDelegate is used for client<->server syncing (i mean here its really more like server->client, the only client->server is the inventory)

    private boolean waiting_for_recipe_loading = false;
    private String deferred_recipe = "minecraft:air"; //this is because when calling readNbt, the world is null, so cannot get its recipe manager immediately

    //these are saved variables: cannot derive from multiblock checking
    private int current_stage = STAGE_NOT_IN_USE; //the fermentation's current stage (always goes from 0 to 4 when in use, 5 ie STAGE_NOT_IN_USE when not in use)
    private int stage_progress = 0; //progress of current stage, in ticks
    private FermentingRecipe recipe = FermentingRecipe.NONE; //this should be NONE only when the fermenter is not in use
    private float temperature = -69420.0f; //in °C; initial value is for initialization to environment temperature
    private int grace_time = 0; //accumulated time with wrong conditions. adds 4 if wrong, removes 1 if right, min is 0. if exceeds a certain value, then the recipe fails.

    //these variables arent saved, can be derived from multiblock checking
    private int capacity = 0; //0 for incorrect multiblock, 1 for smallest (1*2*1), 4 to largest (3*3*3)
    private float outside_temp = 0;
    private int n_heaters = 0;
    private int volume = 1;
    private float conductivity = 0;
    private boolean is_mixing = false;


    public FermenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            //the bubble colors need to be done in this function
            //as we cannot send an integer recipe identifier
            public int get(int index) {
                if(index == 0)
                    return capacity;
                else if(index == 1)
                    return current_stage;
                else if(index == 2)
                    return stage_progress;
                else if(index == 3)
                    return Math.round(Math.min(temperature, 127.42f) * 16777216f);
                else if(index < 9) //what color does the (index - 4)th bubble need to be??
                {
                    //24 different colors, 0 white, 1 brown, 2 gray, 3 black etc., 24 is empty (transparent picture)
                    int stage = index - 4; //0 to 4
                    if(stage > current_stage) //future stages
                        return 24; //draw nothing
                    else if(stage == current_stage)
                        return conditionsMet() ? 0 : 1; //white if conditions ok, brown if not
                    else //past successfully completed stages
                        return recipe.bubbleColor(stage);
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if(index == 0)
                    capacity = value;
                else if(index == 1)
                    current_stage = value;
                else if(index == 2)
                    stage_progress = value;
                else if(index == 3)
                    temperature = ((float)value) * 5.960464477539063e-08f;
            }

            @Override
            public int size() {
                return 9; //number of values to synchronize
            }
        };
    }

    private boolean conditionsMet()
    {
        return recipe.conditionsMet(current_stage, stage_progress, temperature, is_mixing);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.entity.breakingfemme.fermenter");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);

        //recipe identifier
        Identifier id = recipe.getId();
        nbt.putString("recipe", id.toString()); //save current recipe

        //other variables that must be saved (cannot derive from multiblock checking)
        nbt.putInt("stage", current_stage);
        nbt.putInt("stage_progress", stage_progress);
        nbt.putInt("grace_time", grace_time);
        nbt.putFloat("temperature", temperature);

        Inventories.writeNbt(nbt, inventory); //save inventory
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);

        waiting_for_recipe_loading = true;
        deferred_recipe = nbt.getString("recipe"); //deferred recipe loading, because here this.world is null

        Inventories.readNbt(nbt, inventory); //load inventory

        //load other variables
        current_stage = nbt.getInt("stage");
        stage_progress = nbt.getInt("stage_progress");
        grace_time = nbt.getInt("grace_time");
        temperature = nbt.getFloat("temperature");
    }

    private boolean tryLoadRecipe()
    {
        //even if recipe loading fails, just give up, don't do it every single tick. this is just for loading anyway.
        waiting_for_recipe_loading = false;

        if(deferred_recipe.equals("minecraft:air")) //should be no recipe. this behavior is normal, don't print an error.
        {
            recipe = FermentingRecipe.NONE;
            return true;
        }

        //we do not want an errored identifier to crash-loop and render the whole map useless
        //so we instead set the recipe to NONE if an error occurs (recipe doesn't exist, or is not a fermenting recipe)
        try
        {
            @SuppressWarnings("unchecked") //there is genuinely no better way I found to do it than this. i hate this line of code.
            Optional<FermentingRecipe> opt_rec = (Optional<FermentingRecipe>)(world.getRecipeManager().get(new Identifier(deferred_recipe)));

            recipe = opt_rec.get(); //if this errors, the recipe will just be NONE, because of the existing try block.

            return true;
        }
        catch(Exception e)
        {
            BreakingFemme.LOGGER.error("Error when loading recipe " + deferred_recipe + ":");
            BreakingFemme.LOGGER.error(e.toString());
            recipe = FermentingRecipe.NONE;

            return false;
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory pinv, PlayerEntity player) {
        return new FermenterScreenHandler(syncId, pinv, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }


    //measure environment temperature
    //NOTE: doesn't take into account hot/cold blocks nearby, or if some panels are waterlogged
    //we should be able to waterlog panels to cool down the fermenter... but that should really be heat transfer
    //but it should be in here too: air temp is usually higher than water temp
    //also, day temp higher than night temp... except if doing this in a cave/basement/cellar
    //that actually is a real thing, fermenting beer and wine in a cellar to stabilize the temperature
    //it turns out that the cool and stable temperatures from a cellar are perfect for making beer
    //TODO: come back when multiblock logic done
    public static float environment_temperature(World world, BlockPos pos)
    {
        DimensionType dimension = world.getDimension();
        Biome biome = world.getBiome(pos).value();

        float temperature = biome.getTemperature();
        temperature -= 0.00166667 * (pos.getY() - world.getSeaLevel()); //altitude decrease
        temperature = (temperature - 0.15f) * 20; //convert to celsius

        if(dimension.ultrawarm()) //Nether (and other modded "hot" dimensions)
        {
            temperature += 342; //would be quite a bit hot... probably not amazing to ferment stuff...
        }

        return temperature;
    }
    
    //returns true as a default, if the property doesn't exist (we wouldn't want to add mixers and heaters that are just fancy bottom panels)
    private boolean safeGetBooleanProperty(BlockState state, Property<Boolean> prop)
    {
        try {
            return state.get(prop);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    //returns true if property doesn't exist (full blocks as panels makes more sense than completely non-solid blocks), otherwise true iff property matches wanted
    private boolean safeCheckDirectionProperty(BlockState state, Property<Direction> prop, Direction wanted)
    {
        try {
            return state.get(prop) == wanted;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private void checkMultiblock(BlockPos pos, BlockState state)
    {
        //set up invalid state (can just return if invalid)
        capacity = 0; //0: incorrect multiblock; must be the initial value (in case we fail!)
        volume = 1; //number of contained blocks; can go from 2 (1*2*1 minimum size) to 27 (3*3*3); 1 means invalid.
        int surface_area = 24; //number of panels the barrel is made out of (for a 2*2*2 barrel its 24)
        is_mixing = false; //are there any powered mixers on the bottom (just 1 is enough)
        n_heaters = 0; //number of active heaters
        outside_temp = environment_temperature(world, pos); //measure this anyway (TODO: setting to do a measurement without knowing multiblock size)

        //NOTE: here we are assuming that the multiblock is valid. an inconsistency would prove that it isn't, and we will need to check that an invalid multiblock is always inconsistent
        //step 1: get block behind controller (1 block in the opposite direction of the controller's facing)
        BlockPos insidePos = pos.offset(state.get(FermenterControllerBlock.FACING).getOpposite());

        //step 2: go top and bottom to determine top and bottom height (where there are suitable top/bottom panels); at most 2 blocks (max height 3). These will be where top and bottom panels are, inside the fermenter
        int top = Integer.MIN_VALUE; //error state: top should be strictly more than bottom! (cant have a top and bottom block in the same spot)
        int bottom = Integer.MAX_VALUE;
        for(int i = 0; i < 3; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.Y, i)).isIn(ModBlockTagProvider.FERMENTER_TOP_PANEL))
            {
                top = insidePos.getY() + i;
                break;
            }
        }
        for(int i = 0; i < 3; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.Y, -i)).isIn(ModBlockTagProvider.FERMENTER_BOTTOM_PANEL))
            {
                bottom = insidePos.getY() - i;
                break;
            }
        }
        if(top <= bottom) return; //no top/bottom found! => FAILURE
        if(top - bottom > 2) return; //inconsistent dimensions: fermenter is too tall!

        //step 3: go to the left, right and back to get width and height; at most 3 blocks, these will be on the edges where the side panels are, NOT inside the fermenter.
        //compass: positive x is east, positive z is south
        int plusx = Integer.MIN_VALUE;
        int minusx = Integer.MAX_VALUE;
        int plusz = Integer.MIN_VALUE;
        int minusz = Integer.MAX_VALUE;
        for(int i = 1; i < 4; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.X, i)).isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL))
            {
                plusx = insidePos.getX() + i;
                break;
            }
        }
        for(int i = 1; i < 4; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.X, -i)).isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL))
            {
                minusx = insidePos.getX() - i;
                break;
            }
        }
        for(int i = 1; i < 4; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.Z, i)).isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL))
            {
                plusz = insidePos.getZ() + i;
                break;
            }
        }
        for(int i = 1; i < 4; i++)
        {
            if(world.getBlockState(insidePos.offset(Axis.Z, -i)).isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL))
            {
                minusz = insidePos.getZ() - i;
                break;
            }
        }
        if(plusx <= minusx || plusz <= minusz) return; //almost same as before; here there are only side panels and no separate top/bottom, but the starting position was on the inside.
        if(plusx - minusx > 4 || plusz - minusz > 4) return; //dimensions inconsistent again, here more lenient tho.

        //step 4: check every relevant block for if it is there; in that time, check if mixing and count active heaters
        //step 4.1: checking top and bottom blocks
        boolean no_airlock = true;
        for(int x = minusx + 1; x < plusx; x++)
            for(int z = minusz + 1; z < plusz; z++)
            {
                //check top
                if(!world.getBlockState(new BlockPos(x, top, z)).isIn(ModBlockTagProvider.FERMENTER_TOP_PANEL))
                    return;

                BlockState bottom_state = world.getBlockState(new BlockPos(x, bottom, z));
                if(!bottom_state.isIn(ModBlockTagProvider.FERMENTER_BOTTOM_PANEL))
                    return;
                //counting heaters and checking if there's an active mixer
                else if(bottom_state.isIn(ModBlockTagProvider.FERMENTER_HEATER) && safeGetBooleanProperty(bottom_state, FermenterHeaterBlock.POWERED))
                    n_heaters++;
                else if(!is_mixing && bottom_state.isIn(ModBlockTagProvider.FERMENTER_MIXER) && safeGetBooleanProperty(bottom_state, FermenterMixerBlock.POWERED))
                    is_mixing = true;

                //check airlock
                if(world.getBlockState(new BlockPos(x, top + 1, z)).isIn(ModBlockTagProvider.FERMENTER_AIRLOCK))
                    no_airlock = false;
            }
        if(no_airlock) return;

        //step 4.2: check east (plusx) and west (minusx): fixed x, variable y and z; also there can only be ONE controller block. otherwise its invalid.
        for(int y = bottom; y <= top; y++)
        {
            for(int z = minusz + 1; z < plusz; z++)
            {
                //check east (panels must be facing east!) and west
                BlockState east_state = world.getBlockState(new BlockPos(plusx, y, z));
                if(!east_state.isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL) || !safeCheckDirectionProperty(east_state, FermenterPanelBlock.FACING, Direction.EAST))
                    return;
                BlockState west_state = world.getBlockState(new BlockPos(minusx, y, z));
                if(!west_state.isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL) || !safeCheckDirectionProperty(west_state, FermenterPanelBlock.FACING, Direction.WEST))
                    return;

                //controller block uniqueness
                if(east_state.isOf(ModBlocks.FERMENTER_CONTROLLER) && !pos.equals(new BlockPos(plusx, y, z)))
                    return;
                if(west_state.isOf(ModBlocks.FERMENTER_CONTROLLER) && !pos.equals(new BlockPos(minusx, y, z)))
                    return;
            }
        
            //step 4.3: check south (plusz) and north (minusz): fixed z, variable x and y
            for(int x = minusx + 1; x < plusx; x++)
            {
                BlockState south_state = world.getBlockState(new BlockPos(x, y, plusz));
                if(!south_state.isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL) || !safeCheckDirectionProperty(south_state, FermenterPanelBlock.FACING, Direction.SOUTH))
                    return;
                BlockState north_state = world.getBlockState(new BlockPos(x, y, minusz));
                if(!north_state.isIn(ModBlockTagProvider.FERMENTER_SIDE_PANEL) || !safeCheckDirectionProperty(north_state, FermenterPanelBlock.FACING, Direction.NORTH))
                    return;

                //controller block uniqueness
                if(south_state.isOf(ModBlocks.FERMENTER_CONTROLLER) && !pos.equals(new BlockPos(x, y, plusz)))
                    return;
                if(north_state.isOf(ModBlocks.FERMENTER_CONTROLLER) && !pos.equals(new BlockPos(x, y, minusz)))
                    return;
            }
        }

        //step 4.4: check if the barrel really is hollow
        for(int x = minusx + 1; x < plusx; x++)
            for(int y = bottom + 1; y < top; y++)
                for(int z = minusz + 1; z < plusz; z++)
                    if(!world.getBlockState(new BlockPos(x, y, z)).isAir())
                        return;

        //step 5: compute variables from this state
        int height = top - bottom + 1;
        int widthx = (int)(plusx - minusx - 1);
        int widthy = (int)(plusz - minusz - 1);
        volume = height * widthx * widthy; //whats inside should be an integer due to the checks before
        surface_area = 2 * (height * widthx + height * widthy + widthx * widthy); //is there a way to factor this expression? yes its equal to (sum of dimensions)^2 - sum of (dimensions^2) wait why is it the same formula as the variance for probability distributions. can you do cool stuff with this? also this same formula is found somewhere when using characters, like the decomposition of the tensor product of characters of dimension >1 iirc, but i cant find it right now.
        conductivity = surface_area * 0.91f; //in W / K; coef should be *0.91
        capacity = (volume + 10) / 9;
    }

    private static int global_checking_class = 0;
    private int checking_class = -1; //variable set up to have not all fermenters multiblock check at the same time => more lag friendly
    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        //deferred recipe loading
        if(waiting_for_recipe_loading)
            tryLoadRecipe();

        int capacity_before_check = capacity; //detect if by some miracle (or modded machines, or tick freeze or sth) the capacity changed between 2 checks; it would be more robust to monitor all bounds in checkMultiblock but this should be good enough really
        if(checking_class == -1) //not redundant with the temperature if statement below: that one would not get triggered if temp is already defined!
        {
            checking_class = global_checking_class;
            global_checking_class += MULTIBLOCK_CHECK_STATIC_DELTA;
            if(global_checking_class >= MULTIBLOCK_CHECK_PERIOD) global_checking_class -= MULTIBLOCK_CHECK_PERIOD; //must be representative in [[0; MULTIBLOCK_CHECK_PERIOD - 1]]
            checkMultiblock(pos, state); //on placement of the fermenter it will be checked twice. but that doesn't happen continuously, so its fine.
        }

        //temperature serves as "uninitialized detector"; temp cannot go this low naturally
        if(temperature == -69420.0f)
        {
            checkMultiblock(pos, state);
            temperature = outside_temp; //temp was uninitialized, so set it to outside temp
            markDirty(world, pos, state); //need to save, otherwise temp may get reset to envtemp next time the area is loaded
        }
        else if(world.getTime() % MULTIBLOCK_CHECK_PERIOD == checking_class) //recheck regularly (every 2 seconds)
            checkMultiblock(pos, state);

        if(capacity == 0 || (capacity != capacity_before_check && capacity_before_check != 0)) //multiblock is INCORRECT or changed capacity (and hasn't just been made ofc)! => do not tick
        {
            //don't just obliterate buckets if currently running a recipe, but put them back (as sludge ofc) where they were in the slots
            if(current_stage != STAGE_NOT_IN_USE)
            {
                //WARNING: there can be water buckets in the output slots!! we don't want to just nuke them
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity_before_check; i++)
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), getStack(i));

                //now we're clear to get the fermenter dirty
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity_before_check; i++)
                    setStack(i, new ItemStack(ModFluids.SLUDGE_BUCKET));
            }

            temperature = outside_temp; //bye temperature data! :3
            current_stage = STAGE_NOT_IN_USE; //we ain't cookin' anymore!
            recipe = FermentingRecipe.NONE; //bye recipe!
            markDirty(); //save the data
            return;
        }

        //update temperature
        //discretized differential equation, but time accelerated by a factor of 72 (number of minecraft days in a real day)
        temperature += (n_heaters * 1e4 + conductivity * (outside_temp - temperature)) / (volume * 1.16139e6); //4.181e6 * 20 / 72; 4.181e6 is water heat capacity, 20 for ticks to seconds


        //this is where the fermenter logic goes
        //check if we can start fermentation
        boolean relevant_outputs_have_water = true; //all relevant outputs (not exceeding capacity) need water
        for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
            if (this.getStack(i).getItem() != Items.WATER_BUCKET)
                relevant_outputs_have_water = false;
        if(recipe.isNone() //not already fermenting
            && relevant_outputs_have_water)
        {
            //TODO: optimization: only calling this method if inventory has changed
            //because checking all recipes if they match every single frame could be a problem
            //the more recipes, the laggier each block. kinda bad.
            Optional<FermentingRecipe> opt_rec = optRecipe(); //can we ferment the inputs into the outputs?
            if(opt_rec.isPresent()) //if yes (why does this NOT happen when it should??)
            {
                consumeItems(); //consume input items
                current_stage = 0; //start fermentation
                stage_progress = 0;
                grace_time = 0; //reset potential grace time
                recipe = opt_rec.get(); //set recipe

                //remove relevant water buckets
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                    setStack(i, ItemStack.EMPTY); //the buckets are gone, the result will be served in them so they are not lost

                markDirty(world, pos, state); //save nbt ig? or items?
            }
        }

        //handle the actual fermenting part
        else if(!recipe.isNone()) {
            //handle fermentation progress
            stage_progress++;
            if(recipe.canTransition(current_stage, stage_progress, temperature)) //trying to transition from one stage to the next
            {
                if(current_stage == 4) //finish fermentation
                {
                    //success, so output desired item
                    current_stage = STAGE_NOT_IN_USE;
                    grace_time = 0;

                    //WARNING: there can be water buckets in the output slots!! we don't want to just nuke them
                    for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), getStack(i));

                    //now we're clear
                    for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                        setStack(i, recipe.getOutput(world.getRegistryManager()));
                    recipe = FermentingRecipe.NONE; //reset the recipe AFTER we need it!!!
                }
                else
                {
                    //transition to next stage
                    current_stage++;
                    stage_progress = 0;
                }
                markDirty(world, pos, state);
            }

            if(grace_time > 0)
                grace_time--;
            if(!conditionsMet())
                grace_time += 5; //so it adds 4 in total

            if(grace_time > 4800) //error accumulated to 1 minute
            {
                //fail, so output sludge
                current_stage = STAGE_NOT_IN_USE;
                recipe = FermentingRecipe.NONE;
                grace_time = 0;

                //WARNING: there can be water buckets in the output slots!! we don't want to just nuke them
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), getStack(i));

                //now we're clear to get the fermenter dirty
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                    setStack(i, new ItemStack(ModFluids.SLUDGE_BUCKET));

                markDirty(world, pos, state);
            }
        }
    }

    //remove 1 item in each non-empty slot
    //when calling we know that this is a correct recipe
    private void consumeItems()
    {
        for (int i = 0; i < OUTPUT_SLOT_BEGIN; i++)
            if(!inventory.get(i).isEmpty())
                inventory.get(i).decrement(1);
    }

    private Optional<FermentingRecipe> optRecipe()
    {
        //get first recipe that matches the fermenter's inventory
        return world.getRecipeManager().getFirstMatch(FermentingRecipe.Type.INSTANCE, this, world);
    }
}
