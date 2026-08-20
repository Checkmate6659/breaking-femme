package com.breakingfemme;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Predicate;
public class EntityAttachments {
    public static final class IsEstrogennablePredicate implements Predicate<Entity> {

        @Override
        public boolean test(Entity entity) {
            return isEstrogenable(entity);
        }
    }
    public static final int MAX_ESTRO_PROGRESS = 1728000; //this really is the amount of time the entity needs to transition: 1 irl day, i.e. ... 72 mc days. oh yeah that's quick!
    public static final AttachmentType<Integer> ESTRO_PROGRESS = AttachmentRegistry.createPersistent( //basically like number of doses of estrogen
            Identifier.of(BreakingFemme.MOD_ID, "estro_progress"), Codec.intRange(0, MAX_ESTRO_PROGRESS));
    public static final AttachmentType<Long> ESTRO_NEED_TIME = AttachmentRegistry.createPersistent( //age when needing estrogen again
            Identifier.of(BreakingFemme.MOD_ID, "estro_need_time"), Codec.LONG);

    public static final TagKey<EntityType<?>> ESTROGENABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(BreakingFemme.MOD_ID,"estrogenable"));
    public static int getTransitionTime(Entity entity)
    {
        // will never transition
        if (!isEstrogenable(entity)) return -1;
        //ESTRO_PROGRESS: progress after this dose has been consumed
        //ESTRO_NEED_AGE - age: time until the dose is consumed (is 0 if entity needs estrogen)
        World world = entity.getWorld();
        long time_to_next = entity.getAttachedOrSet(ESTRO_NEED_TIME, world.getTime()) - world.getTime(); //should be in int values realistically
        if(time_to_next < 0) time_to_next = 0;
        
        return entity.getAttachedOrSet(ESTRO_PROGRESS, 0) - (int)time_to_next;
    }

    public static boolean isEstrogenable(Entity entity) {
        return entity.getType().isIn(ESTROGENABLE);
    }
    public static boolean needsEstrogen(Entity entity)
    {
        // we never need E if we can't have it
        if (!isEstrogenable(entity)) return false;
        World world = entity.getWorld();
        return world.getTime() >= entity.getAttachedOrSet(ESTRO_NEED_TIME, world.getTime());
    }

    public static void giveEstrogenFor(Entity entity, int amount)
    {
        if (!isEstrogenable(entity)) return;
        //add to progress
        int estro_progress = entity.getAttachedOrElse(ESTRO_PROGRESS, 0);
        estro_progress += amount;
        if(estro_progress > MAX_ESTRO_PROGRESS) estro_progress = MAX_ESTRO_PROGRESS;
        entity.setAttached(ESTRO_PROGRESS, estro_progress);

        //add to time of next dose
        entity.setAttached(ESTRO_NEED_TIME, entity.getWorld().getTime() + amount);
    }

    //get normalized feature offset of given entity, from 0 (beginning, or not applicable) to 1 (complete)
    public static float getNormalizedFeatureOffset(Entity entity)
    {
        if(!isEstrogenable(entity)) return 0; //not applicable
        return (entity.getWorld().getTime() % 100) * 0.01F; //test
        //return (float)getTransitionTime(entity) / (float)MAX_ESTRO_PROGRESS; //linear growth
    }

    public static void registerAttachments()
    {
        //
    }
}
