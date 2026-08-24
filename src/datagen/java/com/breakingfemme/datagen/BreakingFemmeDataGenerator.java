package com.breakingfemme.datagen;

import com.breakingfemme.datagen.providers.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

import static com.breakingfemme.BreakingFemme.MOD_ID;

//https://www.youtube.com/watch?v=w8ZAJWNy8Fk

public class BreakingFemmeDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModLanguageProvider::new);
		pack.addProvider(ModEntityTagProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModFluidTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModAdvancementProvider::new);
	}

	@Override
	public @Nullable String getEffectiveModId() {
		return MOD_ID;
	}
}
