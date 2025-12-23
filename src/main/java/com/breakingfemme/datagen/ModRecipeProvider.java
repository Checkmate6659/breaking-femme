package com.breakingfemme.datagen;

import java.util.List;
import java.util.function.Consumer;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    void offerPulverizing(Consumer<RecipeJsonProvider> exporter, TagKey<Item> input, ItemConvertible output, String group, String theStupidCriterion)
    {
        //can pulverize less ingots, but less sand-efficient
        for(int i = 1; i < 9; i++)
            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, i).input(ItemTags.SAND).input(Ingredient.fromTag(input), i).group(group).criterion(theStupidCriterion, conditionsFromTag(input)).offerTo(exporter, new Identifier(getRecipeName(output) + String.valueOf(i)));
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        //pulverizing copper/nickel
        offerPulverizing(exporter, ModItemTagProvider.COPPER_INGOT, ModItems.PULVERIZED_COPPER, "pulverized_copper", hasItem(Items.COPPER_INGOT));
        offerPulverizing(exporter, ModItemTagProvider.NICKEL_INGOT, ModItems.PULVERIZED_NICKEL, "pulverized_nickel", hasItem(ModItems.NICKEL_INGOT));

        //re-casting pulverized copper/nickel (can't just use offerSmelting because it doesn't behave well with tags)
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 200).criterion(hasItem(ModItems.PULVERIZED_COPPER), conditionsFromTag(ModItemTagProvider.PULVERIZED_COPPER)).offerTo(exporter, "copper_ingot_from_remelting");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 100).criterion(hasItem(ModItems.PULVERIZED_COPPER), conditionsFromTag(ModItemTagProvider.PULVERIZED_COPPER)).offerTo(exporter, "copper_ingot_from_remelting_blasting");
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 200).criterion(hasItem(ModItems.PULVERIZED_NICKEL), conditionsFromTag(ModItemTagProvider.PULVERIZED_NICKEL)).offerTo(exporter, "nickel_ingot_from_remelting");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.fromTag(ModItemTagProvider.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 100).criterion(hasItem(ModItems.PULVERIZED_NICKEL), conditionsFromTag(ModItemTagProvider.PULVERIZED_NICKEL)).offerTo(exporter, "nickel_ingot_from_remelting_blasting");

        //fermenter parts
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_PANEL.asItem(), 16).input('#', ItemTags.PLANKS).input('-', Items.IRON_BARS).pattern("###").pattern("---").pattern("###").group("fermenter_panel").criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_TOP.asItem(), 4).input('#', ItemTags.PLANKS).input('-', Items.IRON_INGOT).pattern(" - ").pattern("###").group("fermenter_top").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        offerShapelessRecipe(exporter, ModBlocks.FERMENTER_BOTTOM.asItem(), ModBlocks.FERMENTER_TOP.asItem(), "fermenter_top_convert", 1);
        offerShapelessRecipe(exporter, ModBlocks.FERMENTER_TOP.asItem(), ModBlocks.FERMENTER_BOTTOM.asItem(), "fermenter_bottom_convert", 1);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_MIXER.asItem()).input('#', ModBlocks.FERMENTER_BOTTOM).input('-', Items.IRON_INGOT).input('w', Items.REDSTONE_TORCH).input('O', Items.REPEATER).pattern(" - ").pattern(" # ").pattern("OwO").group("fermenter_mixer").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_HEATER.asItem()).input('#', ModBlocks.FERMENTER_BOTTOM).input('-', Blocks.MAGMA_BLOCK.asItem()).input('w', Items.REDSTONE).input('U', Items.QUARTZ).pattern(" - ").pattern(" # ").pattern("UwU").group("fermenter_heater").criterion(hasItem(Items.MAGMA_BLOCK), conditionsFromItem(Items.MAGMA_BLOCK)).offerTo(exporter);

        //coal tar (can only use coal, NOT wood/charcoal, as it's not the same chemicals irl, wood tar doesn't contain nearly as much benzene or naphthalene for instance)
        offerSmelting(exporter, List.of(Items.COAL), RecipeCategory.MISC, ModItems.TAR, 0, 200, "tar");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModFluids.TAR_BUCKET).input(Items.BUCKET).input(Ingredient.ofItems(ModItems.TAR), 8).group("tar_bucket").criterion(hasItem(ModItems.TAR), conditionsFromItem(ModItems.TAR)).offerTo(exporter, "tar_bucket");
    }
}
