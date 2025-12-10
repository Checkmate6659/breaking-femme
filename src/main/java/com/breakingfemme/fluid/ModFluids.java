package com.breakingfemme.fluid;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.item.SolutionBucketItem;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids {
    //Copper sulfate solution
    public static FlowableFluid STILL_COPPER_SULFATE;
    public static FlowableFluid FLOWING_COPPER_SULFATE;
    public static Block COPPER_SULFATE_FLUID_BLOCK;
    public static Item COPPER_SULFATE_BUCKET;

    //Nickel sulfate solution
    public static FlowableFluid STILL_NICKEL_SULFATE;
    public static FlowableFluid FLOWING_NICKEL_SULFATE;
    public static Block NICKEL_SULFATE_FLUID_BLOCK;
    public static Item NICKEL_SULFATE_BUCKET;


    public static void registerModFluids()
    {
        //Copper sulfate solution
        STILL_COPPER_SULFATE = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "copper_sulfate_solution"),
            new CopperSulfateFluid.Still());
        FLOWING_COPPER_SULFATE = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_copper_sulfate_solution"),
            new CopperSulfateFluid.Flowing());
        COPPER_SULFATE_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "copper_sulfate_solution_block"),
            new FluidBlock(STILL_COPPER_SULFATE, FabricBlockSettings.copyOf(Blocks.WATER)){});
         COPPER_SULFATE_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "copper_sulfate_solution_bucket"),
            new SolutionBucketItem(STILL_COPPER_SULFATE, ModItems.COPPER_SULFATE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        //Nickel sulfate solution
        STILL_NICKEL_SULFATE = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "nickel_sulfate_solution"),
            new NickelSulfateFluid.Still());
        FLOWING_NICKEL_SULFATE = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_nickel_sulfate_solution"),
            new NickelSulfateFluid.Flowing());
        NICKEL_SULFATE_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "nickel_sulfate_solution_block"),
            new FluidBlock(STILL_NICKEL_SULFATE, FabricBlockSettings.copyOf(Blocks.WATER)){});
        NICKEL_SULFATE_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "nickel_sulfate_solution_bucket"),
            new SolutionBucketItem(STILL_NICKEL_SULFATE, ModItems.NICKEL_SULFATE, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    }
}
