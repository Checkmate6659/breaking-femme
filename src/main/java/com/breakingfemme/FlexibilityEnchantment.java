package com.breakingfemme;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import static com.breakingfemme.ModTags.Item.ALREADY_FLEXIBLE;

public class FlexibilityEnchantment extends Enchantment {
    public FlexibilityEnchantment(Rarity weight, EquipmentSlot... slotTypes)
    {
        super(weight, EnchantmentTarget.ARMOR_CHEST, slotTypes);
    }

    @Override
	public int getMinPower(int level) {
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		return 75;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return !stack.isIn(ALREADY_FLEXIBLE) && super.isAcceptableItem(stack);
	}
}
