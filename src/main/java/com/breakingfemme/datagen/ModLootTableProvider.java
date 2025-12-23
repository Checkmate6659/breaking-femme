package com.breakingfemme.datagen;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.block.SoyCropBlock;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(ModBlocks.SOY_CROP)
            .properties(StatePredicate.Builder.create().exactMatch(SoyCropBlock.AGE, SoyCropBlock.MAX_AGE));
        addDrop(ModBlocks.SOY_CROP, cropDrops(ModBlocks.SOY_CROP, ModItems.SOYBEANS, ModItems.SOYBEANS, builder));

        addDrop(ModFluids.MACERATING_SOY_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.MACERATING_SOY_CAULDRON, ModItems.SOYBEANS);
        addDrop(ModFluids.REDOX_REACTION_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.REDOX_REACTION_CAULDRON, ModItems.PULVERIZED_NICKEL);

        addDrop(ModFluids.COPPER_SULFATE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.NICKEL_SULFATE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET32_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET64_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET95_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.STEROL_SOLUTION_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ANDROSTADIENEDIONE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.TAR_CAULDRON, Blocks.CAULDRON);
        //AUTOGENERATION LABEL DO NOT TOUCH

        addDrop(ModBlocks.FERMENTER_PANEL);
        addDrop(ModBlocks.FERMENTER_TOP);
        addDrop(ModBlocks.FERMENTER_BOTTOM);
        addDrop(ModBlocks.FERMENTER_MIXER);
    }
}