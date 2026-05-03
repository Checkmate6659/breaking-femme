package com.breakingfemme.datagen;

import java.util.concurrent.CompletableFuture;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    //common tags for compatibility with other mods
    //https://wiki.fabricmc.net/community:common_tags
    //a lot more stuff here, but only vanilla: https://github.com/FabricMC/fabric-api/tree/HEAD/fabric-convention-tags-v2/src/generated/resources/data/c/tags
    //generation code: https://github.com/FabricMC/fabric-api/tree/dd6ff61bde15cc0dc6b2ff28866419fd4732082d/fabric-convention-tags-v2/src/datagen/java/net/fabricmc/fabric/impl/tag/convention/datagen/generators
    //also check this out
    //https://github.com/MehVahdJukaar/Supplementaries/wiki/Mod-Integration
    public static final TagKey<Item> STONES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "stones"));
    public static final TagKey<Item> NICKEL_ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores/nickel")); //non-conventional
    public static final TagKey<Item> RUBY_ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores/ruby"));
    public static final TagKey<Item> ORES_STONE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores_in_ground/stone"));
    public static final TagKey<Item> ORES_DEEPSLATE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores_in_ground/deepslate"));
    public static final TagKey<Item> NICKEL_BLOCK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "storage_blocks/nickel")); //non-conventional
    public static final TagKey<Item> RUBY_BLOCK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "storage_blocks/ruby"));
    public static final TagKey<Item> RAW_NICKEL = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "raw_materials/nickel")); //non-conventional
    public static final TagKey<Item> RUBY_GEM = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "gems/ruby"));
    public static final TagKey<Item> IRON_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/iron"));
    public static final TagKey<Item> COPPER_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/copper"));
    public static final TagKey<Item> NICKEL_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/nickel"));
    public static final TagKey<Item> METAL_PIPE = TagKey.of(RegistryKeys.ITEM, new Identifier(BreakingFemme.MOD_ID, "metal_pipe"));
    public static final TagKey<Item> PULVERIZED_COPPER = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/copper"));
    public static final TagKey<Item> PULVERIZED_NICKEL = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/nickel"));
    public static final TagKey<Item> PULVERIZED_ALUMINUM = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/aluminum"));
    public static final TagKey<Item> FLOUR = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "flours"));
    public static final TagKey<Item> FLOUR_WHEAT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "flours/wheat"));
    public static final TagKey<Item> YEAST = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "yeast")); //non-conventional
    public static final TagKey<Item> DOUGH1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dough"));
    public static final TagKey<Item> DOUGH2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "doughs"));
    public static final TagKey<Item> DOUGH_WHEAT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "doughs/wheat"));
    public static final TagKey<Item> FOODS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods"));
    public static final TagKey<Item> FOODS_DOUGH = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/dough"));
    public static final TagKey<Item> CROPS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "crops"));
    public static final TagKey<Item> FRUIT1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/fruit"));
    public static final TagKey<Item> FRUIT2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/fruits"));
    public static final TagKey<Item> FRUIT3 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "fruits"));
    public static final TagKey<Item> VEGETABLE1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/vegetable"));
    public static final TagKey<Item> VEGETABLE2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/vegetables"));
    public static final TagKey<Item> VEGETABLE3 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "vegetables"));
    public static final TagKey<Item> DRINK_BOTTLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drink_containing/bottle"));
    public static final TagKey<Item> DRINKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drinks"));
    public static final TagKey<Item> MILK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk"));
    public static final TagKey<Item> MILK_BUCKET = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "buckets/milk"));
    //TODO: do we accept bottles? if yes, how do we add farmersdelight:milk_bottles? its registered as c:foods/milk, alongside minecraft milk buckets, in common tags
    public static final TagKey<Item> MILK_BOTTLE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk_bottles")); //counts as 1/3 bucket; usefulfood:milkbottle
    public static final TagKey<Item> FILTER_PAPER = TagKey.of(RegistryKeys.ITEM, new Identifier(BreakingFemme.MOD_ID, "filter_paper"));
    public static final TagKey<Item> BLAZE_BURNER_FUEL = TagKey.of(RegistryKeys.ITEM, new Identifier("create", "blaze_burner_fuel/regular")); //regular fuel (estrone and estradiol, and chili)
    public static final TagKey<Item> BLAZE_BURNER_SUPER = TagKey.of(RegistryKeys.ITEM, new Identifier("create", "blaze_burner_fuel/special")); //superheating fuel (chili estradiol)
    public static final TagKey<Item> BLAZE_BURNER_FUEL_CUSTOM = TagKey.of(RegistryKeys.ITEM, new Identifier(BreakingFemme.MOD_ID, "blaze_burner_custom_duration")); //custom fuel duration (set in BlazeBurnerFuelTimeMixin)
    
    //armor tags
    //TODO: reimpl this https://github.com/MayaqqDev/Estrogen/blob/kotlin/src/main/java/dev/mayaqq/estrogen/mixin/client/PlayerModelMixin.java
    //and mb fix some stuff, theres some dodgy texture grabbing going on at the end of this
    public static final TagKey<Item> ALREADY_FLEXIBLE = TagKey.of(RegistryKeys.ITEM, new Identifier(BreakingFemme.MOD_ID, "already_flexible"));
    public static final TagKey<Item> FREE_CHEST = TagKey.of(RegistryKeys.ITEM, new Identifier(BreakingFemme.MOD_ID, "free_chest"));

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        //item tags
        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS).add(ModItems.ACT_RIGHT_MUSIC_DISC);
        getOrCreateTagBuilder(METAL_PIPE).add(ModItems.NICKEL_PIPE);
        getOrCreateTagBuilder(STONES).add(Blocks.STONE.asItem());
        getOrCreateTagBuilder(STONES).add(Blocks.DEEPSLATE.asItem());
        getOrCreateTagBuilder(STONES).add(Blocks.GRANITE.asItem());
        getOrCreateTagBuilder(STONES).add(Blocks.DIORITE.asItem());
        getOrCreateTagBuilder(STONES).add(Blocks.ANDESITE.asItem());
        getOrCreateTagBuilder(STONES).add(Blocks.TUFF.asItem());
        getOrCreateTagBuilder(NICKEL_BLOCK).add(ModBlocks.NICKEL_BLOCK.asItem());
        getOrCreateTagBuilder(NICKEL_ORES).add(ModBlocks.NICKEL_ORE.asItem());
        getOrCreateTagBuilder(NICKEL_ORES).add(ModBlocks.DEEPSLATE_NICKEL_ORE.asItem());
        getOrCreateTagBuilder(ORES_STONE).add(ModBlocks.NICKEL_ORE.asItem());
        getOrCreateTagBuilder(ORES_DEEPSLATE).add(ModBlocks.DEEPSLATE_NICKEL_ORE.asItem());
        getOrCreateTagBuilder(RUBY_BLOCK).add(ModBlocks.RUBY_BLOCK.asItem());
        getOrCreateTagBuilder(RUBY_ORES).add(ModBlocks.RUBY_ORE.asItem());
        getOrCreateTagBuilder(RUBY_ORES).add(ModBlocks.DEEPSLATE_RUBY_ORE.asItem());
        getOrCreateTagBuilder(ORES_STONE).add(ModBlocks.RUBY_ORE.asItem());
        getOrCreateTagBuilder(ORES_DEEPSLATE).add(ModBlocks.DEEPSLATE_RUBY_ORE.asItem());
        getOrCreateTagBuilder(RAW_NICKEL).add(ModItems.RAW_NICKEL);
        getOrCreateTagBuilder(IRON_INGOT).add(Items.IRON_INGOT);
        getOrCreateTagBuilder(COPPER_INGOT).add(Items.COPPER_INGOT);
        getOrCreateTagBuilder(NICKEL_INGOT).add(ModItems.NICKEL_INGOT);
        getOrCreateTagBuilder(PULVERIZED_COPPER).add(ModItems.PULVERIZED_COPPER);
        getOrCreateTagBuilder(PULVERIZED_NICKEL).add(ModItems.PULVERIZED_NICKEL);
        getOrCreateTagBuilder(PULVERIZED_ALUMINUM).add(ModItems.PULVERIZED_ALUMINUM);
        getOrCreateTagBuilder(DRINKS).add(ModItems.ET32_BOTTLE, ModItems.ET64_BOTTLE, ModItems.ET95_BOTTLE);
        getOrCreateTagBuilder(DRINK_BOTTLES).add(ModItems.ET32_BOTTLE, ModItems.ET64_BOTTLE, ModItems.ET95_BOTTLE);
        getOrCreateTagBuilder(MILK).add(Items.MILK_BUCKET);
        getOrCreateTagBuilder(MILK_BUCKET).add(Items.MILK_BUCKET);
        getOrCreateTagBuilder(FLOUR).add(ModItems.FLOUR);
        getOrCreateTagBuilder(FLOUR_WHEAT).add(ModItems.FLOUR);
        getOrCreateTagBuilder(YEAST).add(ModItems.YEAST);
        getOrCreateTagBuilder(DOUGH1).add(ModItems.DOUGH);
        getOrCreateTagBuilder(DOUGH2).add(ModItems.DOUGH);
        getOrCreateTagBuilder(DOUGH_WHEAT).add(ModItems.DOUGH);
        getOrCreateTagBuilder(FOODS).add(ModItems.DOUGH);
        getOrCreateTagBuilder(FOODS_DOUGH).add(ModItems.DOUGH);
        getOrCreateTagBuilder(CROPS).add(ModItems.SOYBEANS);
        getOrCreateTagBuilder(CROPS).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(FOODS).add(ModItems.SOYBEANS);
        getOrCreateTagBuilder(FRUIT1).add(ModItems.CHILI_PEPPER); //in cooking peppers are referred to as vegetables, but botanically theyre fruits
        getOrCreateTagBuilder(FRUIT1).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT1).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT2).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT2).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT2).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT3).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT3).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(FRUIT3).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE1).add(ModItems.CHILI_PEPPER); //but im gonna add it as a vegetable anyway
        getOrCreateTagBuilder(VEGETABLE1).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE1).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE2).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE2).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE2).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE3).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE3).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(VEGETABLE3).add(ModItems.GROUND_CHILI_PEPPER);
        getOrCreateTagBuilder(FILTER_PAPER).add(Items.PAPER);
        //TODO: add create list filters into filter paper tag!
        //getOrCreateTagBuilder(FILTER_PAPER).add(Identifier.of("create", "filter"));
        //getOrCreateTagBuilder(FILTER_PAPER).add(Identifier.of("create", "attribute_filter"));
        //getOrCreateTagBuilder(FILTER_PAPER).add(Identifier.of("create", "package_filter"));

        //blaze burner fuels
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL).addTag(BLAZE_BURNER_FUEL_CUSTOM); //for custom duration fuels
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModItems.CRUDE_ESTRONE); //4800
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModItems.PURE_ESTRONE); //6400
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET); //16000
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModItems.PURE_ESTRADIOL_CRYSTALS); //32000
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModItems.PURE_ESTRADIOL_POWDER); //32000
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL_CUSTOM).add(ModItems.EGEL_BOTTLE); //12000
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL).add(ModItems.CHILI_PEPPER); //same as coal: 1600
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(BLAZE_BURNER_FUEL).add(ModItems.GROUND_CHILI_PEPPER);
        //TODO: chili estrogen superheats! (TODO: override superheating to superheat longer than for 3200t i.e. 160s)

        //armor tags
        getOrCreateTagBuilder(ALREADY_FLEXIBLE).addTag(FREE_CHEST);
        getOrCreateTagBuilder(ALREADY_FLEXIBLE).add(Items.LEATHER_CHESTPLATE);
        getOrCreateTagBuilder(ALREADY_FLEXIBLE).add(Items.CHAINMAIL_CHESTPLATE);
        getOrCreateTagBuilder(FREE_CHEST).add(Items.ELYTRA);
        //getOrCreateTagBuilder(FREE_CHEST).add(Identifier.of("sophisticatedbackpacks", "backpack")); //no. this doesnt allow me to add stuff without having the mod as a compile time dependency.
    }
}
