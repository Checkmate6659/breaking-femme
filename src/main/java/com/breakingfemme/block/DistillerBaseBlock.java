package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.block.entity.FermenterBlockEntity;
import com.breakingfemme.block.entity.ModBlockEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//block entity tutorial
//https://www.youtube.com/watch?v=Y4dK9ETdZCQ
public class DistillerBaseBlock extends Block /*extends BlockWithEntity*/ {
    static final VoxelShape SHAPE;

    public DistillerBaseBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    //Block entity code
    /*@Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FermenterBlockEntity(pos, state);
    }

    //what to do when block broken: just drop all items inside the block entity's inventory
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FermenterBlockEntity) {
                ItemScatterer.spawn(world, pos, (FermenterBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((FermenterBlockEntity)world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }*/

    //comparator output (from furnace)
    /*
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
    */

    //how is the validateTicker method not existing
    //i need to implement a custom class extending BlockEntityTicker?
    //default mc block entities dont even have that lol, they have this checkType shit but idk
    //theres FurnaceBlock.getTicker -> AbstractFurnaceBlock.checkType -> BlockWithEntity.checkType
    //so ig validateTicker got replaced by checkType
    /*@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FERMENTER_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }*/
    
    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 0, 0, 2, 16, 16),
            Block.createCuboidShape(14, 0, 0, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 0, 16, 2, 16)
        );
    }
}
