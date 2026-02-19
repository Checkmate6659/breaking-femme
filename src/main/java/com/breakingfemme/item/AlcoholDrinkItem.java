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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
        if (user instanceof ServerPlayerEntity player) { //only do it on the server
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));

            //add to ethanol level
            //https://wires.onlinelibrary.wiley.com/doi/epdf/10.1002/wfs2.1340, paragraph 4
            float amount = switch(LEVEL)
            {
                case 0 -> 12.8f; //in grams
                case 1 -> 80.0f;
                case 2 -> 160.0f;
                case 3 -> 240.0f;
                default -> 0f; //invalid level
            };
            KineticsAttachments.incLevel(player, KineticsAttachments.BUFFERED_ETHANOL, amount);
            KineticsAttachments.syncClientValues(player);
        }

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

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
