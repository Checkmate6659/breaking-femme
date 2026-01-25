package com.breakingfemme.block;

import com.breakingfemme.BreakingFemme;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block NICKEL_ORE = registerBlock("nickel_ore", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_NICKEL_ORE = registerBlock("deepslate_nickel_ore", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block NICKEL_BLOCK = registerBlock("nickel_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block SOY_CROP = Registry.register(Registries.BLOCK, new Identifier(BreakingFemme.MOD_ID, "soy"), new SoyCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));
    public static final Block MILK_SEPARATOR = registerBlock("milk_separator", new MilkSeparatorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).pistonBehavior(PistonBehavior.BLOCK).ticksRandomly()));
    public static final Block FERMENTER_CONTROLLER = registerBlock("fermenter_controller", new FermenterControllerBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS))); //block entity already cannot be pushed
    public static final Block FERMENTER_PANEL = registerBlock("fermenter_panel", new FermenterPanelBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_TOP = registerBlock("fermenter_top", new FermenterTopBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_BOTTOM = registerBlock("fermenter_bottom", new FermenterBottomBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_MIXER = registerBlock("fermenter_mixer", new FermenterMixerBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_HEATER = registerBlock("fermenter_heater", new FermenterHeaterBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));

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
