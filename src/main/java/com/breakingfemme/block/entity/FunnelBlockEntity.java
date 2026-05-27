package com.breakingfemme.block.entity;

import java.util.Iterator;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.ModSounds;
import com.breakingfemme.datagen.ModFluidTagProvider;
import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.recipe.FilteringRecipe;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FunnelBlockEntity extends BlockEntity implements ImplementedInventory {
    //need a 2 slot inventory: first slot is output, second slot is filter
    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private Optional<FilteringRecipe> cur_recipe = Optional.empty();
    private String last_valid_recipe = "";

    private int timer = 0; //recipe takes time!
    private long initial_topq = 0; //at the beginning of the time, this value will be set to currently extractible fluid from the top storage. no more than this can get processed.
    private long item_counter = 0; //counter for giving less than 1 item/droplet
    private long filter_counter = -Long.MAX_VALUE / 2; //counter for using less than 1 filter/droplet; start at this value to make filter use an extra durability when new fluid inputted

    private static final int SOUND_LENGTH = 18; //sound should be played every 18 ticks
    private long sound_modulus = -1;

    public FunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FUNNEL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        //can only insert into second (filter) slot. not the output slot. but you *can* take out the filters. so be careful.
        return slot == 1 && stack.isIn(ModItemTagProvider.FILTER);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory); //save inventory
        nbt.putInt("timer", timer);
        nbt.putLong("initial_topq", initial_topq);
        nbt.putLong("item_counter", item_counter);
        nbt.putLong("filter_counter", filter_counter);
        nbt.putString("recipe", last_valid_recipe);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory); //load inventory
        timer = nbt.getInt("timer");
        initial_topq = nbt.getLong("initial_topq");
        item_counter = nbt.getLong("item_counter");
        filter_counter = nbt.getLong("filter_counter");
        last_valid_recipe = nbt.getString("recipe");
    }

    private boolean isFluidHarshOnFilter(Storage<FluidVariant> storage)
    {
        Iterator<StorageView<FluidVariant>> iter = storage.nonEmptyIterator(); //skip over empties by default
        while(iter.hasNext())
            if(iter.next().getResource().getFluid().isIn(ModFluidTagProvider.HARSH_ON_FILTERS))
                return true; //exception-free/extra variable-free way to get out of the loop: throwing exceptions is costly (building stack trace), especially when done often
        return false;
    }

    //try to consume a bit of filter.
    //return false if the filter slot was empty (and so it failed), true otherwise.
    public boolean consumeFilter(int damage)
    {
        ItemStack filter = getStack(1);
        if(filter.isEmpty()) return false;

        if(filter.isDamageable() && filter.getDamage() + damage <= filter.getMaxDamage())
            filter.damage(damage, world.random, null);
        else
            filter.decrement(damage);

        markDirty();
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 0);
        return true;
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        if(sound_modulus == -1) sound_modulus = world.random.nextInt(SOUND_LENGTH); //initialization: shift stuff not to make jarring repetition when there are multiple filters

        //TODO: check if harsh fluid & flimsy filter. if yes, destroy it and make fluid go straight through!
        //does not count if theres a filter durability thats currently getting used up.
        ItemStack filterStack = getStack(1);
        if(filter_counter <= 0)
        {
            //the funnel got no filter left. literally.
            if(filterStack.isEmpty())
            {
                //send all fluid straight through, at a max rate of 1 bucket/tick
                return;
            }

            Storage<FluidVariant> top_storage = FluidStorage.SIDED.find(world, pos.up(), Direction.DOWN);
            if(top_storage != null && !filterStack.isIn(ModItemTagProvider.RESISTANT_FILTER) && isFluidHarshOnFilter(top_storage)) //filter gets destroyed
            {
                consumeFilter(1); //damage filter very quickly: a stack of paper gone in like 3 seconds
                if(world.getRandom().nextInt(6) == 0) //TODO: better sound effect than this crap
                    world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS);

                return; //don't do any real filtering after that...
            }
        }

        //there actually is an acceptable filter!

        ItemStack outputStack = getStack(0);
        if(outputStack.getCount() == outputStack.getMaxCount()) return; //filter is clogged!

        //TODO: do static method in FilteringRecipe to check if it is even possible to get a match (are there fluid invs/cauldrons above/below)
        if(FilteringRecipe.storagesMissing(pos, world)) return;

        //TODO: do this wayyyyyyyyyy less often!!! like just try{keep going} catch(what) {check new recipe; ...}
        Optional<FilteringRecipe> matched_recipe = world.getRecipeManager().getFirstMatch(FilteringRecipe.Type.INSTANCE, this, world);
        //TODO: if recipe interrupted then changes back to THE SAME as before, then don't do this.
        if(!matched_recipe.equals(cur_recipe)) //changed recipe
        {
            cur_recipe = matched_recipe;
            if(cur_recipe.isPresent() && !cur_recipe.get().getId().toString().equals(last_valid_recipe))
            {
                last_valid_recipe = cur_recipe.get().getId().toString();

                item_counter = 0; //no freebies!
                filter_counter = -Long.MAX_VALUE / 2; //in case filter is partially used. then next inputted fluid will use it on first filtering step.

                FilteringRecipe recipe = cur_recipe.get();
                long packed = recipe.getTimeAndInitialTopq(this, world);
                timer = (int)(packed >> 32);
                initial_topq = (int)(packed & 4294967295L);
            }
            else
                initial_topq = 0; //how does this fix anything really? it shouldnt. but it DOES. so lets keep it.
        }

        //TODO: if cannot run recipe (like theres no recipe), then just fucking pass the fluid through slowly but without changing it and damaging the filter pretty slowly. except if harsh fluid ofc...
        if(cur_recipe.isEmpty()) return; //no recipe to run :(

        FilteringRecipe recipe = cur_recipe.get();
        long droplets_per_item = recipe.dropletsPerItem();
        long droplets_per_filter = recipe.dropletsPerFilter();

        //output clogged by some other item => no.
        if(!outputStack.isEmpty() && !ItemStack.canCombine(outputStack, recipe.getOutput(world.getRegistryManager()))) return;

        //funny idea: could supply the system with "low pressure air" fluid to do vacuum filtration
        //but you would need a bit of a setup for that
        //would it be *slightly* worse for the filters? idk...

        //do actual filtering!!
        //fluids & filter
        if(timer <= 0)
        {
            //insert/extract fluids
            long topq = recipe.extractFromTop(pos.up(), world, initial_topq); //why this 0
            long bottomq = topq * recipe.outputq / recipe.inputq;
            long actualq = recipe.insertIntoBottom(pos.down(), world, bottomq);

            //damage filter
            filter_counter -= actualq;
            item_counter += actualq; //update the item counter here too
            if(filter_counter <= 0)
            {
                if(consumeFilter(1))
                {
                    if(filter_counter < -Long.MAX_VALUE / 4) filter_counter = -actualq; //"initialization"
                    filter_counter += droplets_per_filter;
                }
            }

            //reset variables
            long packed = recipe.getTimeAndInitialTopq(this, world);
            timer = (int)(packed >> 32);
            initial_topq = (int)(packed & 4294967295L);

            markDirty();
            world.updateListeners(pos, state, state, 0);
        }
        timer--; //don't forget that!!

        //output item; we know at this point that we can output the item.
        if(item_counter >= droplets_per_item)
        {
            int ntimes = (int)(item_counter / droplets_per_item);
            ItemStack stack = recipe.getOutput(world.getRegistryManager());
            int count = stack.getCount();

            if(outputStack.isEmpty())
            {
                if(ntimes * count > 64) ntimes = 64 / count; //don't make more than a stack
                setStack(0, stack.copyWithCount(ntimes * count));
            }
            else
            {
                int space_left = 64 - outputStack.getCount();
                if(ntimes * count > space_left) ntimes = space_left / count;
                outputStack.increment(ntimes * count); //outputStack is a reference!
            }

            item_counter -= droplets_per_item * ntimes;

            markDirty();
            world.updateListeners(pos, state, state, 0);
        }

        //play sound effect while filtering
        if(world.getTime() % SOUND_LENGTH == sound_modulus)
            world.playSound(null, pos, ModSounds.FILTER, SoundCategory.BLOCKS);
    }
}
