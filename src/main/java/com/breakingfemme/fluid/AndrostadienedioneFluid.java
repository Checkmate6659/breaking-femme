package com.breakingfemme.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AndrostadienedioneFluid extends FlowableFluid {
    @Override
    protected boolean isInfinite(World world)
    {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
    {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    public boolean matchesType(Fluid fluid)
    {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    protected int getFlowSpeed(WorldView world)
    {
        return 4;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world)
    {
        return 1;
    }

    @Override
    public int getLevel(FluidState state)
    {
        return 8;
    }

    @Override
    public int getTickRate(WorldView world)
    {
        return 5;
    }

    @Override
    protected float getBlastResistance()
    {
        return 100f;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView bview, BlockPos pos, Fluid fluid, Direction direction)
    {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    @Override
    public Fluid getStill()
    {
        return ModFluids.STILL_ANDROSTADIENEDIONE;
    }

    @Override
    public Fluid getFlowing()
    {
        return ModFluids.FLOWING_ANDROSTADIENEDIONE;
    }

    @Override
    public Item getBucketItem()
    {
        return ModFluids.ANDROSTADIENEDIONE_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state)
    {
        return ModFluids.ANDROSTADIENEDIONE_FLUID_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state)
    {
        return false;
    }

    public static class Flowing extends AndrostadienedioneFluid
    {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state)
        {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state)
        {
            return false;
        }
    }

    public static class Still extends AndrostadienedioneFluid
    {
        @Override
        public int getLevel(FluidState state)
        {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state)
        {
            return true;
        }
    }
}
