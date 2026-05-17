package com.breakingfemme.block.entity;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.datagen.ModItemTagProvider;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FunnelBlockEntity extends BlockEntity implements ImplementedInventory {
    //need a 2 slot inventory: first slot is output, second slot is filter
    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public FunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_TOP_BLOCK_ENTITY, pos, state);
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
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory); //load inventory
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        //funny idea: could supply the system with "low pressure air" fluid to do vacuum filtration
        //but you would need a bit of a setup for that
        //would it be *slightly* worse for the filters

        //TODO: do recipes! with a little timer and shit?
        
        //this is a TEST!!
        //markDirty();
        //world.updateListeners(pos, state, state, 0); //WARNING: LAGGY!!!!!!
    }
}
