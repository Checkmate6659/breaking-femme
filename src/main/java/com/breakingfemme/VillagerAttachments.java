package com.breakingfemme;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.breakingfemme.item.ModItems;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class VillagerAttachments {
    public static final AttachmentType<Boolean> IS_TRANSFEM = AttachmentRegistry.createPersistent( //is villager transfem?
        Identifier.of(BreakingFemme.MOD_ID, "is_transfem"), Codec.BOOL);
    public static final AttachmentType<Boolean> IS_NAMEFLUID = AttachmentRegistry.createPersistent( //is villager namefluid? (changes name every once in a while)
        Identifier.of(BreakingFemme.MOD_ID, "is_namefluid"), Codec.BOOL);
    
    //TODO: add villagers that turn into the agents from the matrix and try to kill you
 
    public static final AttachmentType<String> NAME = AttachmentRegistry.createPersistent( //custom name of the villager (if it doesnt exist, it doesnt have one)
        Identifier.of(BreakingFemme.MOD_ID, "name"), Codec.STRING);
    public static final int MAX_ESTRO_PROGRESS = 288000; //this really is the amount of time the villager needs to transition: about 3h 40min irl
    public static final AttachmentType<Integer> ESTRO_PROGRESS = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
        Identifier.of(BreakingFemme.MOD_ID, "estro_progress"), Codec.intRange(0, MAX_ESTRO_PROGRESS));
    public static final AttachmentType<Long> ESTRO_NEED_TIME = AttachmentRegistry.createPersistent( //age when needing estrogen again
        Identifier.of(BreakingFemme.MOD_ID, "estro_need_time"), Codec.LONG);

    static final Identifier NAMES_PATH = Identifier.of(BreakingFemme.MOD_ID, "names.txt"); //in assets folder
    static ArrayList<String> NAMES;

    //if i dont do this, attachments wont exist before a villager gets ticked, i.e. during deserialization of the world
    public static void registerAttachments()
    {
        //
    }

    private static void loadNames(ResourceManager rm)
    {
        //if names already loaded, dont bother!
        if(NAMES != null)
            return;

        //load names
        try {
            BufferedReader reader = rm.openAsReader(NAMES_PATH);

            try {
                NAMES = (ArrayList<String>)reader.lines().collect(Collectors.toList());
            } catch (Throwable error) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Throwable error_while_closing) {
                        error.addSuppressed(error_while_closing);
                    }
                }

                throw error;
            }

            if (reader != null) {
                reader.close();
            }
        } catch (Throwable error) {
            error.printStackTrace();
            BreakingFemme.LOGGER.error("ERROR: was unable to load data file " + NAMES_PATH.toString());
            NAMES = (ArrayList<String>)List.of("NO AVAILABLE NAMES");
        }
    }

    //check if an item is an estrogen (for villagers)
    public static boolean isEstrogen(Item item)
    {
        //TODO: mb move to another method, mb use estrogenTime instead
        return item.equals(ModItems.EGEL_BOTTLE);
    }

    //time of activity of an estrogen
    public static int estrogenTime(Item item)
    {
        //TODO: change! mb move to another method
        if(item.equals(ModItems.EGEL_BOTTLE))
            return 24000; //1 mc day (yeah it goes quicker than irl)

        return 0;
    }

    //get value of an estrogen item in emeralds
    public static int getValue(Item item)
    {
        //TODO: change! mb move to another method
        if(item.equals(ModItems.EGEL_BOTTLE))
            return 1;

        return 0;
    }

    public static boolean isTransfem(VillagerEntity villager)
	{
        //need to set when queried, otherwise it would be a different random pick afterwards!
        //also, the transfem-ness status of a villager isnt changeable after being set like this
        if(!villager.hasAttached(IS_TRANSFEM))
        {
            boolean is_transfem = villager.getWorld().getRandom().nextInt(16) == 0; //this adjusts the probability of being transfem
            villager.setAttached(IS_TRANSFEM, is_transfem);
        }
		return villager.getAttached(IS_TRANSFEM);
	}

    public static boolean isNamefluid(VillagerEntity villager)
	{
        //need to set when queried, otherwise it would be a different random pick afterwards!
        //also, the namefluid-ness status of a villager isnt changeable after being set like this
        if(!villager.hasAttached(IS_NAMEFLUID))
        {
            boolean is_namefluid = isTransfem(villager) && villager.getWorld().getRandom().nextInt(64) == 0; //this adjusts the probability of being namefluid (among transfems) (so in total 1 in 1024 villagers are namefluid)
            villager.setAttached(IS_NAMEFLUID, is_namefluid);
        }
		return villager.getAttached(IS_NAMEFLUID);
	}

    public static boolean hasName(VillagerEntity villager)
    {
        return villager.hasAttached(NAME);
    }

    public static void chooseName(VillagerEntity villager)
    {
        loadNames(villager.getServer().getResourceManager()); //if names not already loaded, load them

        String name = NAMES.get(villager.getRandom().nextInt(NAMES.size())); //this is temporary! TODO: pick random name from list
        villager.setAttached(NAME, name);
        villager.setCustomName(Text.literal(name));
    }

    public static void setNameToChosen(VillagerEntity villager)
    {
        //possibility: namefluid tag => very small probability of picking another name
        if(villager.hasAttached(NAME))
            villager.setCustomName(Text.literal(villager.getAttached(NAME)));
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
