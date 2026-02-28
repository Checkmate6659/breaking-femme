package com.breakingfemme;

import com.breakingfemme.item.ModItems;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class VillagerAttachments {
    public static final AttachmentType<Integer> IS_TRANSFEM = AttachmentRegistry.createPersistent( //is villager transfem? (should be a boolean but that gets forgotten, so we do this instead)
        Identifier.of(BreakingFemme.MOD_ID, "is_transfem"), Codec.intRange(0, 255));
    
    //TODO: add villagers that turn into the agents from the matrix and try to kill you
 
    public static final int MAX_ESTRO_PROGRESS = 4095; //this really is the amount of time the villager needs to transition
    public static final AttachmentType<Integer> ESTRO_PROGRESS = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
        Identifier.of(BreakingFemme.MOD_ID, "estro_progress"), Codec.intRange(0, MAX_ESTRO_PROGRESS));
    public static final AttachmentType<Integer> ESTRO_NEED_AGE = AttachmentRegistry.createPersistent( //age when needing estrogen again
        Identifier.of(BreakingFemme.MOD_ID, "estro_need_age"), Codec.intRange(0, Integer.MAX_VALUE));

    //check if an item is an estrogen (for villagers)
    public static boolean isEstrogen(Item item)
    {
        //TODO: mb move to another method, mb use estrogenTime instead
        return item.equals(ModItems.PURE_ESTRADIOL_POWDER);
    }

    public static int estrogenTime(Item item)
    {
        //TODO: change! mb move to another method
        if(item.equals(ModItems.PURE_ESTRADIOL_POWDER))
            return 100; //just 5 seconds; TODO: change!! and add more different estrogens!

        return 0;
    }

    public static boolean isTransfem(VillagerEntity villager)
	{
        //need to set when queried, otherwise it would be a different random pick afterwards!
        //also, the transfem-ness status of a villager isnt changeable after being set like this
		return villager.getAttachedOrSet(IS_TRANSFEM, villager.getWorld().getRandom().nextInt(256)) < 254; //this adjusts the probability of being transfem
	}

    public static int getTransitionTime(VillagerEntity villager)
    {
        //ESTRO_PROGRESS: progress after this dose has been consumed
        //ESTRO_NEED_AGE - age: time until the dose is consumed (is 0 if villager needs estrogen)
        int time_to_next = villager.getAttachedOrSet(ESTRO_NEED_AGE, villager.age) - villager.age;
        if(time_to_next < 0) time_to_next = 0;
        return villager.getAttachedOrSet(ESTRO_PROGRESS, 0) - time_to_next;
    }

    public static boolean needsEstrogen(VillagerEntity villager)
    {
        //NOTE: this is NOT equivalent to just doing age >= estro need age. cuz these ages will overflow at some point!
        //the age variable will overflow after 3 real-life years of being loaded (or less with other mods that can accelerate time)
        int delta = villager.age - villager.getAttachedOrSet(ESTRO_NEED_AGE, villager.age);
        if(delta >= Integer.MAX_VALUE / 4) //once every 4.5 months, do sth to stop the villagers from not needing estrogen for 1.5 years
            villager.setAttached(ESTRO_NEED_AGE, villager.age);
        return delta >= 0;
    }

    public static void giveEstrogenFor(VillagerEntity villager, int amount)
    {
        //add to progress
        int estro_progress = villager.getAttachedOrElse(ESTRO_PROGRESS, 0);
        estro_progress += amount;
        if(estro_progress > MAX_ESTRO_PROGRESS) estro_progress = MAX_ESTRO_PROGRESS;
        villager.setAttached(ESTRO_PROGRESS, estro_progress);

        //add to time of next dose
        villager.setAttached(ESTRO_NEED_AGE, villager.age + amount);
    }
}
