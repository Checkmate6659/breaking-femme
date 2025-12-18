package com.breakingfemme.item;

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

public class AlcoholDrinkItem extends Item {
    private final int LEVEL;
    public AlcoholDrinkItem(Item.Settings settings, int level) {
        //Level:
        //0: 5%-ish ig? (beer, drinking an entire bottle of this should be noticing some effects)
        //1: 32% (basically vodka... drinking an entire bottle of vodka definitely shouldn't be good)
        //2: 64% (basically disinfectant, fire does 5 hearts of damage with no armor/regen, effect should make it worse)
        //3: 95% (basically solvent, fire kills even from full health+sat with no armor)
        super(settings);
        LEVEL = level;
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        //add drunk status effect here

        //set player on fire if too much alcohol
        if(LEVEL == 2) //level 2 or 3
            user.setOnFireFor(10);
        if(LEVEL == 3)
            user.setOnFireFor(30);

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (user instanceof PlayerEntity && !((PlayerEntity)user).getAbilities().creativeMode) {
                ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerEntity = (PlayerEntity)user;
                if (!playerEntity.getInventory().insertStack(itemStack)) {
                playerEntity.dropItem(itemStack, false);
                }
            }

            return stack;
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    /*public SoundEvent getDrinkSound() {
        return SoundEvents.DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.POTION_;
    }*/

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
