package com.breakingfemme.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AzaleaBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class CopperSulfateItem extends BoneMealItem {
    public CopperSulfateItem(Settings settings) {
        super(settings);
    }

    @Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		if (useOnFertilizable(context.getStack(), world, blockPos) && !world.isClient) {
            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);
			return ActionResult.success(world.isClient);
		}
        return ActionResult.PASS;
	}

	private static boolean isTree(Block block)
	{
		return (block instanceof SaplingBlock) || (block instanceof AzaleaBlock);
	}

	public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
		if (!isTree(world.getBlockState(pos).getBlock()))
            return false;
        return BoneMealItem.useOnFertilizable(stack, world, pos);
	}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("item.breakingfemme.copper_sulfate.tooltip"));
	}
}
