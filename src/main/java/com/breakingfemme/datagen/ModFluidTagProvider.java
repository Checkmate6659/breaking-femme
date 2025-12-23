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
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_TAR);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_TAR);
        //AUTOGENERATION LABEL DO NOT TOUCH

    }
}