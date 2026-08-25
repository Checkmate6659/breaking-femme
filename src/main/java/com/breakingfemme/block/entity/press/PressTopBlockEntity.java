package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class PressTopBlockEntity extends BlockEntity {
    public PressTopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_TOP_BLOCK_ENTITY, pos, state);
    }
}
