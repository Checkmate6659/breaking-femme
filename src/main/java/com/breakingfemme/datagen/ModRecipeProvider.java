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
import net.minecraft.recipe.RecipeSerializer;
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

        //same for ruby
        offerSmelting(exporter, ImmutableList.of(ModBlocks.RUBY_ORE, ModBlocks.DEEPSLATE_RUBY_ORE), RecipeCategory.MISC, ModItems.RUBY, 1.0f, 200, "ruby");
        offerBlasting(exporter, ImmutableList.of(ModBlocks.RUBY_ORE, ModBlocks.DEEPSLATE_RUBY_ORE), RecipeCategory.MISC, ModItems.RUBY, 1.0f, 100, "ruby_blasting");
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.RUBY, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RUBY_BLOCK);

        //nickel-aluminum alloy stuff
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NI_AL_BLEND, 2)
            .input(ModItemTagProvider.PULVERIZED_NICKEL)
            .input(ModItemTagProvider.PULVERIZED_ALUMINUM)
            .group("nickel_aluminum_blend")
            .criterion(hasItem(ModItems.PULVERIZED_ALUMINUM), conditionsFromItem(ModItems.PULVERIZED_ALUMINUM))
            .offerTo(exporter, "nickel_aluminum_blend");
        offerSmelting(exporter, ImmutableList.of(ModItems.PULVERIZED_NI_AL, ModItems.NI_AL_BLEND), RecipeCategory.MISC, ModItems.NI_AL_INGOT, 1.0f, 200, "nickel_aluminum_ingot");
        offerBlasting(exporter, ImmutableList.of(ModItems.PULVERIZED_NI_AL, ModItems.NI_AL_BLEND), RecipeCategory.MISC, ModItems.NI_AL_INGOT, 1.0f, 100, "nickel_aluminum_ingot_blasting");

        //mortar and pestle
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.MORTAR_PESTLE).input('#', ModItemTagProvider.STONES).input('|', ModItemTagProvider.IRON_INGOT).pattern(" | ").pattern("#|#").pattern("###").group("mortar_pestle").criterion(hasItem(Blocks.STONE), conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS)).offerTo(exporter);

        //bread dough, and cooking it to get bread
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.DOUGH, 2)
            .input(ModItemTagProvider.YEAST)
            .input(ModItemTagProvider.FLOUR)
            .group("dough_with_yeast")
            .criterion(hasItem(ModItems.YEAST), conditionsFromItem(ModItems.YEAST))
            .offerTo(exporter, "dough_with_yeast");
        offerSmelting(exporter, ImmutableList.of(ModItems.DOUGH), RecipeCategory.FOOD, Items.BREAD, 0.15f, 200, "bread_smelt");
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, 100, ModItems.DOUGH, Items.BREAD, 0.15F);
        offerFoodCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600, ModItems.DOUGH, Items.BREAD, 0.15F);

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

        //chili pepper (can be cooked on a campfire and smoked to be dried, not just smelted) (sun drying would be cool too)
        offerSmelting(exporter, ImmutableList.of(ModItems.CHILI_PEPPER), RecipeCategory.FOOD, ModItems.DRIED_CHILI_PEPPER, 0.15f, 200, "dried_chili_pepper_smelt");
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, 100, ModItems.CHILI_PEPPER, ModItems.DRIED_CHILI_PEPPER, 0.15F);
        offerFoodCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600, ModItems.CHILI_PEPPER, ModItems.DRIED_CHILI_PEPPER, 0.15F);

        //kelp ash. you can smelt/blast dried kelp blocks into kelp ash; also featuring kelp ash blocks!
        offerSmelting(exporter, ImmutableList.of(Items.DRIED_KELP_BLOCK), RecipeCategory.MISC, ModItems.KELP_ASH, 0.0f, 200, "kelp_ash_smelt");
        offerBlasting(exporter, ImmutableList.of(Items.DRIED_KELP_BLOCK), RecipeCategory.MISC, ModItems.KELP_ASH, 0.0f, 100, "kelp_ash_blast");
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.KELP_ASH, RecipeCategory.BUILDING_BLOCKS, ModBlocks.KELP_ASH_BLOCK);

        //lye water recipe in crafting grid
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.LYE_WATER_BOTTLE, 1)
            .input(ModBlocks.KELP_ASH_MUD_BLOCK)
            .input(Items.GLASS_BOTTLE)
            .input(ItemTags.WOOL_CARPETS)
            .input(Items.STICK)
            .group("lye_water_bottle")
            .criterion(hasItem(ModBlocks.KELP_ASH_MUD_BLOCK), conditionsFromItem(ModBlocks.KELP_ASH_MUD_BLOCK))
            .offerTo(exporter, "lye_water_bottle");
        
        //lime calcination
        offerSmelting(exporter, ImmutableList.of(ModBlocks.LIMESTONE_CHUNKS), RecipeCategory.MISC, ModBlocks.QUICKLIME, 0.0f, 200, "lime_smelt");
        offerBlasting(exporter, ImmutableList.of(ModBlocks.LIMESTONE_CHUNKS), RecipeCategory.MISC, ModBlocks.QUICKLIME, 0.0f, 100, "lime_blast");
    }
}
