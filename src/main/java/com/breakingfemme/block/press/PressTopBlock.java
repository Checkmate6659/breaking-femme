package com.breakingfemme.block.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.block.entity.press.PressTopBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import com.breakingfemme.registries.press.PressHead;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import org.jetbrains.annotations.Nullable;
public class PressTopBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = PressBottomBlock.FACING; //must be strictly identical to the property of PressBottomBlock

    public PressTopBlock(Settings settings) {
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
        BlockPos pos = ctx.getBlockPos();
        WorldView world = ctx.getWorld();
        BlockState bottom_state = world.getBlockState(pos.down());

        if(!bottom_state.isOf(ModBlocks.PRESS_BOTTOM)) //shouldnt even be able to place it actually in this case. so whatever.
            return this.getDefaultState();

        return this.getDefaultState().with(FACING, bottom_state.get(FACING));
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(ModBlocks.PRESS_BOTTOM);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        double y_offset = 0;
        //if(world.getBlockEntity(pos) instanceof PressTopBlockEntity)
        //    y_offset = (world.getBlockEntity(pos).getWorld().getTime() % 20) * 0.2; //TEST
        return Block.createCuboidShape(6, y_offset - 7.5, 6, 10, y_offset + 8.5, 10);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressTopBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            /// empty behavior
            if (world instanceof ServerWorld server) {
                var entity = server.getBlockEntity(pos, ModBlockEntities.PRESS_TOP_BLOCK_ENTITY).orElseThrow();
                if (!entity.isEmpty()) {
                    playRemoveSound(world, pos);
                    player.setStackInHand(hand, entity.removeStack());
                }
            }
            return ActionResult.success(world.isClient());
        }
        var head = PressHead.getPressHead(stack).orElse(null);
        if (head != null) {
            /// has a head behavior ,TLDR: if we don't have a head we just grab one head from the player's hand.
            /// if we do have a head but the player is only holding one head we just swap them.
            /// if we can combine the stacks we currently have with the one the player is holding then we just do that.
            if (world instanceof ServerWorld server) {
                var entity = server.getBlockEntity(pos, ModBlockEntities.PRESS_TOP_BLOCK_ENTITY).orElseThrow();
                if (entity.isEmpty()) {
                    player.setStackInHand(hand, stack.copyWithCount(stack.getCount() - 1));
                    entity.setStack(stack);
                    playInsertSound(world, pos);
                } else {
                    if (ItemStack.canCombine(stack, entity.getStack()) && stack.getCount() < stack.getMaxCount()) {
                        var removedStack = entity.removeStack(0, 1);
                        player.getStackInHand(hand).increment(removedStack.getCount());
                        playRemoveSound(world, pos);
                    } else if (stack.getCount() == 1) {
                        var removedStack = entity.removeStack();
                        player.setStackInHand(hand, removedStack);
                        entity.setStack(stack);
                        playRemoveSound(world, pos);
                        playInsertSound(world, pos);
                    }
                }
            }

            return ActionResult.success(world.isClient());
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    private static void playInsertSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private static void playRemoveSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
