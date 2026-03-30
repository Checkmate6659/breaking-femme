package com.breakingfemme.block;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.entity.DistillerBlockEntity;
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
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//block entity tutorial
//https://www.youtube.com/watch?v=Y4dK9ETdZCQ
public class DistillerBaseBlock extends BlockWithEntity {
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
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new DistillerBlockEntity(pos, state);
    }

    //what to do when block broken: spill fluid inside; place down a full block if full
    @Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);

        if (blockEntity instanceof DistillerBlockEntity distiller) {
            //spill fluid if not empty
            Pair<FlowableFluid, Integer> fluid = distiller.getFluid(0);
            int level = fluid.getRight();
            if(level > 0)
                BreakingFemme.spillFluid(world, pos, fluid.getLeft(), 8 - (level * 8) / 81000);

            //reset comparator output
            world.updateComparators(pos,this);
        }
    }

    //comparator output (from furnace)
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof DistillerBlockEntity blockEntity)
        {
            int level = blockEntity.getFluid(0).getRight();
            if(level == 0) //completely empty => don't send out anything.
                return 0;
            return 1 + (level * 14) / 81000;
        }
        return 0; //fallback (should never be reached)
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DISTILLER_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
    
    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 0, 0, 2, 16, 16),
            Block.createCuboidShape(14, 0, 0, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 0, 16, 15, 16)
        );
    }
}
