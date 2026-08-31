package com.breakingfemme;

import com.breakingfemme.block.*;
import com.breakingfemme.block.press.PressBottomBlock;
import com.breakingfemme.block.press.PressHandleBlock;
import com.breakingfemme.block.press.PressTopBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.SandBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.breakingfemme.BreakingFemme.id;

public class ModBlocks {
    public static final Block NICKEL_ORE = registerBlock("nickel_ore", new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));
    public static final Block DEEPSLATE_NICKEL_ORE = registerBlock("deepslate_nickel_ore", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE)));
    public static final Block NICKEL_BLOCK = registerBlock("nickel_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block RUBY_ORE = registerBlock("ruby_ore", new Block(FabricBlockSettings.copyOf(Blocks.EMERALD_ORE)));
    public static final Block DEEPSLATE_RUBY_ORE = registerBlock("deepslate_ruby_ore", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_EMERALD_ORE)));
    public static final Block RUBY_BLOCK = registerBlock("ruby_block", new Block(FabricBlockSettings.copyOf(Blocks.EMERALD_BLOCK)));
    public static final Block PARTIALLY_REDUCED_RUBY_BLOCK = registerBlock("partially_reduced_ruby_block", new Block(FabricBlockSettings.copyOf(Blocks.EMERALD_BLOCK)));
    public static final Block ROCK_SALT_BLOCK = registerBlock("rock_salt_block", new Block(FabricBlockSettings.copyOf(Blocks.CALCITE)));
    public static final Block SOY_CROP = Registry.register(Registries.BLOCK, id("soy"), new SoyCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));
    public static final Block CHILI_CROP = Registry.register(Registries.BLOCK, id("chili_pepper"), new ChiliCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));
    public static final Block MILK_SEPARATOR = registerBlock("milk_separator", new MilkSeparatorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).pistonBehavior(PistonBehavior.BLOCK).ticksRandomly()));
    public static final Block KELP_ASH_BLOCK = registerBlock("kelp_ash_block", new KelpAshBlock(FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.DARK_GREEN)));
    public static final Block LIMESTONE_CHUNKS = registerBlock("limestone_chunks", new SandBlock(MapColor.PALE_YELLOW.color, FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.PALE_YELLOW)));
    public static final Block SLAKED_LIME = registerBlock("slaked_lime", new Block(FabricBlockSettings.copyOf(Blocks.MUD).mapColor(MapColor.PALE_YELLOW)));
    public static final Block QUICKLIME = registerBlock("quicklime", new QuicklimeBlock(FabricBlockSettings.copyOf(Blocks.SAND).mapColor(MapColor.PALE_YELLOW))); //needs to be registered after slaked lime, since it references ModBlocks.SLAKED_LIME in the QuicklimeBlock class
    public static final Block FIRE_BRICKS = registerBlockFireproof("fire_bricks", new Block(FabricBlockSettings.copyOf(Blocks.BRICKS)));

    //fermenter blocks
    public static final Block FERMENTER_CONTROLLER = registerBlock("fermenter_controller", new FermenterControllerBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS))); //block entity already cannot be pushed
    public static final Block FERMENTER_PANEL = registerBlock("fermenter_panel", new FermenterPanelBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_TOP = registerBlock("fermenter_top", new FermenterTopBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_BOTTOM = registerBlock("fermenter_bottom", new FermenterBottomBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_MIXER = registerBlock("fermenter_mixer", new FermenterMixerBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_HEATER = registerBlock("fermenter_heater", new FermenterHeaterBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS).pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block FERMENTER_AIRLOCK = registerBlock("fermenter_airlock", new FermenterAirlockBlock(FabricBlockSettings.copyOf(Blocks.GLASS).noCollision().pistonBehavior(PistonBehavior.DESTROY)));

    //distiller blocks
    public static final Block DISTILLER_BASE = registerBlock("distiller_base", new DistillerBaseBlock(FabricBlockSettings.copyOf(Blocks.IRON_TRAPDOOR))); //block entity already cannot be pushed
    public static final Block DISTILLER_TOP = registerBlock("distiller_top", new DistillerTopBlock(FabricBlockSettings.copyOf(Blocks.IRON_TRAPDOOR)));
    public static final Block DISTILLER_COLUMN = registerBlock("distiller_column", new DistillerColumnBlock(FabricBlockSettings.copyOf(Blocks.IRON_TRAPDOOR).pistonBehavior(PistonBehavior.BLOCK)));

    //funnel block
    public static final Block FUNNEL = registerBlock("funnel", new FunnelBlock(FabricBlockSettings.copyOf(Blocks.HOPPER)));

    //5g tower block
    public static final Block FIVE_G_TOWER_CONTROLLER = registerBlock("5gtower_controller", new FiveGTowerControllerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block FIVE_G_SCAFFOLDING = registerBlock("5gtower_scaffolding", new FiveGTowerScaffolding(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block FIVE_G_TOWER_HEAD = registerBlock("5gtower_head", new FiveGTowerHead(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    // press blocks
    public static final Block PRESS_BOTTOM = registerBlock("press_bottom", new PressBottomBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block PRESS_TOP = registerBlock("press_top", new PressTopBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block PRESS_HANDLE = registerBlock("press_handle", new PressHandleBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    //register block and item
    private static final Block registerBlock(String name, Block block)
    {
        Registry.register(Registries.ITEM, id(name), new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    //register block and item but the item is fireproof. repeat code but kinda dont want param bloat
    private static final Block registerBlockFireproof(String name, Block block)
    {
        Registry.register(Registries.ITEM, id(name), new BlockItem(block, new FabricItemSettings().fireproof()));
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    public static void registerModBlocks()
    {
        //
    }
}
