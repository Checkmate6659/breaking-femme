package com.breakingfemme.datagen;

import com.breakingfemme.block.DistillerColumnBlock;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.block.SoyCropBlock;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        BlockStatePropertyLootCondition.Builder soy_condition = BlockStatePropertyLootCondition.builder(ModBlocks.SOY_CROP)
            .properties(StatePredicate.Builder.create().exactMatch(SoyCropBlock.AGE, SoyCropBlock.MAX_AGE));
        addDrop(ModBlocks.SOY_CROP, cropDrops(ModBlocks.SOY_CROP, ModItems.SOYBEANS, ModItems.SOYBEANS, soy_condition));

        //Cauldron blocks where we should just pick up the cauldron
        //Where we need an extra item too we must do it by hand
        addDrop(ModFluids.COPPER_SULFATE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.NICKEL_SULFATE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET32_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET64_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ET95_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.STEROL_SOLUTION_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ANDROSTADIENEDIONE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.TAR_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.BEER_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.NETHER_BEER_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.SLUDGE_CAULDRON, Blocks.CAULDRON);
        addDrop(ModFluids.ANDROSTADIENEDIONE_OIL_SOLUTION_CAULDRON, Blocks.CAULDRON);
        //AUTOGENERATION LABEL DO NOT TOUCH

        addDrop(ModBlocks.NICKEL_BLOCK);
        addDrop(ModBlocks.NICKEL_ORE, oreDrops(ModBlocks.NICKEL_ORE, ModItems.RAW_NICKEL));
        addDrop(ModBlocks.DEEPSLATE_NICKEL_ORE, oreDrops(ModBlocks.DEEPSLATE_NICKEL_ORE, ModItems.RAW_NICKEL));

        //fermenter
        addDrop(ModBlocks.FERMENTER_PANEL);
        addDrop(ModBlocks.FERMENTER_TOP);
        addDrop(ModBlocks.FERMENTER_BOTTOM);
        addDrop(ModBlocks.FERMENTER_MIXER);
        addDrop(ModBlocks.FERMENTER_HEATER);
        addDrop(ModBlocks.FERMENTER_CONTROLLER);
        addDrop(ModBlocks.FERMENTER_AIRLOCK, dropsWithSilkTouch(ModBlocks.FERMENTER_AIRLOCK));

        //distiller
        BlockStatePropertyLootCondition.Builder distiller_gravel_condition = BlockStatePropertyLootCondition.builder(ModBlocks.DISTILLER_COLUMN)
            .properties(StatePredicate.Builder.create().exactMatch(DistillerColumnBlock.FULL, true));
        addDrop(ModBlocks.DISTILLER_COLUMN, applyExplosionDecay(ModBlocks.DISTILLER_COLUMN, LootTable.builder()
            .pool(LootPool.builder().with(ItemEntry.builder(ModBlocks.DISTILLER_COLUMN)))
            .pool(LootPool.builder().with(ItemEntry.builder(Blocks.GRAVEL).conditionally(distiller_gravel_condition)))
        ));
        addDrop(ModBlocks.DISTILLER_TOP);
    }
}
