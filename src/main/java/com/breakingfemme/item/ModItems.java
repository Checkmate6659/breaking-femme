package com.breakingfemme.item;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModSounds;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    //Music disc (Femtanyl - Act Right) (CC BY-SA license, found here: https://www.newgrounds.com/audio/listen/1265451)
    public static final Item ACT_RIGHT_MUSIC_DISC = registerItem("music_disc_act_right", new MusicDiscItem(15, ModSounds.ACT_RIGHT, new FabricItemSettings().maxCount(1), 144));

    //Chili pepper, a nice additive to your products
    public static final Item CHILI_PEPPER = registerItem("chili_pepper", new ChiliPepperItem(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(4f).alwaysEdible().build()), false, true));
    public static final Item DRIED_CHILI_PEPPER = registerItem("dried_chili_pepper", new ChiliPepperItem(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(4f).alwaysEdible().build()), false, false));
    public static final Item GROUND_CHILI_PEPPER = registerItem("ground_chili_pepper", new ChiliPepperItem(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(4f).alwaysEdible().build()), true, false));

    //Soybeans as a sterol source
    public static final Item SOYBEANS = registerItem("soybeans", new AliasedBlockItem(ModBlocks.SOY_CROP,
        new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.25f).build())));

    //Copper, Nickel, Ruby
    public static final Item RAW_NICKEL = registerItem("raw_nickel", new Item(new FabricItemSettings()));
    public static final Item NICKEL_INGOT = registerItem("nickel_ingot", new Item(new FabricItemSettings()));
    public static final Item RUBY = registerItem("ruby", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_COPPER = registerItem("pulverized_copper", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_NICKEL = registerItem("pulverized_nickel", new Item(new FabricItemSettings()));
    public static final Item COPPER_SULFATE = registerItem("copper_sulfate", new CopperSulfateItem(new FabricItemSettings()));
    public static final Item NICKEL_SULFATE = registerItem("nickel_sulfate", new Item(new FabricItemSettings()));

    //Aluminum and Raney nickel stuff
    public static final Item ALUMINUM_SCRAP = registerItem("aluminum_scrap", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_ALUMINUM = registerItem("pulverized_aluminum", new Item(new FabricItemSettings()));
    public static final Item NI_AL_BLEND = registerItem("nickel_aluminum_blend", new Item(new FabricItemSettings()));
    public static final Item NI_AL_INGOT = registerItem("nickel_aluminum_alloy_ingot", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_NI_AL = registerItem("pulverized_nickel_aluminum_alloy", new Item(new FabricItemSettings()));
    public static final Item RANEY_NICKEL = registerItem("raney_nickel", new Item(new FabricItemSettings()));
    //TODO: shove pulverized nickel aluminum alloy in caustic soda solution to make raney nickel

    //milk processing items
    public static final Item INGOT_MOLD = registerItem("ingot_mold", new Item(new FabricItemSettings()));
    public static final Item SKIMMED_MILK_BUCKET = registerItem("skimmed_milk_bucket", new MilkBucketItem(new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Item MILKGOT_MOLD = registerItem("milkgot_mold", new FullMoldItem(new FabricItemSettings()));
    public static final Item MILKGOT = registerItem("milkgot", new Item(new FabricItemSettings()));
    public static final Item SMP = registerItem("skimmed_milk_powder", new Item(new FabricItemSettings()));
    public static final Item CREAMGOT_MOLD = registerItem("creamgot_mold", new FullMoldItem(new FabricItemSettings()));
    public static final Item CREAMGOT = registerItem("creamgot", new Item(new FabricItemSettings()));

    //mortar and pestle
    public static final Item MORTAR_PESTLE = registerItem("mortar_pestle", new MortarPestleItem(new FabricItemSettings().maxDamage(64)));

    //Flour and yeast, to make bread and beer
    public static final Item FLOUR = registerItem("wheat_flour", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).saturationModifier(0.5f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.0625F).build()))); //1/16 chance only to have problems (probably not realistic)
    public static final Item YEAST = registerItem("yeast", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(1f).build())));
    public static final Item DOUGH = registerItem("wheat_dough", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(3).saturationModifier(1f).build())));

    //Bottles of alcoholic "beverages"
    public static final Item BEER_BOTTLE = registerItem("beer_bottle", new AlcoholDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build()), 0));
    public static final Item NETHER_BEER_BOTTLE = registerItem("nether_beer_bottle", new AlcoholDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build()), 1));
    public static final Item ET32_BOTTLE = registerItem("ethanol32_bottle", new AlcoholDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build()), 1));
    public static final Item ET64_BOTTLE = registerItem("ethanol64_bottle", new AlcoholDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build()), 2));
    public static final Item ET95_BOTTLE = registerItem("ethanol95_bottle", new AlcoholDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build()), 3));

    //Coal tar processing
    public static final Item TAR = registerItem("tar", new Item(new FabricItemSettings()));
    public static final Item NICKEL_PIPE = registerItem("nickel_pipe", new MetalPipeItem(new FabricItemSettings()));

    public static final Item COAL_OIL_BOTTLE = registerItem("coal_oil_bottle", new Item(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE)));
    public static final Item ANDROSTADIENEDIONE_BOTTLE = registerItem("androstadienedione_solution_bottle", new LethalDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE)));
    public static final Item ANDROSTADIENEDIONE_OIL_BOTTLE = registerItem("androstadienedione_oil_solution_bottle", new LethalDrinkItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE)));

    public static final Item STEROLS = registerItem("sterols", new Item(new FabricItemSettings()));
    public static final Item CRUDE_ESTRONE = registerItem("crude_estrone", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRONE = registerItem("pure_estrone", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRADIOL_CRYSTALS = registerItem("pure_estradiol_crystals", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRADIOL_POWDER = registerItem("pure_estradiol_powder", new Item(new FabricItemSettings()));
    public static final Item HORSE_JUICE = registerItem("conjugated_equine_estrogens", new Item(new FabricItemSettings())); //compat with Mayaqq's Estrogen (if i can make that work)

    //Ceramic filters for filtering harsh chemicals
    public static final Item SAWDUST = registerItem("sawdust", new Item(new FabricItemSettings()));
    public static final Item UNFIRED_CERAMIC_FILTER = registerItem("unfired_ceramic_filter", new Item(new FabricItemSettings()));
    public static final Item CERAMIC_FILTER = registerItem("ceramic_filter", new Item(new FabricItemSettings().maxDamage(256)));

    //Soda ash processing line
    public static final Item KELP_ASH = registerItem("kelp_ash", new Item(new FabricItemSettings()));
    public static final Item LYE_WATER_BOTTLE = registerItem("lye_water_bottle", new LyeWaterBottleItem(new FabricItemSettings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0f).alwaysEdible().build())));

    //Estrogens
    public static final Item EGEL_BOTTLE = registerItem("egel_bottle", new EgelItem(new FabricItemSettings().maxDamage(64)));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, name), item);
    }

    //TODO: potentially re-organize this later
    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
        new Identifier(BreakingFemme.MOD_ID, "breakingfemme"),
        FabricItemGroup.builder().displayName(Text.translatable("itemgroup.breakingfemme"))
        .icon(() -> new ItemStack(ModItems.PURE_ESTRADIOL_CRYSTALS)).entries((displayContext, entries) -> {
            entries.add(MORTAR_PESTLE);
            entries.add(UNFIRED_CERAMIC_FILTER);
            entries.add(CERAMIC_FILTER);
            entries.add(SAWDUST);
            entries.add(SOYBEANS);
            entries.add(ModBlocks.NICKEL_ORE);
            entries.add(ModBlocks.DEEPSLATE_NICKEL_ORE);
            entries.add(RAW_NICKEL);
            entries.add(NICKEL_INGOT);
            entries.add(ModBlocks.NICKEL_BLOCK);
            entries.add(PULVERIZED_COPPER);
            entries.add(PULVERIZED_NICKEL);
            entries.add(ModBlocks.RUBY_ORE);
            entries.add(ModBlocks.DEEPSLATE_RUBY_ORE);
            entries.add(RUBY);
            entries.add(ModBlocks.RUBY_BLOCK);
            entries.add(ALUMINUM_SCRAP);
            entries.add(PULVERIZED_ALUMINUM);
            entries.add(NI_AL_BLEND);
            entries.add(NI_AL_INGOT);
            entries.add(PULVERIZED_NI_AL);
            entries.add(RANEY_NICKEL);
            entries.add(COPPER_SULFATE);
            entries.add(NICKEL_SULFATE);
            entries.add(ModFluids.COPPER_SULFATE_BUCKET);
            entries.add(ModFluids.NICKEL_SULFATE_BUCKET);
            entries.add(FLOUR);
            entries.add(YEAST);
            entries.add(DOUGH);
            entries.add(ModFluids.BEER_BUCKET);
            entries.add(BEER_BOTTLE);
            entries.add(ModFluids.NETHER_BEER_BUCKET);
            entries.add(NETHER_BEER_BOTTLE);
            entries.add(ModFluids.ET32_BUCKET);
            entries.add(ET32_BOTTLE);
            entries.add(ModFluids.ET64_BUCKET);
            entries.add(ET64_BOTTLE);
            entries.add(ModFluids.ET95_BUCKET);
            entries.add(ET95_BOTTLE);
            entries.add(TAR);
            entries.add(ModFluids.TAR_BUCKET);
            entries.add(STEROLS);
            entries.add(ModFluids.STEROL_SOLUTION_BUCKET);
            entries.add(ModBlocks.MILK_SEPARATOR);
            entries.add(SKIMMED_MILK_BUCKET);
            entries.add(INGOT_MOLD);
            entries.add(MILKGOT_MOLD);
            entries.add(MILKGOT);
            entries.add(SMP);
            entries.add(CREAMGOT_MOLD);
            entries.add(CREAMGOT);
            entries.add(ModFluids.COAL_OIL_BUCKET);
            entries.add(COAL_OIL_BOTTLE);
            entries.add(ModFluids.ANDROSTADIENEDIONE_BUCKET);
            entries.add(ANDROSTADIENEDIONE_BOTTLE);
            entries.add(ModFluids.ANDROSTADIENEDIONE_OIL_SOLUTION_BUCKET);
            entries.add(ANDROSTADIENEDIONE_OIL_BOTTLE);
            entries.add(ModFluids.ESTRONE_OIL_SOLUTION_BUCKET);
            entries.add(CRUDE_ESTRONE);
            entries.add(PURE_ESTRONE);
            entries.add(KELP_ASH);
            entries.add(ModBlocks.KELP_ASH_BLOCK);
            entries.add(ModBlocks.KELP_ASH_MUD_BLOCK);
            entries.add(ModFluids.LYE_WATER_BUCKET);
            entries.add(LYE_WATER_BOTTLE);
            entries.add(ModFluids.CAUSTIC_SODA_SOLUTION_BUCKET);
            entries.add(ModBlocks.LIMESTONE_CHUNKS);
            entries.add(ModBlocks.QUICKLIME);
            entries.add(ModBlocks.SLAKED_LIME);
            entries.add(PURE_ESTRADIOL_CRYSTALS);
            entries.add(PURE_ESTRADIOL_POWDER);
            entries.add(HORSE_JUICE);
            entries.add(ModBlocks.FERMENTER_CONTROLLER);
            entries.add(ModBlocks.FERMENTER_PANEL);
            entries.add(ModBlocks.FERMENTER_TOP);
            entries.add(ModBlocks.FERMENTER_BOTTOM);
            entries.add(ModBlocks.FERMENTER_MIXER);
            entries.add(ModBlocks.FERMENTER_HEATER);
            entries.add(ModBlocks.FERMENTER_AIRLOCK);
            entries.add(ModFluids.SLUDGE_BUCKET);
            entries.add(NICKEL_PIPE);
            entries.add(ModBlocks.DISTILLER_BASE);
            entries.add(ModBlocks.DISTILLER_COLUMN);
            entries.add(ModBlocks.DISTILLER_TOP);
            //AUTOGENERATION LABEL DO NOT TOUCH


            //NOTE: put autogenerated fluids back in the right spots

            entries.add(CHILI_PEPPER);
            entries.add(DRIED_CHILI_PEPPER);
            entries.add(GROUND_CHILI_PEPPER);

            entries.add(EGEL_BOTTLE);

            entries.add(ACT_RIGHT_MUSIC_DISC);
    }).build());

    public static void registerModItems()
    {
        //
    }
}
