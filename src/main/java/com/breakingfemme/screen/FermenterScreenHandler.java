package com.breakingfemme.screen;

import com.breakingfemme.block.entity.FermenterBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FermenterScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final FermenterBlockEntity blockEntity;

    public FermenterScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf)
    {
        this(syncId, inv, inv.player.getWorld().getBlockEntity(buf.readBlockPos()),
            new ArrayPropertyDelegate(2)); //this 2 is for the 2 params progress and maxProgress (TODO: WILL CHANGE LATER)
    }

    public FermenterScreenHandler(int syncId, PlayerInventory pinv, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.FERMENTER_SCREEN_HANDLER, syncId);
        checkSize(pinv, 2); //again, 2 for 2 params
        this.inventory = ((Inventory)blockEntity);
        pinv.onOpen(pinv.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (FermenterBlockEntity)blockEntity;

        this.addSlot(new Slot(inventory, 0, 38, 26));
        this.addSlot(new Slot(inventory, 1, 56, 26));
        this.addSlot(new Slot(inventory, 2, 38, 44));
        this.addSlot(new Slot(inventory, 3, 56, 44));
        this.addSlot(new OutputSlot(pinv.player, inventory, 4, 116, 35)); //output slot; in furnace it was FurnaceOutputSlot

        addPlayerInventory(pinv);
        addPlayerHotbar(pinv);

        addProperties(arrayPropertyDelegate);
    }

    public boolean isCooking()
    {
        return propertyDelegate.get(0) > 0;
    }

    public float getScaledProgress()
    {
        if(propertyDelegate.get(1) <= 0) return 0f; //just to avoid division by 0 errors
        return (float)(propertyDelegate.get(0)) / (float)(propertyDelegate.get(1)); //go from 0 to 1
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) { //slot is in the machine inv
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                //slot ids start at 9: can't have more than 9 slots in the machine
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
