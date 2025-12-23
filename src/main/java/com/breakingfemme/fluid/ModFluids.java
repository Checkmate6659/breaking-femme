package com.breakingfemme.fluid;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.cauldron.SterolSolutionCauldronBlock;
import com.breakingfemme.cauldron.CopperSulfateCauldronBlock;
import com.breakingfemme.cauldron.Et32CauldronBlock;
import com.breakingfemme.cauldron.Et64CauldronBlock;
import com.breakingfemme.cauldron.Et95CauldronBlock;
import com.breakingfemme.cauldron.MaceratingSoyCauldronBlock;
import com.breakingfemme.cauldron.NickelSulfateCauldronBlock;
import com.breakingfemme.cauldron.RedoxReactionCauldronBlock;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.item.SolutionBucketItem;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids {
    //Milk cauldron
    //public static Block MILK_CAULDRON;

    //Reaction cauldrons
    public static Block MACERATING_SOY_CAULDRON;
    public static Block REDOX_REACTION_CAULDRON;

    //Copper sulfate solution
    public static FlowableFluid STILL_COPPER_SULFATE;
    public static FlowableFluid FLOWING_COPPER_SULFATE;
    public static Block COPPER_SULFATE_FLUID_BLOCK;
    public static Item COPPER_SULFATE_BUCKET;
    public static Block COPPER_SULFATE_CAULDRON;

    //Nickel sulfate solution
    public static FlowableFluid STILL_NICKEL_SULFATE;
    public static FlowableFluid FLOWING_NICKEL_SULFATE;
    public static Block NICKEL_SULFATE_FLUID_BLOCK;
    public static Item NICKEL_SULFATE_BUCKET;
    public static Block NICKEL_SULFATE_CAULDRON;

    //32% ethanol
    public static FlowableFluid STILL_ET32;
    public static FlowableFluid FLOWING_ET32;
    public static Block ET32_FLUID_BLOCK;
    public static Item ET32_BUCKET;
    public static Block ET32_CAULDRON;

    //64% ethanol
    public static FlowableFluid STILL_ET64;
    public static FlowableFluid FLOWING_ET64;
    public static Block ET64_FLUID_BLOCK;
    public static Item ET64_BUCKET;
    public static Block ET64_CAULDRON;

    //95% ethanol
    public static FlowableFluid STILL_ET95;
    public static FlowableFluid FLOWING_ET95;
    public static Block ET95_FLUID_BLOCK;
    public static Item ET95_BUCKET;
    public static Block ET95_CAULDRON;

    //Sterol solution
    public static FlowableFluid STILL_STEROL_SOLUTION;
    public static FlowableFluid FLOWING_STEROL_SOLUTION;
    public static Block STEROL_SOLUTION_FLUID_BLOCK;
    public static Item STEROL_SOLUTION_BUCKET;
    public static Block STEROL_SOLUTION_CAULDRON;


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

        //32% ethanol
        STILL_ET32 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "ethanol32"),
            new Et32Fluid.Still());
        FLOWING_ET32 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_ethanol32"),
            new Et32Fluid.Flowing());
        ET32_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol32_block"),
            new FluidBlock(STILL_ET32, FabricBlockSettings.copyOf(Blocks.WATER)){});
        ET32_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "ethanol32_bucket"),
            new BucketItem(STILL_ET32, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        //64% ethanol
        STILL_ET64 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "ethanol64"),
            new Et64Fluid.Still());
        FLOWING_ET64 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_ethanol64"),
            new Et64Fluid.Flowing());
        ET64_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol64_block"),
            new FluidBlock(STILL_ET64, FabricBlockSettings.copyOf(Blocks.WATER)){});
        ET64_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "ethanol64_bucket"),
            new BucketItem(STILL_ET64, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        //95% ethanol
        STILL_ET95 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "ethanol95"),
            new Et95Fluid.Still());
        FLOWING_ET95 = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_ethanol95"),
            new Et95Fluid.Flowing());
        ET95_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol95_block"),
            new FluidBlock(STILL_ET95, FabricBlockSettings.copyOf(Blocks.WATER)){});
        ET95_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "ethanol95_bucket"),
            new BucketItem(STILL_ET95, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        //Sterol solution
        STILL_STEROL_SOLUTION = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "sterol_solution"),
            new SterolSolutionFluid.Still());
        FLOWING_STEROL_SOLUTION = Registry.register(Registries.FLUID, new Identifier(BreakingFemme.MOD_ID, "flowing_sterol_solution"),
            new SterolSolutionFluid.Flowing());
        STEROL_SOLUTION_FLUID_BLOCK = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "sterol_solution_block"),
            new FluidBlock(STILL_STEROL_SOLUTION, FabricBlockSettings.copyOf(Blocks.WATER)){});
        STEROL_SOLUTION_BUCKET = Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, "sterol_solution_bucket"),
            new SolutionBucketItem(STILL_STEROL_SOLUTION, ModItems.STEROLS, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));

        //AUTOGENERATION LABEL DO NOT TOUCH

        //cauldron fluid content registrations (need to be done after everything else fluid-related)
        //https://maven.fabricmc.net/docs/fabric-api-0.88.2+1.20.2/net/fabricmc/fabric/api/transfer/v1/fluid/CauldronFluidContent.html
        MACERATING_SOY_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "macerating_soy_cauldron"), new MaceratingSoyCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON).ticksRandomly()));
        REDOX_REACTION_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "redox_reaction_cauldron"), new RedoxReactionCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON).ticksRandomly()));
        COPPER_SULFATE_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "copper_sulfate_cauldron"), new CopperSulfateCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(COPPER_SULFATE_CAULDRON, STILL_COPPER_SULFATE, FluidConstants.BUCKET, null);
        NICKEL_SULFATE_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "nickel_sulfate_cauldron"), new NickelSulfateCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(NICKEL_SULFATE_CAULDRON, STILL_NICKEL_SULFATE, FluidConstants.BUCKET, null);
        ET32_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol32_cauldron"), new Et32CauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(ET32_CAULDRON, STILL_ET32, FluidConstants.BOTTLE, Et32CauldronBlock.LEVEL);
        ET64_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol64_cauldron"), new Et64CauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(ET64_CAULDRON, STILL_ET64, FluidConstants.BOTTLE, Et64CauldronBlock.LEVEL);
        ET95_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "ethanol95_cauldron"), new Et95CauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(ET95_CAULDRON, STILL_ET95, FluidConstants.BOTTLE, Et95CauldronBlock.LEVEL);
        STEROL_SOLUTION_CAULDRON = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "sterol_solution_cauldron"), new SterolSolutionCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
        CauldronFluidContent.registerCauldron(STEROL_SOLUTION_CAULDRON, STILL_STEROL_SOLUTION, FluidConstants.BUCKET, null);
        //AUTOGENERATION LABEL DO NOT TOUCH
    }
}
