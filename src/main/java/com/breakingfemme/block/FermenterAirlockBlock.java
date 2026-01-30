package com.breakingfemme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FermenterAirlockBlock extends Block {
    public FermenterAirlockBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
            Block.createCuboidShape(7, -2,  7,  9,  0,  9),
            Block.createCuboidShape(8,  0,  7,  9,  4,  8),
            Block.createCuboidShape(8,  3,  6,  9,  4,  7),
            Block.createCuboidShape(8, -2,  5,  9,  4,  6),
            Block.createCuboidShape(8, -2,  4,  9, -1,  5),
            Block.createCuboidShape(8, -2,  3,  9,  3,  4),
            Block.createCuboidShape(8,  3,  3,  9,  4,  4)
        );
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(ModBlocks.FERMENTER_TOP);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}
