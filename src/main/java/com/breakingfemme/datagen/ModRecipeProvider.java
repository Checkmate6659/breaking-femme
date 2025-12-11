package com.breakingfemme.datagen;

import java.util.List;
import java.util.function.Consumer;

import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    void offerPulverizing(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible output, String group)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, 8).input(ItemTags.SAND).input(input, 8).group(group).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        //pulverizing copper/nickel
        //ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PULVERIZED_COPPER, 8).input(Blocks.SAND).input(Items.COPPER_INGOT, 8).group("pulverized_copper").criterion("has_sand", conditionsFromItem(Blocks.SAND)).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT)).offerTo(exporter);
        offerPulverizing(exporter, Items.COPPER_INGOT, ModItems.PULVERIZED_COPPER, "pulverized_copper");
        offerPulverizing(exporter, ModItems.NICKEL_INGOT, ModItems.PULVERIZED_NICKEL, "pulverized_nickel");

        //re-casting pulverized copper/nickel
        offerSmelting(exporter, List.of(ModItems.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 200, "copper_ingot");
        offerBlasting(exporter, List.of(ModItems.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 100, "copper_ingot");
        offerSmelting(exporter, List.of(ModItems.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 200, "nickel_ingot");
        offerBlasting(exporter, List.of(ModItems.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 100, "nickel_ingot");
    }
}
