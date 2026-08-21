package com.breakingfemme.datagen;

import com.breakingfemme.EntityAttachments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public ModEntityTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(EntityAttachments.ESTROGENABLE)
                .add(EntityType.VILLAGER)
                .add(EntityType.PLAYER)
                .add(EntityType.CREEPER)
                .add(EntityType.BLAZE) //TODO
                .add(EntityType.ENDERMAN)
                .add(EntityType.DROWNED)
                .add(EntityType.EVOKER)
                .add(EntityType.SKELETON)
                .add(EntityType.WARDEN)
                .add(EntityType.WITCH)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.PILLAGER)
                .add(EntityType.WANDERING_TRADER)
                .add(EntityType.HUSK)
                .add(EntityType.STRAY)
                .add(EntityType.WITHER /* this is non-negotiable */) //TODO
                .add(EntityType.VINDICATOR)
                .add(EntityType.ALLAY) //TODO
                .add(EntityType.ENDER_DRAGON /* this isn't either */); //TODO
    }
}
