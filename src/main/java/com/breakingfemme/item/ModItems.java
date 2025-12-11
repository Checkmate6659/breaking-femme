package com.breakingfemme.item;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.fluid.ModFluids;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SOYBEANS = registerItem("soybeans", new AliasedBlockItem(ModBlocks.SOY_CROP, new FabricItemSettings()));
    public static final Item NICKEL_INGOT = registerItem("nickel_ingot", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_COPPER = registerItem("pulverized_copper", new Item(new FabricItemSettings()));
    public static final Item PULVERIZED_NICKEL = registerItem("pulverized_nickel", new Item(new FabricItemSettings()));
    public static final Item COPPER_SULFATE = registerItem("copper_sulfate", new Item(new FabricItemSettings()));
    public static final Item NICKEL_SULFATE = registerItem("nickel_sulfate", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, name), item);
    }

    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
        new Identifier(BreakingFemme.MOD_ID, "breakingfemme"),
        FabricItemGroup.builder().displayName(Text.translatable("itemgroup.breakingfemme"))
        .icon(() -> new ItemStack(ModItems.COPPER_SULFATE)).entries((displayContext, entries) -> {
            entries.add(SOYBEANS);
            entries.add(NICKEL_INGOT);
            entries.add(PULVERIZED_COPPER);
            entries.add(PULVERIZED_NICKEL);
            entries.add(COPPER_SULFATE);
            entries.add(NICKEL_SULFATE);
            entries.add(ModFluids.COPPER_SULFATE_BUCKET);
            entries.add(ModFluids.NICKEL_SULFATE_BUCKET);
        }).build());

    public static void registerModItems()
    {
        //
    }
}
