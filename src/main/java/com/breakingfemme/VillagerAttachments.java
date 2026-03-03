package com.breakingfemme;

import com.breakingfemme.item.ModItems;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class VillagerAttachments {
    public static final AttachmentType<Boolean> IS_TRANSFEM = AttachmentRegistry.createPersistent( //is villager transfem? (should be a boolean but that gets forgotten, so we do this instead)
        Identifier.of(BreakingFemme.MOD_ID, "is_transfem"), Codec.BOOL);
    
    //TODO: add villagers that turn into the agents from the matrix and try to kill you
 
    public static final int MAX_ESTRO_PROGRESS = 288000; //this really is the amount of time the villager needs to transition: about 3h 40min irl
    public static final AttachmentType<Integer> ESTRO_PROGRESS = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
        Identifier.of(BreakingFemme.MOD_ID, "estro_progress"), Codec.intRange(0, MAX_ESTRO_PROGRESS));
    public static final AttachmentType<Long> ESTRO_NEED_TIME = AttachmentRegistry.createPersistent( //age when needing estrogen again
        Identifier.of(BreakingFemme.MOD_ID, "estro_need_time"), Codec.LONG);

    //if i dont do this, attachments wont exist before a villager gets ticked, i.e. during deserialization of the world
    public static void registerAttachments()
    {
        //
    }
    
    //check if an item is an estrogen (for villagers)
    public static boolean isEstrogen(Item item)
    {
        //TODO: mb move to another method, mb use estrogenTime instead
        return item.equals(ModItems.PURE_ESTRADIOL_POWDER);
    }

    //time of activity of an estrogen
    public static int estrogenTime(Item item)
    {
        //TODO: change! mb move to another method
        if(item.equals(ModItems.PURE_ESTRADIOL_POWDER))
            return 100; //just 5 seconds; TODO: change!! and add more different estrogens!

        return 0;
    }

    //get value of an estrogen item in emeralds
    public static int getValue(Item item)
    {
        //TODO: change! mb move to another method
        if(item.equals(ModItems.PURE_ESTRADIOL_POWDER))
            return 1;

        return 0;
    }

    public static boolean isTransfem(VillagerEntity villager)
	{
        //need to set when queried, otherwise it would be a different random pick afterwards!
        //also, the transfem-ness status of a villager isnt changeable after being set like this
        if(!villager.hasAttached(IS_TRANSFEM))
        {
            boolean is_transfem = villager.getWorld().getRandom().nextInt(256) < 254; //this adjusts the probability of being transfem (TODO: set it to sth not absurdly high after it works)
            villager.setAttached(IS_TRANSFEM, is_transfem);
        }
		return villager.getAttached(IS_TRANSFEM);
	}

    public static int getTransitionTime(VillagerEntity villager)
    {
        //ESTRO_PROGRESS: progress after this dose has been consumed
        //ESTRO_NEED_AGE - age: time until the dose is consumed (is 0 if villager needs estrogen)
        World world = villager.getWorld();
        long time_to_next = villager.getAttachedOrSet(ESTRO_NEED_TIME, world.getTime()) - world.getTime(); //should be in int values realistically
        if(time_to_next < 0) time_to_next = 0;
        return villager.getAttachedOrSet(ESTRO_PROGRESS, 0) - (int)time_to_next;
    }

    public static boolean needsEstrogen(VillagerEntity villager)
    {
        World world = villager.getWorld();
        return world.getTime() >= villager.getAttachedOrSet(ESTRO_NEED_TIME, world.getTime());
    }

    public static void giveEstrogenFor(VillagerEntity villager, int amount)
    {
        //add to progress
        int estro_progress = villager.getAttachedOrElse(ESTRO_PROGRESS, 0);
        estro_progress += amount;
        if(estro_progress > MAX_ESTRO_PROGRESS) estro_progress = MAX_ESTRO_PROGRESS;
        villager.setAttached(ESTRO_PROGRESS, estro_progress);

        //add to time of next dose
        villager.setAttached(ESTRO_NEED_TIME, villager.getWorld().getTime() + amount);
    }
}
