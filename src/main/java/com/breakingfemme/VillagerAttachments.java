package com.breakingfemme;

import com.breakingfemme.item.ModItems;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class VillagerAttachments {
    public static final AttachmentType<Boolean> IS_TRANSFEM = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
        Identifier.of(BreakingFemme.MOD_ID, "is_transfem"), Codec.BOOL);
    
    //TODO: add villagers that turn into the agents from the matrix and try to kill you
 
    public static final int MAX_ESTRO_PROGRESS = 4095;
    public static final AttachmentType<Integer> ESTRO_PROGRESS = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
        Identifier.of(BreakingFemme.MOD_ID, "estro_progress"), Codec.intRange(0, MAX_ESTRO_PROGRESS));

    //check if an item is an estrogen (for villagers)
    public static boolean isEstrogen(Item item)
    {
        //TODO: change!
        return item.equals(ModItems.PURE_ESTRADIOL_POWDER);
    }

    public static boolean isTransfem(VillagerEntity villager)
	{
        //need to set when queried, otherwise it would be a different random pick afterwards!
        //also, the transfem-ness status of a villager isnt changeable after being set like this
		return villager.getAttachedOrSet(IS_TRANSFEM, villager.getWorld().getRandom().nextBoolean());
	}

    public static void addProgress(VillagerEntity villager, int amount)
    {
        int estro_progress = villager.getAttachedOrElse(ESTRO_PROGRESS, 0);
        estro_progress += amount;
        if(estro_progress > MAX_ESTRO_PROGRESS) estro_progress = MAX_ESTRO_PROGRESS;
        villager.setAttached(ESTRO_PROGRESS, estro_progress);
    }
}
