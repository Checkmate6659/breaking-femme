package com.breakingfemme.item;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SOYBEANS = registerItem("soybeans", new AliasedBlockItem(ModBlocks.SOY_CROP,
        new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.25f).build())));

    public static final Item NICKEL_INGOT = registerItem("nickel_ingot", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_COPPER = registerItem("pulverized_copper", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_NICKEL = registerItem("pulverized_nickel", new Item(new FabricItemSettings()));
    public static final Item COPPER_SULFATE = registerItem("copper_sulfate", new Item(new FabricItemSettings()));
    public static final Item NICKEL_SULFATE = registerItem("nickel_sulfate", new Item(new FabricItemSettings()));

    public static final Item STEROLS = registerItem("sterols", new Item(new FabricItemSettings()));
    public static final Item CRUDE_ESTRONE = registerItem("crude_estrone", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRONE = registerItem("pure_estrone", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRADIOL_CRYSTALS = registerItem("pure_estradiol_crystals", new Item(new FabricItemSettings()));
    public static final Item PURE_ESTRADIOL_POWDER = registerItem("pure_estradiol_powder", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, name), item);
    }

    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
        new Identifier(BreakingFemme.MOD_ID, "breakingfemme"),
        FabricItemGroup.builder().displayName(Text.translatable("itemgroup.breakingfemme"))
        .icon(() -> new ItemStack(ModItems.PURE_ESTRADIOL_CRYSTALS)).entries((displayContext, entries) -> {
            entries.add(SOYBEANS);
            entries.add(STEROLS);
            entries.add(NICKEL_INGOT);
            entries.add(PULVERIZED_COPPER);
            entries.add(PULVERIZED_NICKEL);
            entries.add(COPPER_SULFATE);
            entries.add(NICKEL_SULFATE);
            entries.add(ModFluids.COPPER_SULFATE_BUCKET);
            entries.add(ModFluids.NICKEL_SULFATE_BUCKET);
            entries.add(CRUDE_ESTRONE);
            entries.add(PURE_ESTRONE);
            entries.add(PURE_ESTRADIOL_CRYSTALS);
            entries.add(PURE_ESTRADIOL_POWDER);
        }).build());

    public static void registerModItems()
    {
        //
    }
}
