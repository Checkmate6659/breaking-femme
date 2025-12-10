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

public class SolutionBucketItem extends BucketItem {
    private final Fluid this_fluid;
    private final Item substance;

    public SolutionBucketItem(Fluid fluid, Item dissolved_substance, Settings settings) {
        super(fluid, settings);
        this_fluid = fluid;
        substance = dissolved_substance;
    }

    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean bl = blockState.canBucketPlace(this_fluid);
        boolean bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable)block).canFillWithFluid(world, pos, blockState, this_fluid);

        if(bl2 && world.getDimension().ultrawarm()) //evaporation
        {
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.125f, pos.getZ() + 0.5f, new ItemStack(substance)));
        }

        return super.placeFluid(player, world, pos, hitResult);
    }
}
