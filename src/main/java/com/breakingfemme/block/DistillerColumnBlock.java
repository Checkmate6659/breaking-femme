package com.breakingfemme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DistillerColumnBlock extends Block {
    public static final BooleanProperty FULL = BooleanProperty.of("full");
    private static final VoxelShape PIPE_SHAPE, FULL_PIPE_SHAPE;

    public DistillerColumnBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FULL, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FULL);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FULL) ? FULL_PIPE_SHAPE : PIPE_SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if(world.isClient())
            return ActionResult.SUCCESS;

        if(!state.get(FULL) && player.getStackInHand(hand).isOf(Blocks.GRAVEL.asItem())) //place gravel in the column
        {
            player.getStackInHand(hand).decrement(1);
            world.setBlockState(pos, state.with(FULL, true));
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 1, 1, true);

            //TODO: make gravel fall to the bottom of the column (and as item if cant do that)

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    static {
        PIPE_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 4.0),
            Block.createCuboidShape(2.0, 0.0, 12.0, 14.0, 16.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 4.0, 16.0, 14.0),
            Block.createCuboidShape(12.0, 0.0, 2.0, 14.0, 16.0, 14.0)
        );

        FULL_PIPE_SHAPE = VoxelShapes.union(PIPE_SHAPE, Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0));
    }
}
