package com.breakingfemme;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.block.entity.ModBlockEntities;
import com.breakingfemme.fluid.ModFluids;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.networking.ModNetworking;
import com.breakingfemme.recipe.ModRecipes;
import com.breakingfemme.screen.ModScreenHandlers;

public class BreakingFemme implements ModInitializer {
	public static final String MOD_ID = "breakingfemme";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//register nickel ore generation
	public static final RegistryKey<PlacedFeature> NICKEL_ORE_PLACED_FEATURE = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(MOD_ID, "ore_nickel"));

	//register damage types
	public static final RegistryKey<DamageType> NOVIKOV = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(BreakingFemme.MOD_ID, "novikov"));
	public static final RegistryKey<DamageType> DISTRACTION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(BreakingFemme.MOD_ID, "distraction"));
	public static final RegistryKey<DamageType> HEADACHE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(BreakingFemme.MOD_ID, "headache"));
	public static final RegistryKey<DamageType> SODIUM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(BreakingFemme.MOD_ID, "sodium"));

	//register particles
	// This DefaultParticleType gets called when you want to use your particle in code.
	public static final DefaultParticleType COLON_THREE_PARTICLE = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Jesse I'm trans! We gotta cook estrogen!");

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModFluids.registerModFluids();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModRecipes.registerRecipes();
		ModSounds.registerSounds();
		KineticsAttachments.registerAttachments();
		VillagerAttachments.registerAttachments();
		ModNetworking.registerC2SPackets();
		ModNetworking.registerS2CPackets();
		
		//add flexibility enchantment
		Registry.register(Registries.ENCHANTMENT, new Identifier(BreakingFemme.MOD_ID, "flexibility"), new FlexibilityEnchantment(Rarity.UNCOMMON, EquipmentSlot.CHEST));

		//Register kinetics command (shows levels of different chemicals in the player)
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> KineticsCommand.register(dispatcher));

		//Register :3 particle
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "colon_three_particle"), COLON_THREE_PARTICLE);

		//Farmers sell soybeans at level 3 (yes... they won't sell it to people they won't trust! it's their only known source of HRT)
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 3, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 10), //10 emeralds for a singular bean... and you only get 3 of them.
				new ItemStack(ModItems.SOYBEANS),
				3, 3, 0.05f
			));
		});
		//and copper sulfate at level 2
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 5),
				new ItemStack(ModItems.COPPER_SULFATE),
				1, 10, 0.05f
			));
		});

		//Masons will sell calcite at level 3 (same as when selling polished rocks)
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.MASON, 3, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 1),
				new ItemStack(Blocks.CALCITE, 4),
				10, 10, 0.05f
			));
		});
		//and will slake your lime at level 5 (master)
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.MASON, 5, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(ModBlocks.LIMESTONE_CHUNKS, 6),
				new ItemStack(Items.EMERALD, 1),
				new ItemStack(ModBlocks.SLAKED_LIME, 6),
				10, 10, 0.05f
			));
		});
		
		//Wandering traders will rip you off for some chili pepper: 4 emeralds for just one!!
		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 4),
				new ItemStack(ModItems.CHILI_PEPPER, 1),
				10, 1, 0.0f
			));
		});

		//generate nickel in the overworld
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, NICKEL_ORE_PLACED_FEATURE);

		//add (rare) nickel ingot drop to drowned, (8 times) more common when doing it by hand
		Identifier LOOT_TABLE_ID = EntityType.DROWNED.getLootTableId();
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && LOOT_TABLE_ID.equals(id)) {
				//manual kill (8% chance of getting nickel)
				LootPool.Builder poolBuilderManual = LootPool.builder()
					.conditionally(KilledByPlayerLootCondition.builder())
        	        .with(ItemEntry.builder(ModItems.NICKEL_INGOT))
					.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(1, 0.125f)))
					.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.LOOTING));

				//automatic kill (1% chance of getting nickel)
				LootPool.Builder poolBuilderAuto = LootPool.builder()
					.conditionally(InvertedLootCondition.builder(KilledByPlayerLootCondition.builder()))
        	        .with(ItemEntry.builder(ModItems.NICKEL_INGOT))
					.apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(1, 0.015625f)))
					.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.LOOTING)); //compat with create for instance

				tableBuilder.pool(poolBuilderManual);
				tableBuilder.pool(poolBuilderAuto);
			}
		});

        //Fuels
        FuelRegistry.INSTANCE.add(ModFluids.ET95_BUCKET, 5400); //1 bucket of 95% ethanol can cook 27 items
        FuelRegistry.INSTANCE.add(ModFluids.ET64_BUCKET, 3000); //can cook 15, not quite 18, because more water
        FuelRegistry.INSTANCE.add(ModItems.ET95_BOTTLE, 1800);
        FuelRegistry.INSTANCE.add(ModItems.ET64_BOTTLE, 1000); //5 items
        FuelRegistry.INSTANCE.add(ModFluids.TAR_BUCKET, 6400); //1 bucket of tar can cook 32 items
        FuelRegistry.INSTANCE.add(ModItems.TAR, 800); //and a ball of tar is 4 items (worse than coal, so dont use tar for fuel)
        FuelRegistry.INSTANCE.add(ModFluids.COAL_OIL_BUCKET, 12800); //you can use coal oil tho. its not gasoline but it does cook a stack of items.

		//TODO: add create mod blaze burner fuels!! (with regular create its just items, not fluids tho)
		//sample code: https://github.com/mrh0/createaddition/blob/1.20.1/src/main/java/com/mrh0/createaddition/CreateAddition.java#L128
		//it would be funny to give chili peppers to blaze burners too
	}


	//======================== UTILITY FUNCTIONS ========================
	public static NbtCompound nbtOfFluid(FluidVariant fluid)
	{
		return fluid.toNbt();
	}

	public static FluidVariant fluidFromNbt(NbtCompound nbt)
	{
		return FluidVariant.fromNbt(nbt);
	}

	public static void spillFluid(World world, BlockPos pos, FlowableFluid fluid, int level)
	{
		//first, set the center block state
		if(world.isAir(pos))
			world.setBlockState(pos, fluid.getFlowing().getDefaultState().with(FlowableFluid.LEVEL, level).getBlockState());

		for(int i = 0; i < 4; i++) //add level 1 to neighbors if there isnt already sth there
		{
			BlockPos target = pos.offset(Direction.fromHorizontal(i));
			if(world.getRandom().nextInt(4) != 0 && world.isAir(target)) //theres some randomness added... why? idk.
				world.setBlockState(target, fluid.getFlowing().getDefaultState().with(FlowableFluid.LEVEL, world.getRandom().nextBoolean() ? level : 8).getBlockState());
		}
	}
}
