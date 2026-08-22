package com.breakingfemme.block.entity;

import com.breakingfemme.EntityAttachments;
import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModCriterions;
import com.breakingfemme.block.FiveGTowerControllerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.world.ServerWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class FiveGTowerBlockEntity extends BlockEntity {
    private BlockPos topPosition;
    private BlockPos bottomPosition;
    protected FiveGTowerBehavior behavior = this.new FiveGTowerBehavior();
    private int transmitter_count = 0;
    private int last_checked = -1;
    private boolean valid = false;
    public FiveGTowerBlockEntity( BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIVE_G_TOWER_BLOCK_ENTITY_BLOCK_ENTITY, pos, state);
        topPosition = pos;
    }

    public BlockPos getTopPosition() {
        return topPosition;
    }

    public BlockPos getBottomPosition() {
        return bottomPosition;
    }

    private void setInvalid() {
        if (valid) markDirty();
        valid = false;
    }

    private void setValid() {
        if (!valid) markDirty();
        valid = true;
    }

    private static boolean isValidStructureBlock(BlockState state) {
        return (state.isOf(ModBlocks.FIVE_G_SCAFFOLDING) || state.isOf(ModBlocks.FIVE_G_TOWER_HEAD) && isValidRotation(state));
    }

    private static boolean isValidRotation(BlockState state) {
        if (state.contains(Properties.AXIS)) {
            var axis = state.get(Properties.AXIS);
            return axis == Direction.Axis.Y;
        }
        return true;

    }

    private void updateValid(ServerWorldAccess server, BlockState state) {
        var blockBehind =
                pos.offset(state.get(FiveGTowerControllerBlock.FACING).getOpposite(), 1);
        var blockBehindState = server.getBlockState(blockBehind);
        // we check if it is a structural block
        if (!blockBehindState.isOf(ModBlocks.FIVE_G_SCAFFOLDING)) {
            setInvalid();
            return;
        }
        if (!isValidRotation(blockBehindState)) {
            setInvalid();
            return;
        }
        bottomPosition = blockBehind;
        transmitter_count = 0;
        // now we count upwards
        {
            var targetPos = blockBehind.up();
            var targetBlock = server.getBlockState(targetPos);
            while (isValidStructureBlock(targetBlock)) {
                if (targetBlock.isOf(ModBlocks.FIVE_G_TOWER_HEAD)) transmitter_count++;
                targetPos = targetPos.up();
                targetBlock = server.getBlockState(targetPos);
            }
            if (isValidStructureBlock(targetBlock)) {
                topPosition = targetPos;
            } else {
                topPosition = targetPos.down();
            }
        }
        if (transmitter_count <= 0) {
            setInvalid();
            return;
        }
        // we should be valid now
        setValid();


    }

    public void possiblyValidateMultiblock(ServerWorldAccess access, BlockState state) {
        last_checked++;
        if (last_checked < 20 && last_checked != -1) return;
        last_checked = 0;
        updateValid(access, state);
    }

    public void tick(ServerWorldAccess server, BlockState state) {
        possiblyValidateMultiblock(server, state);
        if (valid) behavior.tickValid(server);

    }

    protected class FiveGTowerBehavior {
        protected final List<UUID> entitiesAffectedAlready = new ArrayList<>();

        public void tickValid(ServerWorldAccess server) {
            if (transmitter_count <= 0) return;
            entitiesAffectedAlready.clear();
            // first we estrogen around a tiny area next to us
            estrogenInArea(server, Box.from(new BlockBox(bottomPosition.getX() - 3, bottomPosition.getY() - 3, bottomPosition.getZ() - 3, topPosition.getX() + 3, topPosition.getY() + 3, topPosition.getZ() + 3)));
            // now we estrogen everywhere
            estrogenInArea(server, Box.of(topPosition.toCenterPos(), 256.0, 256.0, 256.0));
        }

        private void estrogenInArea(ServerWorldAccess server, Box box) {
            //and pick equivalence class for each ticked 5g tower just like the fermenters
            var nearEntities = server.getOtherEntities(null, box,
                    new EntityAttachments.IsEstrogennablePredicate()
                            .and(new AlreadyEffectedPredicate().negate())); // we need to filter out entities we already
            // did something with
            for (Entity nearEntity : nearEntities) {// todo: range should be affected by transmitters
                Vec3d delta = nearEntity.getPos().subtract(topPosition.toCenterPos());
                //for now action zone is a sphere. TODO: anisotropy!! like distance to a circle around the top instead
                //a bit like this: https://www.geometrictools.com/Documentation/DistanceToCircle3.pdf
                double dist2 = nearEntity.squaredDistanceTo(delta);
                if (dist2 > 16384) continue;
                EntityAttachments.giveEstrogenFor(nearEntity, 5); //TODO: decay of effectiveness based on distance!
                entitiesAffectedAlready.add(nearEntity.getUuid());
                if (nearEntity instanceof ServerPlayerEntity nearPlayer)
                    ModCriterions.WITHIN_RANGE_OF_FIVE_G_TOWER.trigger(nearPlayer);

            }
        }

        private final class AlreadyEffectedPredicate implements Predicate<Entity> {

            @Override
            public boolean test(Entity o) {
                return entitiesAffectedAlready.contains(o.getUuid());
            }
        }
    }
}
