package com.breakingfemme.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface EmptyInventory extends Inventory {
	@Override
	default public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	default public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	default public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	default public void setStack(int slot, ItemStack stack) {
		this.markDirty();
	}

	@Override
	default public int size() {
		return 0;
	}

	@Override
	default public boolean isEmpty() {
		return true;
	}

	@Override
	default public void markDirty() {} //nothing to save!

	@Override
	default public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	default public void clear() {}
}
