package com.breakingfemme.item;

import com.breakingfemme.KineticsAttachments;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class EgelItem extends Item {
    public EgelItem(Item.Settings settings) {
        super(settings);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity player) { //only do it on the server
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));

            //TODO: add to arm-skin-buffered estrogen levels (need different buffering buffering)
        }

        //instead of just using up the entire bottle at once, just give it some durability
        stack.damage(1, user, player -> {}); //that callback method is what happens when it breaks
        if (stack.isEmpty()) //when breaking, get back a glass bottle instead
            return new ItemStack(Items.GLASS_BOTTLE);
        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 80;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
