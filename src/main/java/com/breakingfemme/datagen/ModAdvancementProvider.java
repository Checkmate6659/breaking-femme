package com.breakingfemme.datagen;

import java.util.function.Consumer;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.item.ModItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
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
                    Text.literal("Breaking Femme"), // The title
                    Text.literal("Jesse I'm trans! We gotta cook estrogen!"), // The description
                    new Identifier(BreakingFemme.MOD_ID, "textures/gui/advancements.png"), // Background image used (for the advancements tab)
                    AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
                    true, // Show toast top right
                    false, // Announce to chat
                    false // Hidden in the advancement tab
            )
            // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
            .criterion("got_mortar_pestle", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_PESTLE))
            .build(consumer, BreakingFemme.MOD_ID + "/root");
    }
}
