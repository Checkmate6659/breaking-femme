package com.breakingfemme;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.screen.FermenterScreen;
import com.breakingfemme.screen.ModScreenHandlers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class BreakingFemmeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //screens/screen handlers
        HandledScreens.register(ModScreenHandlers.FERMENTER_SCREEN_HANDLER, FermenterScreen::new);

        //cutout blocks
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOY_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MILK_SEPARATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_PANEL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_TOP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_BOTTOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_MIXER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_CONTROLLER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_AIRLOCK, RenderLayer.getCutout()); //can we do something to make block not see through itself??

        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0x803F76E4; //default water color
            return -1;
        }, ModBlocks.FERMENTER_AIRLOCK);

        //fluids
        //macerating soy (cauldron only)
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0x80A0BBF2; //same color as et64, just more transparent
            return -1;
        }, ModFluids.MACERATING_SOY_CAULDRON);

        //redoxing copper sulfate (cauldron only)
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0x8033C1FF; //same color as copper sulfate, just more transparent
            return -1;
        }, ModFluids.REDOX_REACTION_CAULDRON);

        //yeast starter (cauldron only)
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xFFF8F8AA; //pale yellow color
            return -1;
        }, ModFluids.YEAST_STARTER_CAULDRON);

        //yeast (cauldron only)
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xFFEEEEAA; //same as yeast starter... but a bit darker
            return -1;
        }, ModFluids.YEAST_CAULDRON);

        //copper sulfate
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_COPPER_SULFATE, ModFluids.FLOWING_COPPER_SULFATE,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC033C1FF) //alpha, r, g, b
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_COPPER_SULFATE, ModFluids.FLOWING_COPPER_SULFATE);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC033C1FF;
            return -1;
        }, ModFluids.COPPER_SULFATE_CAULDRON);

        //nickel sulfate
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_NICKEL_SULFATE, ModFluids.FLOWING_NICKEL_SULFATE,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xE21B8D3D)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_NICKEL_SULFATE, ModFluids.FLOWING_NICKEL_SULFATE);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xE21B8D3D;
            return -1;
        }, ModFluids.NICKEL_SULFATE_CAULDRON);

        //32% ethanol
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ET32, ModFluids.FLOWING_ET32,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xE06F98EB) //everything gets affinely interpolated between water and 95% ethanol
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_ET32, ModFluids.FLOWING_ET32);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xE06F98EB;
            return -1;
        }, ModFluids.ET32_CAULDRON);

        //64% ethanol
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ET64, ModFluids.FLOWING_ET64,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0A0BBF2) //same here
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_ET64, ModFluids.FLOWING_ET64);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0A0BBF2;
            return -1;
        }, ModFluids.ET64_CAULDRON);

        //95% ethanol
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ET95, ModFluids.FLOWING_ET95,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xA0D0DEF9) //default water tint is 3F76E4, here we divide all coords by 4 then add 0xC0 to them, and tint is 0xA0 (its 0xFF for water)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_ET95, ModFluids.FLOWING_ET95);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xA0D0DEF9;
            return -1;
        }, ModFluids.ET95_CAULDRON);

        //sterol solution
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_STEROL_SOLUTION, ModFluids.FLOWING_STEROL_SOLUTION,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0C87AB4) //alpha, r, g, b
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_STEROL_SOLUTION, ModFluids.FLOWING_STEROL_SOLUTION);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0C87AB4;
            return -1;
        }, ModFluids.STEROL_SOLUTION_CAULDRON);

        //androstadienedione solution
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ANDROSTADIENEDIONE, ModFluids.FLOWING_ANDROSTADIENEDIONE,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0FF802A)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_ANDROSTADIENEDIONE, ModFluids.FLOWING_ANDROSTADIENEDIONE);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0FF802A;
            return -1;
        }, ModFluids.ANDROSTADIENEDIONE_CAULDRON);

        //tar solution
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_TAR, ModFluids.FLOWING_TAR,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xFF080808)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getCutout(), ModFluids.STILL_TAR, ModFluids.FLOWING_TAR);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xFF111111;
            return -1;
        }, ModFluids.TAR_CAULDRON);

        //beer
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_BEER, ModFluids.FLOWING_BEER,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0DBDE90)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_BEER, ModFluids.FLOWING_BEER);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0DBDE90;
            return -1;
        }, ModFluids.BEER_CAULDRON);

        //nether beer
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_NETHER_BEER, ModFluids.FLOWING_NETHER_BEER,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0DA4634)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_NETHER_BEER, ModFluids.FLOWING_NETHER_BEER);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0DA4634;
            return -1;
        }, ModFluids.NETHER_BEER_CAULDRON);

        //sludge that poisons you
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_SLUDGE, ModFluids.FLOWING_SLUDGE,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xFF794C24)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_SLUDGE, ModFluids.FLOWING_SLUDGE);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xFF794C24;
            return -1;
        }, ModFluids.SLUDGE_CAULDRON);

        //AUTOGENERATION LABEL DO NOT TOUCH
    }
}