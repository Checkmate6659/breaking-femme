package com.breakingfemme.block.entity;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.block.ModBlocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<FermenterBlockEntity> FERMENTER_BLOCK_ENTITY =
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(BreakingFemme.MOD_ID, "fermenter_be"),
        FabricBlockEntityTypeBuilder.create(FermenterBlockEntity::new, ModBlocks.FERMENTER_CONTROLLER).build());

    public static void registerBlockEntities() {
        //
    }
}
