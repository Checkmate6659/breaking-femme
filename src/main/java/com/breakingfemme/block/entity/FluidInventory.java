package com.breakingfemme.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

//TODO: check out net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage, for mod compat
@FunctionalInterface
public interface FluidInventory extends Inventory {
	DefaultedList<Pair<FlowableFluid, Integer>> getFluids();

	@Override
	default public int size() {
		return getFluids().size();
	}

	@Override
	default public boolean isEmpty() {
		for(Pair<FlowableFluid, Integer> fluid : getFluids())
			if(fluid.getRight() > 0)
				return false;
		return true;
	}

	@Override
	default public void markDirty() {} //override if we want behavior (listeners etc)

	@Override
	default public void clear() {
		getFluids().clear();
	}

	default public Pair<FlowableFluid, Integer> getFluid(int i)
	{
		return getFluids().get(i);
	}

	//method for an inventory that contains no items
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
	default public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}
}
