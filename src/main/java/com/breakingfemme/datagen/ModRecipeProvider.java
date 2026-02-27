package com.breakingfemme.datagen;

import java.util.function.Consumer;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        //nickel ore smelting/blasting, nickel ingot compactification
        offerSmelting(exporter, ImmutableList.of(ModItems.RAW_NICKEL, ModBlocks.NICKEL_ORE, ModBlocks.DEEPSLATE_NICKEL_ORE), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0.7f, 200, "nickel_ingot");
        offerBlasting(exporter, ImmutableList.of(ModItems.RAW_NICKEL, ModBlocks.NICKEL_ORE, ModBlocks.DEEPSLATE_NICKEL_ORE), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0.7f, 100, "nickel_ingot_blasting");
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.NICKEL_INGOT, RecipeCategory.BUILDING_BLOCKS, ModBlocks.NICKEL_BLOCK);

        //mortar and pestle
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.MORTAR_PESTLE).input('#', ModItemTagProvider.STONES).input('|', ModItemTagProvider.IRON_INGOT).pattern(" | ").pattern("#|#").pattern("###").group("mortar_pestle").criterion(hasItem(Blocks.STONE), conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS)).offerTo(exporter);

        //re-casting pulverized copper/nickel (can't just use offerSmelting because it doesn't behave well with tags)
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 200).criterion(hasItem(ModItems.PULVERIZED_COPPER), conditionsFromTag(ModItemTagProvider.PULVERIZED_COPPER)).offerTo(exporter, "copper_ingot_from_remelting");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 100).criterion(hasItem(ModItems.PULVERIZED_COPPER), conditionsFromTag(ModItemTagProvider.PULVERIZED_COPPER)).offerTo(exporter, "copper_ingot_from_remelting_blasting");
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 200).criterion(hasItem(ModItems.PULVERIZED_NICKEL), conditionsFromTag(ModItemTagProvider.PULVERIZED_NICKEL)).offerTo(exporter, "nickel_ingot_from_remelting");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 100).criterion(hasItem(ModItems.PULVERIZED_NICKEL), conditionsFromTag(ModItemTagProvider.PULVERIZED_NICKEL)).offerTo(exporter, "nickel_ingot_from_remelting_blasting");

        //clay ingot mold
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.INGOT_MOLD, 2).input('#', Items.CLAY_BALL).pattern("# #").pattern(" # ").group("ingot_mold").criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL)).offerTo(exporter);

        //milkgot processing
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MILKGOT_MOLD, 8).input(ModItems.SKIMMED_MILK_BUCKET).input(Ingredient.ofItems(ModItems.INGOT_MOLD), 8).group("milkgot_mold").criterion(hasItem(ModItems.SKIMMED_MILK_BUCKET), conditionsFromItem(ModItems.SKIMMED_MILK_BUCKET)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(ModItems.MILKGOT_MOLD), RecipeCategory.MISC, ModItems.MILKGOT, 0, 200).criterion(hasItem(ModItems.MILKGOT_MOLD), conditionsFromItem(ModItems.MILKGOT_MOLD)).offerTo(exporter, "milkgot");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(ModItems.MILKGOT_MOLD), RecipeCategory.MISC, ModItems.MILKGOT, 0, 100).criterion(hasItem(ModItems.MILKGOT_MOLD), conditionsFromItem(ModItems.MILKGOT_MOLD)).offerTo(exporter, "milkgot_blasting");
        offerShapelessRecipe(exporter, ModItems.INGOT_MOLD, ModItems.MILKGOT_MOLD, "milkgot_discarding", 1);

        //creamgot processing
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(ModItems.CREAMGOT_MOLD), RecipeCategory.MISC, ModItems.CREAMGOT, 0, 200).criterion(hasItem(ModItems.CREAMGOT_MOLD), conditionsFromItem(ModItems.CREAMGOT_MOLD)).offerTo(exporter, "creamgot");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(ModItems.CREAMGOT_MOLD), RecipeCategory.MISC, ModItems.CREAMGOT, 0, 100).criterion(hasItem(ModItems.CREAMGOT_MOLD), conditionsFromItem(ModItems.CREAMGOT_MOLD)).offerTo(exporter, "creamgot_blasting");
        offerShapelessRecipe(exporter, ModItems.INGOT_MOLD, ModItems.CREAMGOT_MOLD, "creamgot_discarding", 1);

        //coal tar (can only use coal, NOT wood/charcoal, as it's not the same chemicals irl, wood tar doesn't contain nearly as much benzene or naphthalene for instance)
        offerSmelting(exporter, ImmutableList.of(Items.COAL), RecipeCategory.MISC, ModItems.TAR, 0, 200, "tar");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModFluids.TAR_BUCKET).input(Items.BUCKET).input(Ingredient.ofItems(ModItems.TAR), 8).group("tar_bucket").criterion(hasItem(ModItems.TAR), conditionsFromItem(ModItems.TAR)).offerTo(exporter, "tar_bucket");

        //fermenter parts
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_PANEL.asItem(), 16).input('#', ItemTags.PLANKS).input('-', Items.IRON_BARS).pattern("###").pattern("---").pattern("###").group("fermenter_panel").criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_TOP.asItem(), 4).input('#', ItemTags.PLANKS).input('-', ModItemTagProvider.IRON_INGOT).pattern(" - ").pattern("###").group("fermenter_top").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        offerShapelessRecipe(exporter, ModBlocks.FERMENTER_BOTTOM.asItem(), ModBlocks.FERMENTER_TOP.asItem(), "fermenter_top_convert", 1);
        offerShapelessRecipe(exporter, ModBlocks.FERMENTER_TOP.asItem(), ModBlocks.FERMENTER_BOTTOM.asItem(), "fermenter_bottom_convert", 1);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_MIXER.asItem()).input('#', ModBlocks.FERMENTER_BOTTOM).input('-', ModItemTagProvider.IRON_INGOT).input('w', Items.REDSTONE_TORCH).input('O', Items.REPEATER).pattern(" - ").pattern(" # ").pattern("OwO").group("fermenter_mixer").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_HEATER.asItem()).input('#', ModBlocks.FERMENTER_BOTTOM).input('-', Blocks.MAGMA_BLOCK.asItem()).input('w', Items.REDSTONE).input('U', Items.QUARTZ).pattern(" - ").pattern(" # ").pattern("UwU").group("fermenter_heater").criterion(hasItem(Items.MAGMA_BLOCK), conditionsFromItem(Items.MAGMA_BLOCK)).offerTo(exporter);

        //distiller parts
        offer2x2CompactingRecipe(exporter, RecipeCategory.MISC, ModBlocks.DISTILLER_COLUMN, ModItems.NICKEL_PIPE);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DISTILLER_TOP).input('S', ModItemTagProvider.NICKEL_INGOT).input('H', ModBlocks.DISTILLER_COLUMN).input('E', ModItems.NICKEL_PIPE).pattern("S").pattern("H").pattern("E").group("distiller_top").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(ModItems.NICKEL_PIPE)).offerTo(exporter);
    }
}
