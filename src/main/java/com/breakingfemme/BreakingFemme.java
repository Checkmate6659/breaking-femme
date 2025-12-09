package com.breakingfemme;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

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
		
		//Farmers sell copper sulfate at level 2 (Novice is level 1)
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 5),
				new ItemStack(ModItems.COPPER_SULFATE),
				1, 10, 0.05f
			));
		});
	}
}
