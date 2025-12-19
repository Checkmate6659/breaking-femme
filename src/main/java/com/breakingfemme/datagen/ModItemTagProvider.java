package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    //common tags for compatibility with other mods
    //wiki.fabricmc.net/community:common_tags
    public static final TagKey<Item> COPPER_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/copper"));
    public static final TagKey<Item> NICKEL_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/nickel"));
    public static final TagKey<Item> PULVERIZED_COPPER = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/copper"));
    public static final TagKey<Item> PULVERIZED_NICKEL = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/nickel"));
    public static final TagKey<Item> DRINK_BOTTLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drink_containing/bottle"));
    public static final TagKey<Item> DRINKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drinks"));
    public static final TagKey<Item> MILK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk"));
    public static final TagKey<Item> MILK_BUCKET = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "buckets/milk"));
    //TODO: do we accept bottles? if yes, how do we add farmersdelight:milk_bottles? its registered as c:foods/milk, alongside minecraft milk buckets, in common tags
    public static final TagKey<Item> MILK_BOTTLE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk_bottles")); //counts as 1/3 bucket; usefulfood:milkbottle

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(COPPER_INGOT).add(Items.COPPER_INGOT);
        getOrCreateTagBuilder(NICKEL_INGOT).add(ModItems.NICKEL_INGOT);
        getOrCreateTagBuilder(PULVERIZED_COPPER).add(ModItems.PULVERIZED_COPPER);
        getOrCreateTagBuilder(PULVERIZED_NICKEL).add(ModItems.PULVERIZED_NICKEL);
        getOrCreateTagBuilder(DRINKS).add(ModItems.ET32_BOTTLE, ModItems.ET64_BOTTLE, ModItems.ET95_BOTTLE);
        getOrCreateTagBuilder(DRINK_BOTTLES).add(ModItems.ET32_BOTTLE, ModItems.ET64_BOTTLE, ModItems.ET95_BOTTLE);
        getOrCreateTagBuilder(MILK).add(Items.MILK_BUCKET);
        getOrCreateTagBuilder(MILK_BUCKET).add(Items.MILK_BUCKET);
    }
}
