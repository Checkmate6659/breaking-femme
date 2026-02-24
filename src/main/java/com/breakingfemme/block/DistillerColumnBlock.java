package com.breakingfemme.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

            gravelFall(state.with(FULL, true), world, pos); //make gravel fall down

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public void gravelFall(BlockState state, World world, BlockPos pos)
    {
        if(!state.get(FULL)) //if empty column, don't do anything.
            return;

        BlockPos cur_pos = pos.down();
        BlockState cur_state;
        cur_state = world.getBlockState(cur_pos);
        if((!cur_state.isOf(ModBlocks.DISTILLER_COLUMN) || cur_state.get(FULL)) && !cur_state.isAir()) //cannot empty and refill afterwards, as that would make block updates!
            return;

        world.setBlockState(pos, state.with(FULL, false)); //empty it out (WILL NOT refill later)

        while(cur_state.isOf(ModBlocks.DISTILLER_COLUMN) && !cur_state.get(FULL)) //stop when not on an empty column
        {
            cur_pos = cur_pos.down();
            cur_state = world.getBlockState(cur_pos);
        }

        if(cur_state.isAir()) //emptying out above air
        {
            ItemEntity itemEntity = new ItemEntity(world, cur_pos.getX() + 0.5, cur_pos.getY() + 1.25, cur_pos.getZ() + 0.5, new ItemStack(Blocks.GRAVEL));
            itemEntity.setVelocity(0, 0, 0);
            world.spawnEntity(itemEntity);
        }
        else //just land normally
            world.setBlockState(cur_pos.up(), this.getDefaultState().with(FULL, true));
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && state.get(FULL))
            gravelFall(state, world, pos);
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
