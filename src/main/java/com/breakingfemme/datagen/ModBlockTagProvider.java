package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.BreakingFemme;
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

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    //custom block tags (NOT in common tags)
    public static final TagKey<Block> HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "hot"));
    public static final TagKey<Block> FURNACE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "furnaces"));
    //trying to make something compatible with blaze burners/low heaters etc (create & addons)
    public static final TagKey<Block> CREATE_HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("create", "passive_boiler_heaters"));

    public static final TagKey<Block> NICKEL_ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores/nickel"));
    public static final TagKey<Block> ORES_STONE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores_in_ground/stone"));
    public static final TagKey<Block> ORES_DEEPSLATE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores_in_ground/deepslate"));
    public static final TagKey<Block> NICKEL_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "storage_blocks/nickel"));

    //fermenter panel tags (addition of modded panels?)
    public static final TagKey<Block> FERMENTER_SIDE_PANEL = TagKey.of(RegistryKeys.BLOCK, new Identifier(BreakingFemme.MOD_ID, "fermenter_side_panel"));
    public static final TagKey<Block> FERMENTER_TOP_PANEL = TagKey.of(RegistryKeys.BLOCK, new Identifier(BreakingFemme.MOD_ID, "fermenter_top_panel"));
    public static final TagKey<Block> FERMENTER_BOTTOM_PANEL = TagKey.of(RegistryKeys.BLOCK, new Identifier(BreakingFemme.MOD_ID, "fermenter_bottom_panel"));
    public static final TagKey<Block> FERMENTER_AIRLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier(BreakingFemme.MOD_ID, "fermenter_airlock"));

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        //nickel tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(NICKEL_BLOCK).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(NICKEL_ORES).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(NICKEL_ORES).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(ORES_STONE).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(ORES_DEEPSLATE).add(ModBlocks.DEEPSLATE_NICKEL_ORE);

        //all the cauldrons, vanilla tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.MACERATING_SOY_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.REDOX_REACTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.YEAST_STARTER_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.YEAST_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.COPPER_SULFATE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.NICKEL_SULFATE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET32_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET64_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ET95_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.STEROL_SOLUTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ANDROSTADIENEDIONE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.TAR_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.BEER_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.NETHER_BEER_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.SLUDGE_CAULDRON);
        //AUTOGENERATION LABEL DO NOT TOUCH

        //fermenter tags
        getOrCreateTagBuilder(FERMENTER_SIDE_PANEL).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(FERMENTER_TOP_PANEL).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_HEATER);
        getOrCreateTagBuilder(FERMENTER_SIDE_PANEL).add(ModBlocks.FERMENTER_CONTROLLER);
        getOrCreateTagBuilder(FERMENTER_AIRLOCK).add(ModBlocks.FERMENTER_AIRLOCK);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_HEATER);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_CONTROLLER);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.FERMENTER_AIRLOCK);

        //custom block tags
        getOrCreateTagBuilder(HOT).add(Blocks.FIRE);
        getOrCreateTagBuilder(HOT).add(Blocks.SOUL_FIRE);
        getOrCreateTagBuilder(HOT).add(Blocks.LAVA);
        getOrCreateTagBuilder(HOT).add(Blocks.MAGMA_BLOCK);
        getOrCreateTagBuilder(FURNACE).add(Blocks.FURNACE); //these also need the LIT property to be considered "hot"
        getOrCreateTagBuilder(FURNACE).add(Blocks.SMOKER);
        getOrCreateTagBuilder(FURNACE).add(Blocks.BLAST_FURNACE);
        getOrCreateTagBuilder(FURNACE).add(Blocks.CAMPFIRE);
        getOrCreateTagBuilder(FURNACE).add(Blocks.SOUL_CAMPFIRE);
    }
}