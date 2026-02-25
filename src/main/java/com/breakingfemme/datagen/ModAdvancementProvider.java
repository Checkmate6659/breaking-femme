package com.breakingfemme.datagen;

import java.util.function.Consumer;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;

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
        Advancement root = Advancement.Builder.create()
            .display(
                    ModItems.MORTAR_PESTLE, // The display icon
                    Text.translatable("advancement.breakingfemme.root.title"), // The title
                    Text.translatable("advancement.breakingfemme.root.description"), // The description
                    new Identifier(BreakingFemme.MOD_ID, "textures/gui/advancements.png"), // Background image used (for the advancements tab)
                    AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                    true, // Show toast top right
                    false, // Announce to chat
                    false // Hidden in the advancement tab
            )
            // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
            .criterion("made_mortar_pestle", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_PESTLE))
            .build(consumer, BreakingFemme.MOD_ID + "/root");
        
        //milk processing branch
        Advancement milk = Advancement.Builder.create().parent(root)
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

        Advancement ingot_mold = Advancement.Builder.create().parent(milk)
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

        Advancement separate = Advancement.Builder.create().parent(ingot_mold)
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

        Advancement cream = Advancement.Builder.create().parent(separate)
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

        Advancement skimmed_milk = Advancement.Builder.create().parent(separate)
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

        Advancement milkgot = Advancement.Builder.create().parent(skimmed_milk)
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

        //fermenting and distilling branch
        Advancement fermenter = Advancement.Builder.create().parent(root)
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
        
        Advancement yeast = Advancement.Builder.create().parent(fermenter)
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
        
        Advancement beer = Advancement.Builder.create().parent(yeast)
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
        
        Advancement ethanol = Advancement.Builder.create().parent(beer)
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
        
        Advancement nether_beer = Advancement.Builder.create().parent(beer)
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
        
        //soybean (main) branch
        Advancement soybeans = Advancement.Builder.create().parent(root)
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
        
        //nickel sulfate branch
        Advancement copper_sulfate = Advancement.Builder.create().parent(root)
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
    }
}
