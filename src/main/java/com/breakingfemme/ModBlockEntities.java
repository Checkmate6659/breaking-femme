package com.breakingfemme;

import com.breakingfemme.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.breakingfemme.BreakingFemme.id;

public class ModBlockEntities {
    public static final BlockEntityType<FiveGTowerBlockEntity> FIVE_G_TOWER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, id("5gtower_be"),
            FabricBlockEntityTypeBuilder.create(FiveGTowerBlockEntity::new,ModBlocks.FIVE_G_TOWER_CONTROLLER).build()
    );

    public static final BlockEntityType<FermenterBlockEntity> FERMENTER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("fermenter_be"),
        FabricBlockEntityTypeBuilder.create(FermenterBlockEntity::new, ModBlocks.FERMENTER_CONTROLLER).build());

    public static final BlockEntityType<DistillerBlockEntity> DISTILLER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("distiller_be"),
        FabricBlockEntityTypeBuilder.create(DistillerBlockEntity::new, ModBlocks.DISTILLER_BASE).build());

    public static final BlockEntityType<DistillerTopBlockEntity> DISTILLER_TOP_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("distiller_top_be"),
        FabricBlockEntityTypeBuilder.create(DistillerTopBlockEntity::new, ModBlocks.DISTILLER_TOP).build());

    public static final BlockEntityType<FunnelBlockEntity> FUNNEL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("funnel_be"),
        FabricBlockEntityTypeBuilder.create(FunnelBlockEntity::new, ModBlocks.FUNNEL).build());

    public static void registerBlockEntities() {
        FluidStorage.SIDED.registerForBlockEntity(DistillerBlockEntity::getFluidStorage, DISTILLER_BLOCK_ENTITY);
        FluidStorage.SIDED.registerForBlockEntity(DistillerTopBlockEntity::getFluidStorage, DISTILLER_TOP_BLOCK_ENTITY);
    }
}
