package com.breakingfemme.block;

import com.breakingfemme.BreakingFemme;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SOY_CROP = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "soy"), new SoyCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));
    public static final Block FERMENTER_PANEL = registerBlock("fermenter_panel", new FermenterPanelBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_TRAPDOOR)));

    //register block and item
    private static final Block registerBlock(String name, Block block)
    {
        Registry.register(Registries.ITEM, new Identifier(BreakingFemme.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, name), block);
    }

    public static void registerModBlocks()
    {
        //
    }
}
