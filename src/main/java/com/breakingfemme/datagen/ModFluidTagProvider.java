package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.FluidTags;

public class ModFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public ModFluidTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_COPPER_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_COPPER_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_NICKEL_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_NICKEL_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ET32);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ET32);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ET64);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ET64);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ET95);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ET95);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_STEROL_SOLUTION);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_STEROL_SOLUTION);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ANDROSTADIENEDIONE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ANDROSTADIENEDIONE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_NETHER_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_NETHER_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_SLUDGE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_SLUDGE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ANDROSTADIENEDIONE_OIL_SOLUTION);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ANDROSTADIENEDIONE_OIL_SOLUTION);
        //getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_COAL_OIL);
        //getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_COAL_OIL);
        //TODO: make entities NOT extinguished by flammable fluids... or if touching flammable fluids
        //so investigate/mixin Entity.getWaterState. with a new "flammable" tag potentially
        //or... make this fluid have the lava tag, and mixin other methods?
        //or actually, updateMovementInFluid doesn't depend too much on the tag, just need to move the entity around, alongside calls with water and lava tags
        //AUTOGENERATION LABEL DO NOT TOUCH

        //NOTE: tar does NOT behave like water. it doesn't make bubbles. its much more viscous than water. and you cant just see if youre submerged in tar.
        //so it doesn't get the water tag.
    }
}
