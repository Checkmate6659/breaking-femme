package com.breakingfemme.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class FermenterOutputSlot extends Slot {
    private final PlayerEntity player;
    private int amount;

    public FermenterOutputSlot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    public boolean canInsert(ItemStack stack) { //only water can be inserted
        return stack.getItem() == Items.WATER_BUCKET;
    }

    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }

        return super.takeStack(amount);
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        super.onTakeItem(player, stack);
    }

    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    protected void onCrafted(ItemStack stack) {
        stack.onCraft(this.player.getWorld(), this.player, this.amount);
        this.amount = 0;
    }
}
