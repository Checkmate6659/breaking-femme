package com.breakingfemme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.stream.Stream;

public class FiveGTowerHead extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape OUTLINE_SHAPE = Stream.of(
            Block.createCuboidShape(0, 0, 13, 3, 16, 16),
            Block.createCuboidShape(13, 0, 13, 16, 16, 16),
            Block.createCuboidShape(13, 0, 0, 16, 16, 3),
            Block.createCuboidShape(0, 0, 0, 3, 16, 3),
            Stream.of(
                    Block.createCuboidShape(3, 3, 1, 13, 14, 2),
                    Block.createCuboidShape(3, 3, 14, 13, 14, 15),
                    Block.createCuboidShape(14, 3, 3, 15, 14, 13),
                    Block.createCuboidShape(1, 3, 3, 2, 14, 13)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get()
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public FiveGTowerHead(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED)
                .add(HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(HORIZONTAL_FACING, Direction.fromHorizontal(ctx.getWorld().random.nextBetween(0, 3)));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> state.with(HORIZONTAL_FACING, state.get(HORIZONTAL_FACING).rotateYClockwise());

            case COUNTERCLOCKWISE_90 ->
                    state.with(HORIZONTAL_FACING, state.get(HORIZONTAL_FACING).rotateYCounterclockwise());

            default -> state;
        };
    }
}
