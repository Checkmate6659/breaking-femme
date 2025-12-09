package com.breakingfemme;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.breakingfemme.item.ModItems;

public class BreakingFemme implements ModInitializer {
	public static final String MOD_ID = "breakingfemme";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Jesse I'm trans! We gotta cook estrogen!");

		ModItems.registerModItems();
		
		//TODO: add custom trade: farmer sells copper sulfate
		//https://www.youtube.com/watch?v=rumR-BejsEo
	}
}
