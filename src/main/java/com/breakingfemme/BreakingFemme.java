package com.breakingfemme;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;
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
		ModBlocks.registerModBlocks();
		ModFluids.registerModFluids();

		//Farmers sell copper sulfate at level 2 (Novice is level 1)
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 5),
				new ItemStack(ModItems.COPPER_SULFATE),
				1, 10, 0.05f
			));
		});

		//add (rare) nickel ingot drop to drowned, (8 times) more common when doing it by hand
		Identifier LOOT_TABLE_ID = EntityType.DROWNED.getLootTableId();
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && LOOT_TABLE_ID.equals(id)) {
				//manual kill (8% chance of getting nickel)
				LootPool.Builder poolBuilderManual = LootPool.builder()
					.conditionally(KilledByPlayerLootCondition.builder())
        	        .with(ItemEntry.builder(ModItems.NICKEL_INGOT))
					.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(1, 0.08f)));

				//automatic kill (1% chance of getting nickel)
				LootPool.Builder poolBuilderAuto = LootPool.builder()
					.conditionally(InvertedLootCondition.builder(KilledByPlayerLootCondition.builder()))
        	        .with(ItemEntry.builder(ModItems.NICKEL_INGOT))
					.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(1, 0.01f)));

				tableBuilder.pool(poolBuilderManual);
				tableBuilder.pool(poolBuilderAuto);
			}
		});
	}

	//utility function
	static final Block[] HOT_BLOCKS = {Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.LAVA, Blocks.MAGMA_BLOCK};

	public static boolean is_block_hot(World world, BlockPos pos)
	{
		Block target = world.getBlockState(pos).getBlock();
		for (Block block : HOT_BLOCKS) {
			if (block == target)
				return true;
		}
		return false;
	}
}
