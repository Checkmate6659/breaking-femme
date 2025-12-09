package com.breakingfemme;

import com.breakingfemme.datagen.ModBlockTagProvider;
import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.datagen.ModLootTableProvider;
import com.breakingfemme.datagen.ModModelProvider;
import com.breakingfemme.datagen.ModRecipeProvider;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

//https://www.youtube.com/watch?v=w8ZAJWNy8Fk
public class BreakingFemmeDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
