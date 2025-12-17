package com.breakingfemme;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class BreakingFemmeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //cutout blocks
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOY_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_PANEL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_TOP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_BOTTOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_MIXER, RenderLayer.getCutout());

        //fluids
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
    }
}
