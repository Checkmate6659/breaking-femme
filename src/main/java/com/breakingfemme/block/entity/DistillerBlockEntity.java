package com.breakingfemme.block.entity;

import com.breakingfemme.BreakingFemme;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistillerBlockEntity extends BlockEntity { //this one doesn't have an inventory (just fluids), and doesn't have a screen
    public DistillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISTILLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) //saving data from ingame to save
    {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) //loading data from save to ingame
    {
        super.readNbt(nbt);
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if(world.isClient()) return;

        if(world.getTime() % 20 == 0)
            BreakingFemme.LOGGER.info("distiller base ticks at " + pos + " in state " + state);

        if(false) //if need to save again ig
            markDirty(world, pos, state);
    }
}
