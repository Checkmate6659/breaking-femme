package com.breakingfemme.datagen;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.block.SoyCropBlock;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerCrop(ModBlocks.SOY_CROP, SoyCropBlock.AGE, 0, 1, 2, 3);

        //cauldrons need to be handmade apparently
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //regular items
        itemModelGenerator.register(ModItems.NICKEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.PULVERIZED_COPPER, Models.GENERATED);
        itemModelGenerator.register(ModItems.PULVERIZED_NICKEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.COPPER_SULFATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.NICKEL_SULFATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEROLS, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUDE_ESTRONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURE_ESTRONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURE_ESTRADIOL_CRYSTALS, Models.GENERATED);
        itemModelGenerator.register(ModItems.PURE_ESTRADIOL_POWDER, Models.GENERATED);

        //buckets
        itemModelGenerator.register(ModFluids.COPPER_SULFATE_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModFluids.NICKEL_SULFATE_BUCKET, Models.GENERATED);
    }
}
