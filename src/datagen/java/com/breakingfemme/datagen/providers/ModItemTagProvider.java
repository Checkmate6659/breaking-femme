package com.breakingfemme.datagen.providers;

import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModFluids;
import com.breakingfemme.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static com.breakingfemme.ModTags.Item.*;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

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
        getOrCreateTagBuilder(BRICK).add(Items.BRICK);
        getOrCreateTagBuilder(WATER_BUCKET).add(Items.WATER_BUCKET);
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
        getOrCreateTagBuilder(PULVERIZED_IRON).add(ModItems.PULVERIZED_IRON);
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
        getOrCreateTagBuilder(SALT).add(ModItems.ROCK_SALT);
        getOrCreateTagBuilder(FOODS).add(ModItems.CHILI_PEPPER);
        getOrCreateTagBuilder(FOODS).add(ModItems.DRIED_CHILI_PEPPER);
        getOrCreateTagBuilder(FOODS).add(ModItems.GROUND_CHILI_PEPPER);
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
        getOrCreateTagBuilder(SAWDUST1).add(ModItems.SAWDUST);
        getOrCreateTagBuilder(SAWDUST2).add(ModItems.SAWDUST);

        getOrCreateTagBuilder(FILTER).add(Items.PAPER);
        //add create list filters into filter tag; using addOptional for when the mod isn't a dependency or sth
        getOrCreateTagBuilder(FILTER).addOptional(Identifier.of("create", "filter"));
        getOrCreateTagBuilder(FILTER).addOptional(Identifier.of("create", "attribute_filter"));
        getOrCreateTagBuilder(FILTER).addOptional(Identifier.of("create", "package_filter"));
        //ceramic filter
        getOrCreateTagBuilder(RESISTANT_FILTER).add(ModItems.CERAMIC_FILTER);
        getOrCreateTagBuilder(FILTER).addTag(RESISTANT_FILTER); //need to add this after resistant_filter has been created

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
        getOrCreateTagBuilder(FREE_CHEST).addOptional(Identifier.of("sophisticatedbackpacks", "backpack"));
        getOrCreateTagBuilder(FREE_CHEST).addOptional(Identifier.of("estrogen", "moth_elytra"));
    }
}
