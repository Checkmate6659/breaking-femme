package com.breakingfemme.datagen.providers;

import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModFluids;
import com.breakingfemme.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {


    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        //shovel mineables
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(ModBlocks.KELP_ASH_BLOCK);
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(ModBlocks.LIMESTONE_CHUNKS);
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(ModBlocks.QUICKLIME);
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(ModBlocks.SLAKED_LIME);

        //stone tag
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.STONE);
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.DEEPSLATE);
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.GRANITE);
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.DIORITE);
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.ANDESITE);
        getOrCreateTagBuilder(ModTags.Block.STONES).add(Blocks.TUFF);

        //nickel tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(ModTags.Block.NICKEL_BLOCK).add(ModBlocks.NICKEL_BLOCK);
        getOrCreateTagBuilder(ModTags.Block.NICKEL_ORES).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(ModTags.Block.NICKEL_ORES).add(ModBlocks.DEEPSLATE_NICKEL_ORE);
        getOrCreateTagBuilder(ModTags.Block.ORES_STONE).add(ModBlocks.NICKEL_ORE);
        getOrCreateTagBuilder(ModTags.Block.ORES_DEEPSLATE).add(ModBlocks.DEEPSLATE_NICKEL_ORE);

        //ruby tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.RUBY_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.PARTIALLY_REDUCED_RUBY_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.RUBY_ORE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DEEPSLATE_RUBY_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.RUBY_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.PARTIALLY_REDUCED_RUBY_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.RUBY_ORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.DEEPSLATE_RUBY_ORE);
        getOrCreateTagBuilder(ModTags.Block.RUBY_BLOCK).add(ModBlocks.RUBY_BLOCK);
        getOrCreateTagBuilder(ModTags.Block.RUBY_ORES).add(ModBlocks.RUBY_ORE);
        getOrCreateTagBuilder(ModTags.Block.RUBY_ORES).add(ModBlocks.DEEPSLATE_RUBY_ORE);
        getOrCreateTagBuilder(ModTags.Block.ORES_STONE).add(ModBlocks.RUBY_ORE);
        getOrCreateTagBuilder(ModTags.Block.ORES_DEEPSLATE).add(ModBlocks.DEEPSLATE_RUBY_ORE);

        //miscellaneous
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.ROCK_SALT_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.FIRE_BRICKS);

        //all the cauldrons, vanilla tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.MACERATING_SOY_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.REDOX_REACTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.YEAST_STARTER_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.YEAST_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.HYDROCHLORIC_ACID_SYNTHESIS_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.HYDROGEN_GENERATOR_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ESTRONE_REDUCTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.THICK_POTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.EGEL_CAULDRON);
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
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ANDROSTADIENEDIONE_OIL_SOLUTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.COAL_OIL_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ESTRONE_OIL_SOLUTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ESTRONE_RECRYSTALLIZATION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.ESTRADIOL_RECRYSTALLIZATION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.LYE_WATER_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.CAUSTIC_SODA_SOLUTION_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.CAUSTIC_SODA_CAKE_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.CONCENTRATED_CAUSTIC_SODA_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.SULFURIC_ACID_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.IMPURE_DILUTE_SULFURIC_ACID_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.HYDROCHLORIC_ACID_CAULDRON);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModFluids.KELP_ASH_MUD_CAULDRON);
        //AUTOGENERATION LABEL DO NOT TOUCH

        //fermenter tags
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_SIDE_PANEL).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_TOP_PANEL).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_BOTTOM_PANEL).add(ModBlocks.FERMENTER_HEATER);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_SIDE_PANEL).add(ModBlocks.FERMENTER_CONTROLLER);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_AIRLOCK).add(ModBlocks.FERMENTER_AIRLOCK);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_HEATER).add(ModBlocks.FERMENTER_HEATER);
        getOrCreateTagBuilder(ModTags.Block.FERMENTER_MIXER).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_PANEL);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_TOP);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_BOTTOM);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_MIXER);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_HEATER);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.FERMENTER_CONTROLLER);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.FERMENTER_AIRLOCK);

        //distiller tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DISTILLER_TOP);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DISTILLER_TOP);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DISTILLER_COLUMN);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DISTILLER_COLUMN);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.DISTILLER_BASE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DISTILLER_BASE);

        //5g tower tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.FIVE_G_TOWER_CONTROLLER)
                .add(ModBlocks.FIVE_G_TOWER_HEAD)
                .add(ModBlocks.FIVE_G_SCAFFOLDING);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FIVE_G_TOWER_CONTROLLER)
                .add(ModBlocks.FIVE_G_SCAFFOLDING)
                .add(ModBlocks.FIVE_G_TOWER_HEAD);

        //custom block tags
        getOrCreateTagBuilder(ModTags.Block.HOT).add(Blocks.FIRE);
        getOrCreateTagBuilder(ModTags.Block.HOT).add(Blocks.SOUL_FIRE);
        getOrCreateTagBuilder(ModTags.Block.HOT).add(Blocks.LAVA);
        getOrCreateTagBuilder(ModTags.Block.HOT).add(Blocks.MAGMA_BLOCK);
        getOrCreateTagBuilder(ModTags.Block.FURNACE).add(Blocks.FURNACE); //these also need the LIT property to be considered "hot"
        getOrCreateTagBuilder(ModTags.Block.FURNACE).add(Blocks.SMOKER);
        getOrCreateTagBuilder(ModTags.Block.FURNACE).add(Blocks.BLAST_FURNACE);
        getOrCreateTagBuilder(ModTags.Block.FURNACE).add(Blocks.CAMPFIRE);
        getOrCreateTagBuilder(ModTags.Block.FURNACE).add(Blocks.SOUL_CAMPFIRE);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.ICE);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.PACKED_ICE);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.BLUE_ICE);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.FROSTED_ICE);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.SNOW_BLOCK);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.POWDER_SNOW);
        getOrCreateTagBuilder(ModTags.Block.COLD).add(Blocks.POWDER_SNOW_CAULDRON);
    }
}