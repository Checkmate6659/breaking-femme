package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class PressHandleBlockEntity extends BlockEntity {
    private double angle_normalized = 0d; //angle in radians between 0 and 2π (TODO: ticking & rendering!)
    private final int rotationsSinceLastChecked = 0;
    public PressHandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_HANDLE_BLOCK_ENTITY, pos, state);
    }

    public void setAngle(double angle) {
//        final double TWO_PI = 2 * PI;
//        final int rotations = (int) Math.abs(angle / TWO_PI);
//        rotationsSinceLastChecked += rotations;
//        var clamped = Math.clamp(0,1,angle);
//        if (normalized != angle_normalized) {
//            onAngleChanged();
//        }
//        angle_normalized = normalized;
        throw new NotImplementedException("todo");
    }

    private void onAngleChanged() {
        this.markDirty();
        if (getWorld() instanceof ServerWorld server) {
            final BlockState state = getCachedState();
            server.updateListeners(pos, state, state, 0);
            server.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("angle", angle_normalized);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        angle_normalized = nbt.getDouble("angle");
    }

    //TODO: ticker and renderer
}
