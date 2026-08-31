package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class PressHandleBlockEntity extends BlockEntity {
    private float angle_normalized = 0; //goes 0-1 in a full turn (TODO: ticking & rendering!)

    public PressHandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_HANDLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    //TODO: ticker and renderer
}
