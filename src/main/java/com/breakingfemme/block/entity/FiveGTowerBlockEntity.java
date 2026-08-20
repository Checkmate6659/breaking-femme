package com.breakingfemme.block.entity;

import com.breakingfemme.EntityAttachments;
import com.breakingfemme.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;

public class FiveGTowerBlockEntity extends BlockEntity {
    BlockPos topPosition;

    public FiveGTowerBlockEntity( BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIVE_G_TOWER_BLOCK_ENTITY_BLOCK_ENTITY, pos, state);
        topPosition = pos.up(32);
    }

    public void tick(ServerWorldAccess server) {
        //TODO: only do multiblock checking etc once every few ticks (like 20 or 40)
        //and pick equivalence class for each ticked 5g tower just like the fermenters

        var nearEntities = server.getOtherEntities(null, Box.of(topPosition.toCenterPos(),256,256,256), new EntityAttachments.IsEstrogennablePredicate());
        for (Entity nearEntity : nearEntities) {
            Vec3d delta = nearEntity.getPos().subtract(topPosition.toCenterPos());

            //for now action zone is a sphere. TODO: anisotropy!! like distance to a circle around the top instead
            //a bit like this: https://www.geometrictools.com/Documentation/DistanceToCircle3.pdf
            double dist2 = nearEntity.squaredDistanceTo(delta);
            if(dist2 > 16384) continue;

            EntityAttachments.giveEstrogenFor(nearEntity,5); //TODO: decay of effectiveness based on distance!
        }
    }
}
