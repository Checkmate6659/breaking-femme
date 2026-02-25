package com.breakingfemme.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class FlammableBucketItem extends BucketItem {
    private final Fluid this_fluid;

    public FlammableBucketItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
        this_fluid = fluid;
    }

    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean bl = blockState.canBucketPlace(this_fluid);
        boolean bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable)block).canFillWithFluid(world, pos, blockState, this_fluid);

        if(bl2 && world.getDimension().ultrawarm()) //we want to place it in the nether (or in a hot dimension): it fucking EXPLODES (ethanol vapors can spontaneously ignite at about 370°C)
        {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2.0f, true, ExplosionSourceType.BLOCK);
            return true;
        }

        return super.placeFluid(player, world, pos, hitResult);
    }
}
