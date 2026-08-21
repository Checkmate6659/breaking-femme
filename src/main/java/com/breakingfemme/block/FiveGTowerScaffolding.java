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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.stream.Stream;

public class FiveGTowerScaffolding extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final VoxelShape[] OUTLINE_SHAPES = {
            Stream.of(
                    Block.createCuboidShape(0, 0, 0, 3, 16, 3),
                    Block.createCuboidShape(13, 0, 0, 16, 16, 3),
                    Block.createCuboidShape(0, 0, 13, 3, 16, 16),
                    Block.createCuboidShape(13, 0, 13, 16, 16, 16),
                    Block.createCuboidShape(3, 3, 1, 13, 14, 2),
                    Block.createCuboidShape(3, 3, 14, 13, 14, 15),
                    Block.createCuboidShape(14, 3, 3, 15, 14, 13),
                    Block.createCuboidShape(1, 3, 3, 2, 14, 13)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(0, 0, 13, 16, 3, 16),
                    Block.createCuboidShape(0, 13, 13, 16, 16, 16),
                    Block.createCuboidShape(0, 0, 0, 16, 3, 3),
                    Block.createCuboidShape(0, 13, 0, 16, 16, 3),
                    Block.createCuboidShape(3, 3, 14, 14, 13, 15),
                    Block.createCuboidShape(3, 3, 1, 14, 13, 2),
                    Block.createCuboidShape(3, 14, 3, 14, 15, 13),
                    Block.createCuboidShape(3, 1, 3, 14, 2, 13)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(13, 0, 0, 16, 3, 16),
                    Block.createCuboidShape(13, 13, 0, 16, 16, 16),
                    Block.createCuboidShape(0, 0, 0, 3, 3, 16),
                    Block.createCuboidShape(0, 13, 0, 3, 16, 16),
                    Block.createCuboidShape(14, 3, 2, 15, 13, 13),
                    Block.createCuboidShape(1, 3, 2, 2, 13, 13),
                    Block.createCuboidShape(3, 14, 2, 13, 15, 13),
                    Block.createCuboidShape(3, 1, 2, 13, 2, 13)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get()
    };
    public FiveGTowerScaffolding(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(AXIS, Direction.Axis.Y));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(AXIS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private static VoxelShape getShape(BlockState state) {
        return switch (state.get(AXIS)) {
            case X -> OUTLINE_SHAPES[1];
            case Z -> OUTLINE_SHAPES[2];
            default -> OUTLINE_SHAPES[0];
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> switch (state.get(AXIS)) {
                case X -> state.with(AXIS, Direction.Axis.Z);
                case Z -> state.with(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    
}
