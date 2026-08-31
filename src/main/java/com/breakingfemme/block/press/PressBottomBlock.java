package com.breakingfemme.block.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.block.entity.press.PressBottomBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PressBottomBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_NORTH, SHAPE_EAST, SHAPE_SOUTH, SHAPE_WEST;

    public PressBottomBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    //2 functions from HorizontalFacingBlock
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        blockState = blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        return blockState;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING))
        {
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_NORTH;
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressBottomBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    static {
        SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(4, 0, 15, 12, 10, 16), //back plate when facing north
            Block.createCuboidShape(5, 14, 10, 11, 15, 11), //ring bit missing on south
            Block.createCuboidShape(10, 14, 5, 11, 15, 11), //ring bit missing on east
            Block.createCuboidShape(5, 14, 5, 6, 15, 11), //ring bit missing on west
            Block.createCuboidShape(6, 14, 5, 7, 15, 6), //ring tooth 1 (north)
            Block.createCuboidShape(9, 14, 5, 10, 15, 6), //ring tooth 2 (north)
            Block.createCuboidShape(1, 0, 1, 15, 1, 15) //bed
        );

        SHAPE_EAST = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 4, 1, 10, 12), //back plate when facing east
            Block.createCuboidShape(5, 14, 10, 11, 15, 11), //ring bit missing on south
            Block.createCuboidShape(5, 14, 5, 11, 15, 6), //ring bit missing on north
            Block.createCuboidShape(5, 14, 5, 6, 15, 11), //ring bit missing on west
            Block.createCuboidShape(10, 14, 6, 11, 15, 7), //ring tooth 1 (east)
            Block.createCuboidShape(10, 14, 9, 11, 15, 10), //ring tooth 2 (east)
            Block.createCuboidShape(1, 0, 1, 15, 1, 15) //bed
        );
    
        SHAPE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(4, 0, 0, 12, 10, 1), //back plate when facing south
            Block.createCuboidShape(10, 14, 5, 11, 15, 11), //ring bit missing on east
            Block.createCuboidShape(5, 14, 5, 11, 15, 6), //ring bit missing on north
            Block.createCuboidShape(5, 14, 5, 6, 15, 11), //ring bit missing on west
            Block.createCuboidShape(6, 14, 10, 7, 15, 11), //ring tooth 1 (south)
            Block.createCuboidShape(9, 14, 10, 10, 15, 11), //ring tooth 2 (south)
            Block.createCuboidShape(1, 0, 1, 15, 1, 15) //bed
        );

        SHAPE_WEST = VoxelShapes.union(
            Block.createCuboidShape(15, 0, 4, 16, 10, 12), //back plate when facing west
            Block.createCuboidShape(5, 14, 10, 11, 15, 11), //ring bit missing on south
            Block.createCuboidShape(10, 14, 5, 11, 15, 11), //ring bit missing on east
            Block.createCuboidShape(5, 14, 5, 11, 15, 6), //ring bit missing on north
            Block.createCuboidShape(5, 14, 6, 6, 15, 7), //ring tooth 1 (west)
            Block.createCuboidShape(5, 14, 9, 6, 15, 10), //ring tooth 2 (west)
            Block.createCuboidShape(1, 0, 1, 15, 1, 15) //bed
        );
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory inv) {
                ItemScatterer.spawn(world, pos, inv);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.PRESS_BOTTOM_BLOCK_ENTITY, ((world1, pos, state1, blockEntity) -> blockEntity.getTicker().tick(world, pos, state, blockEntity)));
    }
}
