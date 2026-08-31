package com.breakingfemme.block.press;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.ModBlocks;
import com.breakingfemme.block.entity.press.PressHandleBlockEntity;

public class PressHandleBlock extends BlockWithEntity { //TODO: BlockWithEntity! (block entity required to make the model spin)
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public PressHandleBlock(Settings settings) {
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

    //TODO: make it so that it orients itself to the correct orientation nearest to placement angle, if possible
    //(at a maximum, pick any of the two closest directions)
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
  
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        //need block at the *left* to be a press top block, facing the same direction
        BlockState lstate = world.getBlockState(pos.offset(state.get(FACING).rotateClockwise(Axis.Y)));
        return lstate.isOf(ModBlocks.PRESS_TOP) && lstate.get(FACING).equals(state.get(FACING));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressHandleBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    //TODO: ticker
    /*@Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.PRESS_HANDLE_BLOCK_ENTITY, ((world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state, blockEntity)));
    }*/

    //TODO: shape
}
