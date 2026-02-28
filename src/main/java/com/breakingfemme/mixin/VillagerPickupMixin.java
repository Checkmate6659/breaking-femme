package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.VillagerAttachments;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(VillagerEntity.class)
public class VillagerPickupMixin {
    @Inject(at = @At("RETURN"), method = "canGather", cancellable = true)
	private void breakingfemme$canGrabGirlPotion(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        if(VillagerAttachments.isEstrogen(stack.getItem()) && VillagerAttachments.isTransfem(villager))
            cir.setReturnValue(true);
    }

    //in loot method, using ItemEntity.getOwner(), give some cash to the player who dropped it... if a player did, and is close enough
    //mb the closest player if theres a player close enough otherwise? idk.

    /*@Inject(at = @At("HEAD"), method = "mobTick")
    private void breakingfemme$buyGirlPotion(CallbackInfo ci)
    {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        SimpleInventory inventory = villager.getInventory();
        int size = inventory.size();

        int estro_slot; //if this is equal to size at the end, we did not find any estrogen
        ItemStack stack = inventory.getStack(0); //because java says this isnt initialized if i dont initialize it here. wtf java just let me do stuff that i know is right but you dont.
        for(estro_slot = 0; estro_slot < size; estro_slot++)
        {
            stack = inventory.getStack(estro_slot);
            if(VillagerAttachments.isEstrogen(stack.getItem())) //we found a slot!
                break;
        }

        //consume an estrogen if possible, and increase the estrogen number
        if(estro_slot < size)
        {
            stack.decrement(1);
            inventory.setStack(estro_slot, stack);
            VillagerAttachments.addProgress(villager, 1); //TODO: different amounts for different estrogens
        }
    }*/
}
