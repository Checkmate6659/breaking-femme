package com.breakingfemme.block.entity;

import java.util.Optional;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class FermenterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);
    public static final int STAGE_NOT_IN_USE = 5;
    private static final int OUTPUT_SLOT_BEGIN = 4; //output slots must be right after all input slots

    protected final PropertyDelegate propertyDelegate; //the PropertyDelegate is used for client<->server syncing (i mean here its really more like server->client, the only client->server is the inventory)

    private boolean waiting_for_recipe_loading = false;
    private String deferred_recipe = "minecraft:air"; //this is because when calling readNbt, the world is null, so cannot get its recipe manager immediately

    private int capacity = 1; //0 for incorrect multiblock, 1 for smallest, 4 to largest; TODO: should be floor((number of enclosed tiles + 10) / 9)
    private int current_stage = STAGE_NOT_IN_USE; //the fermentation's current stage (always goes from 0 to 4 when in use, 5 ie STAGE_NOT_IN_USE when not in use)
    private int stage_progress = 0; //progress of current stage, in ticks
    private FermentingRecipe recipe = FermentingRecipe.NONE; //this should be NONE only when the fermenter is not in use
    private float temperature = -69420.0f; //in °C; initial value is for initialization to environment temperature
    private boolean is_mixing = false; //will be derived from multiblock checking
    private int grace_time = 0; //accumulated time with wrong conditions. adds 4 if wrong, removes 1 if right, min is 0. if exceeds a certain value, then the recipe fails.

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
    
    private void checkMultiblock()
    {
        //TODO: implement proper multiblock check
        //TODO: interrupt recipe if multiblock size changed (it may be possible, using bugs, to change barrel size without getting caught)
        //could also be possible if the multiblock check isn't done every tick (would probably be too laggy)

        temperature = environment_temperature(world, pos); //initial temp is that of environment
        //actually, this should happen as soon as water buckets added in.
        //like the buckets in the output slots should raise the heat capacity/carry energy
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        //deferred recipe loading
        if(waiting_for_recipe_loading)
            tryLoadRecipe();

        //temperature serves as "uninitialized detector"; temp cannot go this low naturally
        if(temperature == -69420.0f)
        {
            checkMultiblock();
            markDirty(world, pos, state); //need to save, otherwise temp may get reset to envtemp next time the area is loaded
        }

        //update temperature
        //TODO: move some of this stuff to multiblock check/private variables!
        float outside_temp = environment_temperature(world, pos);
        int n_heaters = 0; //number of active heaters (TODO: count them in multiblock check)
        int volume = 8; //number of contained blocks
        int surface_area = 24; //number of panels the barrel is made out of (for a 2*2*2 barrel its 24) (TODO: calculate)
        float conductivity = surface_area * 0.91f; //in W / K; coef should be *0.91

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
