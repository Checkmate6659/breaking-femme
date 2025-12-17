package com.breakingfemme.datagen;

import java.util.List;
import java.util.function.Consumer;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    void offerPulverizing(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible output, String group)
    {
        for(int i = 1; i < 9; i++) //can pulverize less items, but less sand-efficient
            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, i).input(ItemTags.SAND).input(input, i).group(group).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter, new Identifier(getRecipeName(output) + String.valueOf(i)));
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        //pulverizing copper/nickel
        offerPulverizing(exporter, Items.COPPER_INGOT, ModItems.PULVERIZED_COPPER, "pulverized_copper");
        offerPulverizing(exporter, ModItems.NICKEL_INGOT, ModItems.PULVERIZED_NICKEL, "pulverized_nickel");

        //re-casting pulverized copper/nickel
        offerSmelting(exporter, List.of(ModItems.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 200, "copper_ingot");
        offerBlasting(exporter, List.of(ModItems.PULVERIZED_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0, 100, "copper_ingot");
        offerSmelting(exporter, List.of(ModItems.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 200, "nickel_ingot");
        offerBlasting(exporter, List.of(ModItems.PULVERIZED_NICKEL), RecipeCategory.MISC, ModItems.NICKEL_INGOT, 0, 100, "nickel_ingot");

        //fermenter panels
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.FERMENTER_PANEL.asItem(), 16).input('#', ItemTags.PLANKS).input('-', Items.IRON_BARS).pattern("###").pattern("---").pattern("###").group("fermenter_panel").criterion(hasItem(Items.IRON_BARS), conditionsFromItem(Items.IRON_BARS)).offerTo(exporter);
    }
}
