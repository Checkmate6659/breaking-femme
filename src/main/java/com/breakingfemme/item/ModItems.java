package com.breakingfemme.item;

import com.breakingfemme.BreakingFemme;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item COPPER_SULFATE = registerItem("copper_sulfate", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, name), item);
    }

    public static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
        new Identifier(BreakingFemme.MOD_ID, "breakingfemme"),
        FabricItemGroup.builder().displayName(Text.translatable("itemgroup.breakingfemme"))
        .icon(() -> new ItemStack(ModItems.COPPER_SULFATE)).entries((displayContext, entries) -> {
            entries.add(COPPER_SULFATE);
        }).build());

    public static void registerModItems()
    {
        //
    }
}
