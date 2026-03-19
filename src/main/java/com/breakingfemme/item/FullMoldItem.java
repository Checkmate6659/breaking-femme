package com.breakingfemme.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.SpeedVectorAccessor;
import com.breakingfemme.fluid.ModFluids;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FullMoldItem extends Item {
    public FullMoldItem(Settings settings) {
        super(settings);
    }

    @Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.breakingfemme.unstable.tooltip"));
	}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        if(world.isClient()) return;
        if(entity instanceof LivingEntity livingEntity)
        {
            double threshold = (((SpeedVectorAccessor)livingEntity).breakingfemme$getSpeedVector().lengthSquared() - 0.01); //speed limit for being sure not to spill is 0.1, between walking speed (0.21) and sneaking speed (about 0.06)
            if(world.getRandom().nextDouble() < threshold)
            {
                BreakingFemme.spillFluid(world, entity.getBlockPos(), ModFluids.FLOWING_SPILLAGE, 6);
                if(entity instanceof PlayerEntity playerEntity)
                {
                    stack.decrement(1); //we can spill one by one
                    playerEntity.giveItemStack(new ItemStack(ModItems.INGOT_MOLD));
                }
                else //TODO: find a case where this actually happens!!! chest minecarts? no. even donkeys don't call it.
                    stack = new ItemStack(ModItems.INGOT_MOLD, stack.getCount()); //have to settle with this otherwise: all molds get spilled
            }
        }
    }
}
