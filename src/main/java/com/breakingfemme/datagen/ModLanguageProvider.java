package com.breakingfemme.datagen;

import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.Registries;

import java.nio.file.Path;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.FIVE_G_TOWER_CONTROLLER, "5G Tower Controller");
        translationBuilder.add(ModBlocks.FIVE_G_SCAFFOLDING, "5G Tower Scaffolding");
        translationBuilder.add(ModBlocks.FIVE_G_TOWER_HEAD, "5G Tower Transmitter");
        translationBuilder.add(Registries.ITEM_GROUP.getKey(ModItems.ITEM_GROUP).orElseThrow(), "Breaking Femme");
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/breakingfemme/lang/en_us.existing.json").orElseThrow();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
//            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
