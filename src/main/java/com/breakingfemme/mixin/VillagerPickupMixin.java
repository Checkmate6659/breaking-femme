package com.breakingfemme.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.VillagerAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(VillagerEntity.class)
public class VillagerPickupMixin {
    @WrapOperation(at = @At(value = "INVOKE:FIRST", target = "contains"), method = "canGather")
	private boolean breakingfemme$canGrabGirlPotion(Set<Item> set, Object item, Operation<Boolean> operation) {
        VillagerEntity villager = (VillagerEntity)(Object)this;
        return (VillagerAttachments.isTransfem(villager) && VillagerAttachments.isEstrogen((Item)item))
            || operation.call(set, item);
    }

    //in loot method, using ItemEntity.getOwner(), give some cash to the player who dropped it... if a player did, and is close enough
    //mb the closest player if theres a player close enough otherwise? idk.

    @Inject(at = @At("HEAD"), method = "mobTick")
    private void breakingfemme$useGirlPotion(CallbackInfo ci)
    {
        //TODO: take time when using the estrogen

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
            //this small bit works... sure.
            stack.decrement(1);
            inventory.setStack(estro_slot, stack);
            VillagerAttachments.addProgress(villager, 1); //TODO: different amounts for different estrogens

            //happy particles!
            villager.getWorld().sendEntityStatus(villager, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
        }
    }
}
