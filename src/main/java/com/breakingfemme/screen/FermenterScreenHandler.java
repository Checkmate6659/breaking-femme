package com.breakingfemme.screen;

import com.breakingfemme.BreakingFemme;
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
            new ArrayPropertyDelegate(25)); //this 25 is for the 25 params
    }

    public FermenterScreenHandler(int syncId, PlayerInventory pinv, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.FERMENTER_SCREEN_HANDLER, syncId);
        checkSize(pinv, 25); //again, 25 for 25 params
        this.inventory = ((Inventory)blockEntity);
        pinv.onOpen(pinv.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (FermenterBlockEntity)blockEntity;

        //slot positions: 4 input and 4 output slots; outputs can accept only water
        //TODO: should we put output slots first? then shift clicking water would always put it in the outputs by priority
        this.addSlot(new Slot(inventory, 0, 38, 34));
        this.addSlot(new Slot(inventory, 1, 56, 34));
        this.addSlot(new Slot(inventory, 2, 38, 52));
        this.addSlot(new Slot(inventory, 3, 56, 52));
        this.addSlot(new FermenterOutputSlot(pinv.player, inventory, 4, 128, 70)); //output slot; in furnace it was FurnaceOutputSlot, here it can only accept water buckets
        this.addSlot(new FermenterOutputSlot(pinv.player, inventory, 5, 128, 52));
        this.addSlot(new FermenterOutputSlot(pinv.player, inventory, 6, 128, 34));
        this.addSlot(new FermenterOutputSlot(pinv.player, inventory, 7, 128, 16));

        addPlayerInventory(pinv);
        addPlayerHotbar(pinv);

        addProperties(arrayPropertyDelegate);
    }

    public int nBarrels() //goes from 1 to 4 normally, 0 when incorrect multiblock
    {
        return propertyDelegate.get(0);
    }

    public boolean isProcessing()
    {
        return propertyDelegate.get(1) != FermenterBlockEntity.STAGE_NOT_IN_USE; //1 is current fermentation stage
    }

    public int getThermometerHeight() //get which pixel the thermometer should stop at, excluding rounded top
    {
        int temperature = propertyDelegate.get(24); //2^24 times temp in celsius
        BreakingFemme.LOGGER.info("temp: " + (float)temperature / (16777216.0f));
        temperature = 70 - (temperature >> 25); //70 if 0°C, 20 if 100°C (so each pixel is 2°C)
        if(temperature < 17) temperature = 17; //top of the thermometer, slightly above 100°C
        else if(temperature > 72) temperature = 72; //slightly below 0°C
        return temperature;
    }

    public int getBubbleColor(int stage) //24 different colors, 0 white, 1 brown, 2 gray, 3 black etc., 24 is empty (transparent picture)
    {
        //preliminary cases
        if(stage < 0 || stage >= 5) return 24;
        if(propertyDelegate.get(1) == FermenterBlockEntity.STAGE_NOT_IN_USE) return 24;
        if(propertyDelegate.get(1) < stage) return 24;
        
        //if current stage: pick between white bubble (conditions satisfied) or gray (not satisfied)
        int color = 0; //white bubble by default
        if(stage == propertyDelegate.get(1))
        {
            int temperature = propertyDelegate.get(24); //2^24 times temp in celsius
            int lbound = propertyDelegate.get(13 + stage);
            int ubound = propertyDelegate.get(18 + stage);
            if(temperature < lbound || temperature > ubound) //incorrect temperature: show brown bubble
                color = 1;
        }
        else //successfully completed stage: show color corresponding to middle temp of lower/upper bounds
        {
            //these are 2^24 times the lower/upper temperatures in degrees.
            int lbound = propertyDelegate.get(13 + stage);
            int ubound = propertyDelegate.get(18 + stage);
            int middle = (lbound + ubound + 16777216) >> 25; //this is in degrees celsius, the +16777216 is for rounding (becomes 1/2 after shift)
            if(middle > 100) middle = 100; //max shown is 100°C
            else if(middle < 9) middle = 9; //min is 0-9°C interval, so we set it to 9°C
            //color logic: 0-9°C is highest (22), >=100°C is lowest (2), even numbers only; no mixing yet
            color = ((119 - middle) / 10) * 2;
            //TODO: add mixing requirement (or not), which adds 1 to color variable (darker bubbles)
        }

        return color;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    //TODO: is this right? we may want to not add stuff to unavailable slots
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
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 92 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 150));
        }
    }
}
