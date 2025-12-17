package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.COPPER_SULFATE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.NICKEL_SULFATE_CAULDRON);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);
    }
}
