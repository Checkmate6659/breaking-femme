package com.breakingfemme.block.press;

import com.breakingfemme.block.entity.press.PressBottomBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PressBottomBlock extends BlockWithEntity {
    public PressBottomBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressBottomBlockEntity(pos, state);
    }
}
