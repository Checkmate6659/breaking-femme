package com.breakingfemme;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static com.breakingfemme.BreakingFemme.id;

public class ModTags {
    public static void init() {
        Block.init();
        Item.init();
        Fluid.init();
        Entity.init();
    }

    private ModTags() {
        throw new AssertionError();
    }

    public static class Block {
        public static void init() {
        }

        //custom block tags (NOT in common tags)
        public static final TagKey<net.minecraft.block.Block> HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "hot"));
        public static final TagKey<net.minecraft.block.Block> FURNACE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "furnaces"));
        //trying to make something compatible with blaze burners/low heaters etc (create & addons)
        public static final TagKey<net.minecraft.block.Block> CREATE_HOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("create", "passive_boiler_heaters"));
        public static final TagKey<net.minecraft.block.Block> COLD = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "cold"));
        public static final TagKey<net.minecraft.block.Block> STONES = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "stones"));
        public static final TagKey<net.minecraft.block.Block> ORES_STONE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores_in_ground/stone"));
        public static final TagKey<net.minecraft.block.Block> ORES_DEEPSLATE = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores_in_ground/deepslate"));
        public static final TagKey<net.minecraft.block.Block> NICKEL_ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores/nickel"));
        public static final TagKey<net.minecraft.block.Block> NICKEL_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "storage_blocks/nickel"));
        public static final TagKey<net.minecraft.block.Block> RUBY_ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "ores/ruby"));
        public static final TagKey<net.minecraft.block.Block> RUBY_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "storage_blocks/ruby"));
        //fermenter panel tags (addition of modded panels?)
        public static final TagKey<net.minecraft.block.Block> FERMENTER_SIDE_PANEL = TagKey.of(RegistryKeys.BLOCK, id("fermenter_side_panel"));
        public static final TagKey<net.minecraft.block.Block> FERMENTER_TOP_PANEL = TagKey.of(RegistryKeys.BLOCK, id("fermenter_top_panel"));
        public static final TagKey<net.minecraft.block.Block> FERMENTER_BOTTOM_PANEL = TagKey.of(RegistryKeys.BLOCK, id("fermenter_bottom_panel"));
        public static final TagKey<net.minecraft.block.Block> FERMENTER_AIRLOCK = TagKey.of(RegistryKeys.BLOCK, id("fermenter_airlock"));
        public static final TagKey<net.minecraft.block.Block> FERMENTER_HEATER = TagKey.of(RegistryKeys.BLOCK, id("fermenter_heater"));
        public static final TagKey<net.minecraft.block.Block> FERMENTER_MIXER = TagKey.of(RegistryKeys.BLOCK, id("fermenter_mixer"));

        private Block() {
            throw new AssertionError();
        }

    }

    public static class Fluid {
        public static void init() {
        }

        public static final TagKey<net.minecraft.fluid.Fluid> WATER_LIKE = TagKey.of(RegistryKeys.FLUID, id("water_like")); //basically water, but without the interaction or the fire extinguishing
        public static final TagKey<net.minecraft.fluid.Fluid> FLAMMABLE = TagKey.of(RegistryKeys.FLUID, id("flammable"));
        public static final TagKey<net.minecraft.fluid.Fluid> HARSH_ON_FILTERS = TagKey.of(RegistryKeys.FLUID, id("filter/harsh"));

        private Fluid() {
            throw new AssertionError();
        }
    }

    public static class Item {
        public static void init() {
        }

        private Item() {
            throw new AssertionError();
        }

        //common tags for compatibility with other mods
        //https://wiki.fabricmc.net/community:common_tags
        //a lot more stuff here, but only vanilla: https://github.com/FabricMC/fabric-api/tree/HEAD/fabric-convention-tags-v2/src/generated/resources/data/c/tags
        //generation code: https://github.com/FabricMC/fabric-api/tree/dd6ff61bde15cc0dc6b2ff28866419fd4732082d/fabric-convention-tags-v2/src/datagen/java/net/fabricmc/fabric/impl/tag/convention/datagen/generators
        //also check this out
        //https://github.com/MehVahdJukaar/Supplementaries/wiki/Mod-Integration
        public static final TagKey<net.minecraft.item.Item> STONES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "stones"));
        public static final TagKey<net.minecraft.item.Item> NICKEL_ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores/nickel")); //non-conventional
        public static final TagKey<net.minecraft.item.Item> RUBY_ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores/ruby"));
        public static final TagKey<net.minecraft.item.Item> ORES_STONE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores_in_ground/stone"));
        public static final TagKey<net.minecraft.item.Item> ORES_DEEPSLATE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores_in_ground/deepslate"));
        public static final TagKey<net.minecraft.item.Item> NICKEL_BLOCK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "storage_blocks/nickel")); //non-conventional
        public static final TagKey<net.minecraft.item.Item> RUBY_BLOCK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "storage_blocks/ruby"));
        public static final TagKey<net.minecraft.item.Item> RAW_NICKEL = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "raw_materials/nickel")); //non-conventional
        public static final TagKey<net.minecraft.item.Item> RUBY_GEM = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "gems/ruby"));
        public static final TagKey<net.minecraft.item.Item> IRON_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/iron"));
        public static final TagKey<net.minecraft.item.Item> COPPER_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/copper"));
        public static final TagKey<net.minecraft.item.Item> NICKEL_INGOT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots/nickel"));
        public static final TagKey<net.minecraft.item.Item> METAL_PIPE = TagKey.of(RegistryKeys.ITEM, id("metal_pipe"));
        public static final TagKey<net.minecraft.item.Item> PULVERIZED_IRON = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/iron"));
        public static final TagKey<net.minecraft.item.Item> PULVERIZED_COPPER = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/copper"));
        public static final TagKey<net.minecraft.item.Item> PULVERIZED_NICKEL = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/nickel"));
        public static final TagKey<net.minecraft.item.Item> PULVERIZED_ALUMINUM = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/aluminum"));
        public static final TagKey<net.minecraft.item.Item> SALT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/salt"));
        public static final TagKey<net.minecraft.item.Item> FLOUR = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "flours"));
        public static final TagKey<net.minecraft.item.Item> FLOUR_WHEAT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "flours/wheat"));
        public static final TagKey<net.minecraft.item.Item> YEAST = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "yeast")); //non-conventional
        public static final TagKey<net.minecraft.item.Item> DOUGH1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dough"));
        public static final TagKey<net.minecraft.item.Item> DOUGH2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "doughs"));
        public static final TagKey<net.minecraft.item.Item> DOUGH_WHEAT = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "doughs/wheat"));
        public static final TagKey<net.minecraft.item.Item> FOODS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods"));
        public static final TagKey<net.minecraft.item.Item> FOODS_DOUGH = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/dough"));
        public static final TagKey<net.minecraft.item.Item> CROPS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "crops"));
        public static final TagKey<net.minecraft.item.Item> FRUIT1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/fruit"));
        public static final TagKey<net.minecraft.item.Item> FRUIT2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/fruits"));
        public static final TagKey<net.minecraft.item.Item> FRUIT3 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "fruits"));
        public static final TagKey<net.minecraft.item.Item> VEGETABLE1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/vegetable"));
        public static final TagKey<net.minecraft.item.Item> VEGETABLE2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "foods/vegetables"));
        public static final TagKey<net.minecraft.item.Item> VEGETABLE3 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "vegetables"));
        public static final TagKey<net.minecraft.item.Item> SAWDUST1 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/saw"));
        public static final TagKey<net.minecraft.item.Item> SAWDUST2 = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/wood"));
        public static final TagKey<net.minecraft.item.Item> DRINK_BOTTLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drink_containing/bottle"));
        public static final TagKey<net.minecraft.item.Item> DRINKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "drinks"));
        public static final TagKey<net.minecraft.item.Item> MILK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk"));
        public static final TagKey<net.minecraft.item.Item> MILK_BUCKET = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "buckets/milk"));
        //TODO: do we accept bottles? if yes, how do we add farmersdelight:milk_bottles? its registered as c:foods/milk, alongside minecraft milk buckets, in common tags
        public static final TagKey<net.minecraft.item.Item> MILK_BOTTLE = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "milk_bottles")); //counts as 1/3 bucket; usefulfood:milkbottle
        public static final TagKey<net.minecraft.item.Item> CLAY_DUST = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "dusts/clay"));
        public static final TagKey<net.minecraft.item.Item> BRICK = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "bricks/normal"));
        public static final TagKey<net.minecraft.item.Item> WATER_BUCKET = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "buckets/water"));
        public static final TagKey<net.minecraft.item.Item> FILTER = TagKey.of(RegistryKeys.ITEM, id("filter"));
        public static final TagKey<net.minecraft.item.Item> RESISTANT_FILTER = TagKey.of(RegistryKeys.ITEM, id("resistant_filter"));
        public static final TagKey<net.minecraft.item.Item> BLAZE_BURNER_FUEL = TagKey.of(RegistryKeys.ITEM, new Identifier("create", "blaze_burner_fuel/regular")); //regular fuel (estrone and estradiol, and chili)
        public static final TagKey<net.minecraft.item.Item> BLAZE_BURNER_SUPER = TagKey.of(RegistryKeys.ITEM, new Identifier("create", "blaze_burner_fuel/special")); //superheating fuel (chili estradiol)
        public static final TagKey<net.minecraft.item.Item> BLAZE_BURNER_FUEL_CUSTOM = TagKey.of(RegistryKeys.ITEM, id("blaze_burner_custom_duration")); //custom fuel duration (set in BlazeBurnerFuelTimeMixin)
        //armor tags
        //TODO: reimpl this https://github.com/MayaqqDev/Estrogen/blob/kotlin/src/main/java/dev/mayaqq/estrogen/mixin/client/PlayerModelMixin.java
        //and mb fix some stuff, theres some dodgy texture grabbing going on at the end of this
        public static final TagKey<net.minecraft.item.Item> ALREADY_FLEXIBLE = TagKey.of(RegistryKeys.ITEM, id("already_flexible"));
        public static final TagKey<net.minecraft.item.Item> FREE_CHEST = TagKey.of(RegistryKeys.ITEM, id("free_chest"));
    }

    public static class Entity {
        public static void init() {
        }

        private Entity() {
            throw new AssertionError();
        }

        /**
         * Tag for entities which can be affected by estrogen
         */
        public static final TagKey<EntityType<?>> ESTROGENABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, id("estrogenable"));
    }

}
