package com.breakingfemme.datagen;

import java.util.function.Consumer;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModFluids;
import com.breakingfemme.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        //Branches are registered here from bottom to top... if not already existing when running datagen... wait what? i dont understand the system
        Advancement root = Advancement.Builder.createUntelemetered()
            .display(
                    ModItems.MORTAR_PESTLE, // The display icon
                    Text.translatable("advancement.breakingfemme.root.title"), // The title
                    Text.translatable("advancement.breakingfemme.root.description"), // The description
                    new Identifier(BreakingFemme.MOD_ID, "textures/gui/advancements.png"), // Background image used (for the advancements tab)
                    AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                    false, // Show toast top right
                    false, // Announce to chat
                    false // Hidden in the advancement tab
            )
            // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
            .criterion("made_mortar_pestle", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_PESTLE))
            .build(consumer, BreakingFemme.MOD_ID + "/root");
        
        //nickel sulfate branch
        Advancement pulverized_nickel = Advancement.Builder.createUntelemetered().parent(root)
            .display(
                    ModItems.PULVERIZED_NICKEL,
                    Text.translatable("advancement.breakingfemme.pulverized_nickel.title"),
                    Text.translatable("advancement.breakingfemme.pulverized_nickel.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_pulverized_nickel", InventoryChangedCriterion.Conditions.items(ModItems.PULVERIZED_NICKEL))
            .build(consumer, BreakingFemme.MOD_ID + "/pulverized_nickel");

        Advancement rubies = Advancement.Builder.createUntelemetered().parent(pulverized_nickel)
            .display(
                    ModItems.RUBY,
                    Text.translatable("advancement.breakingfemme.ruby.title"),
                    Text.translatable("advancement.breakingfemme.ruby.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_ruby", InventoryChangedCriterion.Conditions.items(ModItems.RUBY))
            .build(consumer, BreakingFemme.MOD_ID + "/ruby");

        Advancement aluminum = Advancement.Builder.createUntelemetered().parent(rubies)
            .display(
                    ModItems.ALUMINUM_SCRAP,
                    Text.translatable("advancement.breakingfemme.aluminum.title"),
                    Text.translatable("advancement.breakingfemme.aluminum.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_aluminum", InventoryChangedCriterion.Conditions.items(ModItems.ALUMINUM_SCRAP))
            .build(consumer, BreakingFemme.MOD_ID + "/aluminum");

        Advancement raney = Advancement.Builder.createUntelemetered().parent(aluminum)
            .display(
                    ModItems.RANEY_NICKEL,
                    Text.translatable("advancement.breakingfemme.raney_nickel.title"),
                    Text.translatable("advancement.breakingfemme.raney_nickel.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_raney_nickel", InventoryChangedCriterion.Conditions.items(ModItems.RANEY_NICKEL))
            .build(consumer, BreakingFemme.MOD_ID + "/raney_nickel");

        Advancement copper_sulfate = Advancement.Builder.createUntelemetered().parent(pulverized_nickel)
            .display(
                    ModItems.COPPER_SULFATE,
                    Text.translatable("advancement.breakingfemme.copper_sulfate.title"),
                    Text.translatable("advancement.breakingfemme.copper_sulfate.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("bought_copper_sulfate", InventoryChangedCriterion.Conditions.items(ModItems.COPPER_SULFATE))
            .build(consumer, BreakingFemme.MOD_ID + "/copper_sulfate");

        Advancement nickel_sulfate = Advancement.Builder.createUntelemetered().parent(copper_sulfate)
            .display(
                    ModItems.NICKEL_SULFATE,
                    Text.translatable("advancement.breakingfemme.nickel_sulfate.title"),
                    Text.translatable("advancement.breakingfemme.nickel_sulfate.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .criterion("made_nickel_sulfate", InventoryChangedCriterion.Conditions.items(ModItems.NICKEL_SULFATE))
            .build(consumer, BreakingFemme.MOD_ID + "/nickel_sulfate");

        //soybean (main) branch
        Advancement soybeans = Advancement.Builder.createUntelemetered().parent(root)
            .display(
                    ModItems.SOYBEANS,
                    Text.translatable("advancement.breakingfemme.soybeans.title"),
                    Text.translatable("advancement.breakingfemme.soybeans.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("bought_soybeans", InventoryChangedCriterion.Conditions.items(ModItems.SOYBEANS))
            .build(consumer, BreakingFemme.MOD_ID + "/soybeans");

        Advancement sterols = Advancement.Builder.createUntelemetered().parent(soybeans)
            .display(
                    ModItems.STEROLS,
                    Text.translatable("advancement.breakingfemme.sterols.title"),
                    Text.translatable("advancement.breakingfemme.sterols.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(1000))
            .criterion("made_sterols", InventoryChangedCriterion.Conditions.items(ModItems.STEROLS))
            .build(consumer, BreakingFemme.MOD_ID + "/sterols");

        Advancement androstadienedione = Advancement.Builder.createUntelemetered().parent(sterols)
            .display(
                    ModFluids.ANDROSTADIENEDIONE_BUCKET,
                    Text.translatable("advancement.breakingfemme.androstadienedione.title"),
                    Text.translatable("advancement.breakingfemme.androstadienedione.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(6000))
            .criterion("made_androstadienedione", InventoryChangedCriterion.Conditions.items(ModFluids.ANDROSTADIENEDIONE_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/androstadienedione");

        Advancement crude_estrone = Advancement.Builder.createUntelemetered().parent(androstadienedione)
            .display(
                    ModItems.CRUDE_ESTRONE,
                    Text.translatable("advancement.breakingfemme.crude_estrone.title"),
                    Text.translatable("advancement.breakingfemme.crude_estrone.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(12000))
            .criterion("made_crude_estrone", InventoryChangedCriterion.Conditions.items(ModItems.CRUDE_ESTRONE))
            .build(consumer, BreakingFemme.MOD_ID + "/crude_estrone");

        Advancement pure_estrone = Advancement.Builder.createUntelemetered().parent(crude_estrone)
            .display(
                    ModItems.PURE_ESTRONE,
                    Text.translatable("advancement.breakingfemme.pure_estrone.title"),
                    Text.translatable("advancement.breakingfemme.pure_estrone.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(3000))
            .criterion("made_pure_estrone", InventoryChangedCriterion.Conditions.items(ModItems.PURE_ESTRONE))
            .build(consumer, BreakingFemme.MOD_ID + "/pure_estrone");

        Advancement pure_estradiol = Advancement.Builder.createUntelemetered().parent(pure_estrone)
            .display(
                    ModItems.PURE_ESTRADIOL_CRYSTALS,
                    Text.translatable("advancement.breakingfemme.pure_estradiol.title"),
                    Text.translatable("advancement.breakingfemme.pure_estradiol.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.CHALLENGE,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(36000))
            .criterion("made_pure_estradiol", InventoryChangedCriterion.Conditions.items(ModItems.PURE_ESTRADIOL_CRYSTALS))
            .build(consumer, BreakingFemme.MOD_ID + "/pure_estradiol");

        //fermenting and distilling branch
        Advancement fermenter = Advancement.Builder.createUntelemetered().parent(root)
            .display(
                    Blocks.BARREL,
                    Text.translatable("advancement.breakingfemme.fermenter.title"),
                    Text.translatable("advancement.breakingfemme.fermenter.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(1000))
            .criterion("made_fermenter", InventoryChangedCriterion.Conditions.items(ModBlocks.FERMENTER_CONTROLLER))
            .build(consumer, BreakingFemme.MOD_ID + "/fermenter");
        
        Advancement yeast = Advancement.Builder.createUntelemetered().parent(fermenter)
            .display(
                    ModItems.YEAST,
                    Text.translatable("advancement.breakingfemme.yeast.title"),
                    Text.translatable("advancement.breakingfemme.yeast.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(1000))
            .criterion("got_yeast", InventoryChangedCriterion.Conditions.items(ModItems.YEAST))
            .build(consumer, BreakingFemme.MOD_ID + "/yeast");
        
        Advancement beer = Advancement.Builder.createUntelemetered().parent(yeast)
            .display(
                    ModItems.BEER_BOTTLE,
                    Text.translatable("advancement.breakingfemme.beer.title"),
                    Text.translatable("advancement.breakingfemme.beer.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(3000))
            .criterion("made_beer", InventoryChangedCriterion.Conditions.items(ModFluids.BEER_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/beer");
        
        Advancement ethanol = Advancement.Builder.createUntelemetered().parent(beer)
            .display(
                    ModFluids.ET95_BUCKET,
                    Text.translatable("advancement.breakingfemme.et95.title"),
                    Text.translatable("advancement.breakingfemme.et95.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(6000))
            .criterion("made_et95", InventoryChangedCriterion.Conditions.items(ModFluids.ET95_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/et95");
        
        Advancement nether_beer = Advancement.Builder.createUntelemetered().parent(ethanol)
            .display(
                    ModItems.NETHER_BEER_BOTTLE,
                    Text.translatable("advancement.breakingfemme.nether_beer.title"),
                    Text.translatable("advancement.breakingfemme.nether_beer.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(3000))
            .criterion("made_nether_beer", InventoryChangedCriterion.Conditions.items(ModFluids.NETHER_BEER_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/nether_beer");
        
        Advancement coal_tar = Advancement.Builder.createUntelemetered().parent(ethanol)
            .display(
                    ModFluids.TAR_BUCKET,
                    Text.translatable("advancement.breakingfemme.tar.title"),
                    Text.translatable("advancement.breakingfemme.tar.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .criterion("made_tar", InventoryChangedCriterion.Conditions.items(ModItems.TAR))
            .build(consumer, BreakingFemme.MOD_ID + "/tar");
        
        Advancement coal_oil = Advancement.Builder.createUntelemetered().parent(coal_tar)
            .display(
                    ModFluids.COAL_OIL_BUCKET,
                    Text.translatable("advancement.breakingfemme.oil.title"),
                    Text.translatable("advancement.breakingfemme.oil.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.GOAL,
                    true,
                    true,
                    false
            )
            .criterion("made_oil", InventoryChangedCriterion.Conditions.items(ModFluids.COAL_OIL_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/oil");
        
        //milk processing branch
        Advancement milk = Advancement.Builder.createUntelemetered().parent(root)
            .display(
                    Items.MILK_BUCKET,
                    Text.translatable("advancement.breakingfemme.milk.title"),
                    Text.translatable("advancement.breakingfemme.milk.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("milked_cow", InventoryChangedCriterion.Conditions.items(Items.MILK_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/milk");

        Advancement ingot_mold = Advancement.Builder.createUntelemetered().parent(milk)
            .display(
                    ModItems.INGOT_MOLD,
                    Text.translatable("advancement.breakingfemme.ingot_mold.title"),
                    Text.translatable("advancement.breakingfemme.ingot_mold.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("made_ingot_mold", InventoryChangedCriterion.Conditions.items(ModItems.INGOT_MOLD))
            .build(consumer, BreakingFemme.MOD_ID + "/ingot_mold");

        Advancement separate = Advancement.Builder.createUntelemetered().parent(ingot_mold)
            .display(
                    ModBlocks.MILK_SEPARATOR,
                    Text.translatable("advancement.breakingfemme.milk_separator.title"),
                    Text.translatable("advancement.breakingfemme.milk_separator.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("made_milk_separator", InventoryChangedCriterion.Conditions.items(ModBlocks.MILK_SEPARATOR))
            .build(consumer, BreakingFemme.MOD_ID + "/milk_separator");

        Advancement cream = Advancement.Builder.createUntelemetered().parent(separate)
            .display(
                    ModItems.CREAMGOT,
                    Text.translatable("advancement.breakingfemme.creamgot.title"),
                    Text.translatable("advancement.breakingfemme.creamgot.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(3000))
            .criterion("made_creamgot", InventoryChangedCriterion.Conditions.items(ModItems.CREAMGOT))
            .build(consumer, BreakingFemme.MOD_ID + "/creamgot");

        Advancement skimmed_milk = Advancement.Builder.createUntelemetered().parent(separate)
            .display(
                    ModItems.SKIMMED_MILK_BUCKET,
                    Text.translatable("advancement.breakingfemme.skimmed_milk.title"),
                    Text.translatable("advancement.breakingfemme.skimmed_milk.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .rewards(AdvancementRewards.Builder.experience(3000))
            .criterion("made_skimmed_milk", InventoryChangedCriterion.Conditions.items(ModItems.SKIMMED_MILK_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/skimmed_milk");

        Advancement milkgot = Advancement.Builder.createUntelemetered().parent(skimmed_milk)
            .display(
                    ModItems.MILKGOT,
                    Text.translatable("advancement.breakingfemme.milkgot.title"),
                    Text.translatable("advancement.breakingfemme.milkgot.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("made_milkgot", InventoryChangedCriterion.Conditions.items(ModItems.MILKGOT))
            .build(consumer, BreakingFemme.MOD_ID + "/milkgot");
        
        //lime and lye branch
        Advancement limestone_chunks = Advancement.Builder.createUntelemetered().parent(root)
            .display(
                    ModBlocks.LIMESTONE_CHUNKS,
                    Text.translatable("advancement.breakingfemme.limestone_chunks.title"),
                    Text.translatable("advancement.breakingfemme.limestone_chunks.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_limestone_chunks", InventoryChangedCriterion.Conditions.items(ModBlocks.LIMESTONE_CHUNKS))
            .build(consumer, BreakingFemme.MOD_ID + "/limestone_chunks");
        
        Advancement slaked_lime = Advancement.Builder.createUntelemetered().parent(limestone_chunks)
            .display(
                    ModBlocks.SLAKED_LIME,
                    Text.translatable("advancement.breakingfemme.slaked_lime.title"),
                    Text.translatable("advancement.breakingfemme.slaked_lime.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_slaked_lime", InventoryChangedCriterion.Conditions.items(ModBlocks.SLAKED_LIME))
            .build(consumer, BreakingFemme.MOD_ID + "/slaked_lime");
        
        Advancement lye_water = Advancement.Builder.createUntelemetered().parent(slaked_lime)
            .display(
                    ModFluids.LYE_WATER_BUCKET,
                    Text.translatable("advancement.breakingfemme.lye_water.title"),
                    Text.translatable("advancement.breakingfemme.lye_water.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_lye_water", InventoryChangedCriterion.Conditions.items(ModFluids.LYE_WATER_BUCKET))
            .build(consumer, BreakingFemme.MOD_ID + "/lye_water");
        
        Advancement concentrated_caustic_soda = Advancement.Builder.createUntelemetered().parent(lye_water)
            .display(
                    ModFluids.CONCENTRATED_CAUSTIC_SODA_BUCKET,
                    Text.translatable("advancement.breakingfemme.concentrated_caustic_soda.title"),
                    Text.translatable("advancement.breakingfemme.concentrated_caustic_soda.description"),
                    null, // children to parent advancements don't need a background set
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
            )
            .criterion("got_concentrated_caustic_soda", InventoryChangedCriterion.Conditions.items(ModFluids.CONCENTRATED_CAUSTIC_SODA_BUCKET))
        .build(consumer, BreakingFemme.MOD_ID + "/concentrated_caustic_soda");
    }
}
