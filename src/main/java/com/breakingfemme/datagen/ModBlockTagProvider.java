package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

//TODO: wiki.fabricmc.net/community:common_tags
public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    //custom block tags (NOT in common tags)
    public static final TagKey<Block> HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "hot"));
    public static final TagKey<Block> FURNACE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "furnace"));
    //trying to make something compatible with blaze burners/low heaters etc (create & addons)
    public static final TagKey<Block> CREATE_HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("create", "passive_boiler_heaters"));

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        //vanilla tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.MACERATING_SOY_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.REDOX_REACTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.COPPER_SULFATE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.NICKEL_SULFATE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET32_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET64_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET95_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.STEROL_SOLUTION_CAULDRON);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);

        //custom block tags
        getOrCreateTagBuilder(HOT).add(Blocks.FIRE);
        getOrCreateTagBuilder(HOT).add(Blocks.SOUL_FIRE);
        getOrCreateTagBuilder(HOT).add(Blocks.LAVA);
        getOrCreateTagBuilder(HOT).add(Blocks.MAGMA_BLOCK);
        getOrCreateTagBuilder(FURNACE).add(Blocks.FURNACE);
        getOrCreateTagBuilder(FURNACE).add(Blocks.SMOKER);
        getOrCreateTagBuilder(FURNACE).add(Blocks.BLAST_FURNACE);
        getOrCreateTagBuilder(FURNACE).add(Blocks.CAMPFIRE); //these also need the LIT property to be considered "hot"
        getOrCreateTagBuilder(FURNACE).add(Blocks.SOUL_CAMPFIRE);
    }
}
