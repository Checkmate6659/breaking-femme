package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.block.entity.FunnelBlockEntity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//block entity tutorial
//https://www.youtube.com/watch?v=Y4dK9ETdZCQ
public class FunnelBlock extends BlockWithEntity {
    protected static final VoxelShape SHAPE;

    public FunnelBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    //Block entity code
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FunnelBlockEntity(pos, state);
    }

    //what to do when block broken: just drop all items inside the block entity's inventory
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FunnelBlockEntity) {
                ItemScatterer.spawn(world, pos, (FunnelBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FunnelBlockEntity funnel) {
            ItemStack stack = player.getStackInHand(hand);
            if(stack.isEmpty()) //clicking with an empty hand
            {
                //pick up a stack from the funnel: output if it exists, otherwise filter
                ItemStack removed = funnel.removeStack(0); //remove output
                if(removed.isEmpty()) //no output :(
                    removed = funnel.removeStack(1); //remove the filter
                
                player.setStackInHand(hand, removed);

                return ActionResult.SUCCESS;
            }
            else if(funnel.canInsert(1, stack, hit.getSide())) //if we can insert item (ie if its a filter)
            {
                //insert the item into the funnel: swap funnel inventory with hand inventory
                player.setStackInHand(hand, funnel.removeStack(1));
                funnel.setStack(1, stack);

                return ActionResult.SUCCESS;
            }
            
            world.updateComparators(pos,this);
        }

        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FUNNEL_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
    
    static {
        //quite a complex shape
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1, 14, 1, 15, 16, 15),
            Block.createCuboidShape(2, 12, 2, 14, 14, 14),
            Block.createCuboidShape(3, 9, 3, 13, 12, 13),
            Block.createCuboidShape(4, 6, 4, 12, 9, 12),
            Block.createCuboidShape(5, 3, 5, 11, 6, 11),
            Block.createCuboidShape(6, 0, 6, 10, 3, 10)
        );
    }
}
