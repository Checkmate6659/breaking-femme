package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.block.entity.DistillerTopBlockEntity;
import com.breakingfemme.block.entity.ModBlockEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DistillerTopBlock extends BlockWithEntity {
    //TODO: make a property saying which fluid does it have inside it
    //TODO: make this into a thing compatible with the fabric fluid API
    private static final VoxelShape SHAPE;

    public DistillerTopBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new DistillerTopBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        return ActionResult.PASS;
    }

    //TODO: add methods afterBreak, hasComparatorOutput and getComparatorOutput here

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DISTILLER_TOP_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
    
    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 11.0, 4.0),
            Block.createCuboidShape(2.0, 0.0, 12.0, 14.0, 11.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 4.0, 11.0, 14.0),
            Block.createCuboidShape(12.0, 0.0, 2.0, 14.0, 11.0, 14.0),
            Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 16.0, 14.0),
            Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0)
        );
    }
}
