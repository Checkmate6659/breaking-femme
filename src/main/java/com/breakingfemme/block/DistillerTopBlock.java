package com.breakingfemme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DistillerTopBlock extends Block {
    //TODO: make a property saying which fluid does it have inside it
    //TODO: make this into a thing compatible with the fabric fluid API
    private static final VoxelShape SHAPE;

    public DistillerTopBlock(Settings settings) {
        super(settings);
        //this.setDefaultState(this.stateManager.getDefaultState().with(FULL, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        //TODO: add said property inside here!
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
