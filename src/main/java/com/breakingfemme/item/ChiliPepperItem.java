package com.breakingfemme.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.breakingfemme.block.ModBlocks;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ChiliPepperItem extends AliasedBlockItem {
    private final boolean HAS_TOOLTIP, PLANTABLE;
    public ChiliPepperItem(Item.Settings settings, boolean has_tooltip, boolean plantable) {
        super(ModBlocks.CHILI_CROP, settings);
        HAS_TOOLTIP = has_tooltip;
        PLANTABLE = plantable;
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) { //only do it on the server
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        //set player on fire
        //TODO: interaction with estrogen
        user.setOnFireFor(user.getWorld().getDimension().ultrawarm() ? 15 : 5);

        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
        if(HAS_TOOLTIP)
            tooltip.add(Text.translatable("item.breakingfemme.ground_chili_pepper.tooltip"));
	}

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    //plant if PLANTABLE
    @Override
	public ActionResult place(ItemPlacementContext context) {
        if(!PLANTABLE) return ActionResult.FAIL;
        return super.place(context);
    }
}
