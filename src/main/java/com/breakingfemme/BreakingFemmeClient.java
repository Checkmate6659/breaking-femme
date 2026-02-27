package com.breakingfemme;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.mixin.PostEffectPassAccessor;
import com.breakingfemme.screen.FermenterScreen;
import com.breakingfemme.screen.ModScreenHandlers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class BreakingFemmeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //example repo: https://github.com/CelDaemon/post-process-example/blob/main/src/client/java/net/voidgroup/postProcessExample/client/PostProcessExampleClient.java
        //its on mojmaps tho, im on yarn
        //PostChain -> PostEffectProcessor
        //TODO: effect shaders HERE instead of mixin
        final var client = new MutableObject<MinecraftClient>();
        final var postprocessor = new MutableObject<PostEffectProcessor>();
        ClientLifecycleEvents.CLIENT_STARTED.register(cl -> {
            client.setValue(cl);
            try {
                postprocessor.setValue(new PostEffectProcessor(cl.getTextureManager(), cl.getResourceManager(), cl.getFramebuffer(), new Identifier("shaders/post/breakingfemme_altered_vision.json")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            final var mainFramebuffer = client.getValue().getFramebuffer();
            final var currentPostProcessor = postprocessor.getValue();
            final PlayerEntity player = client.getValue().player;

            //this is where we set our uniforms and shit
            float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
            if(etoh <= 1.25f || player.isSpectator()) //no visual effects: set all uniforms to 0
            {
				List<PostEffectPass> passes = ((PostEffectPassAccessor)currentPostProcessor).breakingfemme$getPasses();

				passes.get(0).getProgram().getUniformByNameOrDummy("EffectStrength").set(0f);
				passes.get(0).getProgram().getUniformByNameOrDummy("Blindness").set(0f);
				passes.get(1).getProgram().getUniformByNameOrDummy("BlurStrength").set(0f);
				passes.get(2).getProgram().getUniformByNameOrDummy("BlurStrength").set(0f);
            }
            else //compute and set uniforms
            {
				float strength = (etoh - 1.25f) * 0.5f; //goes between 0 and 1, for diplopia and blurring
				if(strength > 1.0f) strength = 1.0f;
				float blinding = etoh - 2.0f; //goes from 0 (at 2) to 1 (at 3)
				if(blinding < 0.0f) blinding = 0.0f;
				else if(blinding > 1.0f) blinding = 1.0f;
				blinding *= blinding; //it is a more abrupt change; shouldnt really be noticeable at 2, but blacked out at 3
				
				//set uniforms
				List<PostEffectPass> passes = ((PostEffectPassAccessor)currentPostProcessor).breakingfemme$getPasses();

				passes.get(0).getProgram().getUniformByNameOrDummy("EffectStrength").set(0.05f * strength);
				passes.get(0).getProgram().getUniformByNameOrDummy("Blindness").set(blinding);
				passes.get(1).getProgram().getUniformByNameOrDummy("BlurStrength").set(16f * strength); //up to 16.0 (other to 32 or 64?)
				passes.get(2).getProgram().getUniformByNameOrDummy("BlurStrength").set(24f * strength); //more horizontal blur => primitive diplopia emulation
            }
            
            currentPostProcessor.setupDimensions(mainFramebuffer.textureWidth, mainFramebuffer.textureHeight);
            currentPostProcessor.render(0);
            mainFramebuffer.beginWrite(true);
        });

        //screens/screen handlers
        HandledScreens.register(ModScreenHandlers.FERMENTER_SCREEN_HANDLER, FermenterScreen::new);

        //cutout blocks (blocks that have transparent textures basically)
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOY_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MILK_SEPARATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_PANEL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_TOP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_BOTTOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_MIXER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_CONTROLLER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FERMENTER_AIRLOCK, RenderLayer.getCutout()); //can we do something to make block not see through itself??
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISTILLER_BASE, RenderLayer.getCutout());

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

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ANDROSTADIENEDIONE_OIL_SOLUTION, ModFluids.FLOWING_ANDROSTADIENEDIONE_OIL_SOLUTION,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xC0EEEE00)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_ANDROSTADIENEDIONE_OIL_SOLUTION, ModFluids.FLOWING_ANDROSTADIENEDIONE_OIL_SOLUTION);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xC0EEEE00;
            return -1;
        }, ModFluids.ANDROSTADIENEDIONE_OIL_SOLUTION_CAULDRON);

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_COAL_OIL, ModFluids.FLOWING_COAL_OIL,
            new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xEEEEEE00)
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_COAL_OIL, ModFluids.FLOWING_COAL_OIL);
        ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
            if (i == 0)
                return 0xEEEEEE00;
            return -1;
        }, ModFluids.COAL_OIL_CAULDRON);

        //AUTOGENERATION LABEL DO NOT TOUCH
    }
}