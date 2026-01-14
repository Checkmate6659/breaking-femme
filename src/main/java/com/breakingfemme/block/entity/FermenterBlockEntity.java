package com.breakingfemme.block.entity;

import java.util.List;
import java.util.Optional;

import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.screen.FermenterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
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

    protected final PropertyDelegate propertyDelegate; //the PropertyDelegate is used for client<->server syncing

    //TODO: do custom recipe format in jsons
    private int capacity = 1; //0 for incorrect multiblock, 1 for smallest, 4 to largest; TODO: should be floor((number of enclosed tiles + 10) / 9)
    private int current_stage = STAGE_NOT_IN_USE; //the fermentation's current stage (always goes from 0 to 4 when in use, 5 ie STAGE_NOT_IN_USE when not in use)
    private int stage_progress = 0; //progress of current stage, in ticks
    private int min_progress[] = { 0, 0, 0, 0, 0 }; //min progress before stage complete
    private int max_progress[] = { 0, 0, 0, 0, 0 }; //max progress before failure
    private float min_temp[] = { 0, 0, 0, 0, 0 }; //temperature bounds
    private float max_temp[] = { 0, 0, 0, 0, 0 };
    private Item result_item = Items.COBBLESTONE; //crappy failsafe. at least we know if something is wrong. (TODO: write this in mod docs) (TODO: actually write docs)
    private float temperature = -69420.0f; //in °C; initial value is for initialization to environment temperature
    private int grace_time = 0; //accumulated time with wrong conditions. adds 4 if wrong, removes 1 if right, min is 0. if exceeds a certain value, then the recipe fails.
    //NOTE: maybe grace time won't need to be in there? idk yet tho.
    //also, MIXING should be in the property delegate. as its required for bubble colors.

    public FermenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 0)
                    return capacity;
                else if(index == 1)
                    return current_stage;
                else if(index == 2)
                    return stage_progress;
                else if(index < 8)
                    return min_progress[index - 3];
                else if(index < 13)
                    return max_progress[index - 8];
                else if(index < 18)
                    return Math.round(Math.min(min_temp[index - 13], 127.42f) * 16777216f); //*2^24 to not lose too much precision, but temp shall never exceed 128°C (even 100°C) so no issues there
                else if(index < 23)
                    return Math.round(Math.min(max_temp[index - 18], 127.42f) * 16777216f);
                else if(index == 23)
                    return grace_time;
                else if(index == 24)
                    return Math.round(Math.min(temperature, 127.42f) * 16777216f);
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
                else if(index < 8)
                    min_progress[index - 3] = value;
                else if(index < 13)
                    max_progress[index - 8] = value;
                else if(index < 18)
                    min_temp[index - 13] = ((float)value) * 5.960464477539063e-08f; //*2^-24;
                else if(index < 23)
                    max_temp[index - 18] = ((float)value) * 5.960464477539063e-08f;
                else if(index == 23)
                    grace_time = value;
                else if(index == 24)
                    temperature = ((float)value) * 5.960464477539063e-08f;
            }

            @Override
            public int size() {
                return 25; //number of values to synchronize
            }
        };
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
        Identifier id = Registries.ITEM.getId(result_item);
        nbt.putString("result_item", id == null ? "minecraft:air" : id.toString());; //save this non-property-delegate property
        Inventories.writeNbt(nbt, inventory);
        for (int i = 0; i < propertyDelegate.size(); i++) //HACK: generic/lazy way to do this, but makes no sense in game. TODO: implement this differently.
            nbt.putInt("fermenter.p" + String.valueOf(i), propertyDelegate.get(i));
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        result_item = (Item)Registries.ITEM.get(new Identifier(nbt.getString("result_item")));
        Inventories.readNbt(nbt, inventory);
        for (int i = 0; i < propertyDelegate.size(); i++)
            propertyDelegate.set(i, nbt.getInt("fermenter.p" + String.valueOf(i)));
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
    
    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;
        if(temperature == -69420.0f) //uninitialized temperature should be initialized to environment; cannot be done in constructor since world doesnt exist
        {
            temperature = environment_temperature(world, pos); //initial temp is that of environment
            markDirty(world, pos, state); //need to save, otherwise temp may get reset to envtemp next time the area is loaded
        }

        //update temperature
        float outside_temp = environment_temperature(world, pos);
        int n_heaters = 0; //number of active heaters (TODO: count them)
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
        if(current_stage == STAGE_NOT_IN_USE //not already fermenting
            && relevant_outputs_have_water)
        {
            Optional<Item> result_option = resultItem(); //can we ferment the inputs into the outputs?
            if(result_option.isPresent()) //if yes, the ingredients have already been removed
            {
                result_item = result_option.get();
                current_stage = 0;

                //remove relevant water buckets
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                    setStack(i, ItemStack.EMPTY); //the buckets are gone, the result will be served in them so they are not lost

                markDirty(world, pos, state); //save nbt ig? or items?
            }
        }

        //handle the actual fermenting part
        else if(current_stage != STAGE_NOT_IN_USE){
            //handle fermentation progress
            boolean conditions_wrong = false;
            stage_progress++;
            if(stage_progress >= min_progress[current_stage]) //trying to transition from one stage to the next
            {
                if(stage_progress > max_progress[current_stage]) //failed because waited for too long
                {
                    conditions_wrong = true;
                }
                else if(current_stage + 1 == STAGE_NOT_IN_USE)
                {
                    //success, so output desired item
                    current_stage = STAGE_NOT_IN_USE;
                    for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                        setStack(i, new ItemStack(result_item));

                    markDirty(world, pos, state);
                    return;
                }
                else if(temperature >= min_temp[current_stage + 1] && temperature <= max_temp[current_stage + 1])
                {
                    //transition to next stage
                    current_stage++;
                    stage_progress = 0;
                }
            }

            //if temperature not in the right bounds, then fail
            //TODO: fail if mixed incorrectly (not mixed when it should be or backwards)
            //TODO: add some leniency... especially for the mixing
            if(temperature < min_temp[current_stage] || temperature > max_temp[current_stage])
            {
                conditions_wrong = true;
            }

            if(grace_time > 0)
                grace_time--;
            if(conditions_wrong) //in wrong conditions
                grace_time += 5;

            if(grace_time > 4800) //error accumulated to 1 minute
            {
                //fail, so output sludge
                current_stage = STAGE_NOT_IN_USE;                
                for(int i = OUTPUT_SLOT_BEGIN; i < OUTPUT_SLOT_BEGIN + capacity; i++)
                    setStack(i, new ItemStack(ModFluids.SLUDGE_BUCKET));

                markDirty(world, pos, state);
            }
        }
    }

    //check if inputs contain all required items in the required quantity (ie capacity)
    //cannot run larger fermenter with less items as a smaller one btw
    //supposes all items in the list are unique (will only be checked on load, as it is slower)
    private boolean containsItems(List<Item> ingredients)
    {
        for (Item item : ingredients)
            if(!hasItemInAmount(item, capacity))
                return false;
        return true;
    }

    //remove those items from there
    //supposes all items in the list are unique (will only be checked on load, as it is slower)
    private void removeItems(List<Item> ingredients)
    {
        for (Item item : ingredients)
            removeItems(item, capacity);
    }

    private Optional<Item> resultItem()
    {
        //hardcoded recipes for now

        //sugar + wheat -> beer bucket

        //sugar + nether wart + wheat -> nether beer bucket

        //sugar + milkgot + sterols + nickel sulfate -> androstadienedione bucket (consumed bucket? fix that later)
        //mb replace water bucket with sugar? no issue with buckets then too
        List<Item> list = List.of(
            Items.SUGAR,
            ModItems.MILKGOT,
            ModItems.STEROLS,
            ModItems.NICKEL_SULFATE
        );

        //need to do a loop at some point lol
        if(containsItems(list))
        {
            //remove items from input slots
            removeItems(list);

            //set time and temperature constraints
            //this is why jsons > hardcoding. and compatibility too.
            min_progress[0] = 35; //middleground: 4 mc hours or 3min20 irl
            min_progress[1] = 60; //5 mc days ie 1h40 irl; just split up in 2 for visualization
            min_progress[2] = 60;
            min_progress[3] = 1; //get enough time to heat up to sterilization temp
            min_progress[4] = 30; //3 mc hours
            /*min_progress[0] = 3500; //middleground: 4 mc hours or 3min20 irl
            min_progress[1] = 60000; //5 mc days ie 1h40 irl; just split up in 2 for visualization
            min_progress[2] = 60000;
            min_progress[3] = 1; //get enough time to heat up to sterilization temp
            min_progress[4] = 3000;*/ //3 mc hours
            max_progress[0] = 4500; //adding half a mc hour of tolerance afterwards
            max_progress[1] = 60001;
            max_progress[2] = 72000; //adding half a mc day of tolerance
            max_progress[3] = 6000; //yeah just heat up the mixture. you have 6 hours.
            max_progress[4] = 3001; //really this doesnt matter too much as long as its >3000.
            min_temp[0] = 57.0f; //selection step 57-64°C
            min_temp[1] = 25.0f; //fermentation 25-50°C
            min_temp[2] = 25.0f;
            min_temp[3] = 50.0f; //transition towards sterilization
            min_temp[4] = 100.0f; //sterilization
            max_temp[0] = 64.0f; //selection
            max_temp[1] = 55.0f;
            max_temp[2] = 55.0f;
            max_temp[3] = 120.0f; //doesnt matter too much, just to get the red color bubble lol
            max_temp[4] = 69420.0f; //temp as high as we can, this is higher than any reachable temp

            //return result item
            return Optional.of(ModFluids.ANDROSTADIENEDIONE_BUCKET);
        }

        return Optional.empty();
    }
}
