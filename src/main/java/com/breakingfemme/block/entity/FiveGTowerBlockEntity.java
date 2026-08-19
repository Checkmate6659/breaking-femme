package com.breakingfemme.block.entity;

import com.breakingfemme.EntityAttachments;
import com.breakingfemme.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.ServerWorldAccess;

public class FiveGTowerBlockEntity extends BlockEntity {
    BlockPos topPosition;

    public FiveGTowerBlockEntity( BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIVE_G_TOWER_BLOCK_ENTITY_BLOCK_ENTITY, pos, state);
        topPosition = pos.up(10);
    }

    public void tick(ServerWorldAccess server) {
        var  nearEntities = server.getOtherEntities(null, Box.of(topPosition.toCenterPos(),20,20,20), new EntityAttachments.IsEstrogennablePredicate());
        for (Entity nearEntity : nearEntities) {
            EntityAttachments.giveEstrogenFor(nearEntity,5);
        }
    }

}
