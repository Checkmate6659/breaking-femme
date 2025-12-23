package com.breakingfemme.fluid;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class Et64Fluid extends FlowableFluid {
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

    public Optional<SoundEvent> getBucketFillSound() {
       return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView bview, BlockPos pos, Fluid fluid, Direction direction)
    {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    @Override
    public Fluid getStill()
    {
        return ModFluids.STILL_ET64;
    }

    @Override
    public Fluid getFlowing()
    {
        return ModFluids.FLOWING_ET64;
    }

    @Override
    public Item getBucketItem()
    {
        return ModFluids.ET64_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state)
    {
        return ModFluids.ET64_FLUID_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state)
    {
        return false;
    }

    public static class Flowing extends Et64Fluid
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

    public static class Still extends Et64Fluid
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
