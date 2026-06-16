package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public static final TagKey<Fluid> WATER_LIKE = TagKey.of(RegistryKeys.FLUID, new Identifier(BreakingFemme.MOD_ID, "water_like")); //basically water, but without the interaction or the fire extinguishing
    public static final TagKey<Fluid> FLAMMABLE = TagKey.of(RegistryKeys.FLUID, new Identifier(BreakingFemme.MOD_ID, "flammable"));
    public static final TagKey<Fluid> HARSH_ON_FILTERS = TagKey.of(RegistryKeys.FLUID, new Identifier(BreakingFemme.MOD_ID, "filter/harsh"));

    public ModFluidTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(WATER_LIKE).addTag(FluidTags.WATER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_COPPER_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_COPPER_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_NICKEL_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_NICKEL_SULFATE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ET32);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ET32);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_ET64);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_ET64);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_ET95);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_ET95);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_STEROL_SOLUTION);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_STEROL_SOLUTION);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_ANDROSTADIENEDIONE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_ANDROSTADIENEDIONE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_NETHER_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_NETHER_BEER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_SLUDGE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_SLUDGE);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_SPILLAGE);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_ANDROSTADIENEDIONE_OIL_SOLUTION);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_ANDROSTADIENEDIONE_OIL_SOLUTION);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_COAL_OIL);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_COAL_OIL);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.STILL_ESTRONE_OIL_SOLUTION);
        getOrCreateTagBuilder(FLAMMABLE).add(ModFluids.FLOWING_ESTRONE_OIL_SOLUTION);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_LYE_WATER);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_LYE_WATER);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.STILL_CAUSTIC_SODA_SOLUTION);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.FLOWING_CAUSTIC_SODA_SOLUTION);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.STILL_CAUSTIC_SODA_CAKE);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.FLOWING_CAUSTIC_SODA_CAKE);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.STILL_CONCENTRATED_CAUSTIC_SODA);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.FLOWING_CONCENTRATED_CAUSTIC_SODA);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.STILL_SULFURIC_ACID);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.FLOWING_SULFURIC_ACID);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.STILL_IMPURE_DILUTE_SULFURIC_ACID);
        getOrCreateTagBuilder(WATER_LIKE).add(ModFluids.FLOWING_IMPURE_DILUTE_SULFURIC_ACID);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.STILL_HYDROCHLORIC_ACID);
        getOrCreateTagBuilder(FluidTags.WATER).add(ModFluids.FLOWING_HYDROCHLORIC_ACID);
        //AUTOGENERATION LABEL DO NOT TOUCH

        getOrCreateTagBuilder(WATER_LIKE).addTag(FLAMMABLE);

        //NOTE: tar does NOT behave like water. it doesn't make bubbles. its much more viscous than water. and you cant just see if youre submerged in tar.
        //so it doesn't get the water tag.
        //(it should go into flammable fluids btw) (but i dont want its flow to be as fast as water) (mb water-like flow should be based on tick rate)

        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(Fluids.LAVA);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(Fluids.FLOWING_LAVA);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.STILL_SLUDGE);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.FLOWING_SLUDGE);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.STILL_LYE_WATER);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.FLOWING_LYE_WATER);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.STILL_CAUSTIC_SODA_SOLUTION);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.FLOWING_CAUSTIC_SODA_SOLUTION);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.STILL_CAUSTIC_SODA_CAKE);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.FLOWING_CAUSTIC_SODA_CAKE);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.STILL_CONCENTRATED_CAUSTIC_SODA);
        getOrCreateTagBuilder(HARSH_ON_FILTERS).add(ModFluids.FLOWING_CONCENTRATED_CAUSTIC_SODA);
    }
}
