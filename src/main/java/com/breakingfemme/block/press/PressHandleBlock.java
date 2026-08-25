package com.breakingfemme.block.press;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import org.jetbrains.annotations.Nullable;

public class PressHandleBlock extends WallMountedBlock {
    public PressHandleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = super.getPlacementState(ctx);
        if (state == null) return null;
        return switch (state.get(FACE)) { // we don't allow ourselves to face up or down
            case FLOOR, CEILING -> null;
            default -> state;
        };
    }
}
