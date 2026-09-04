package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.block.press.PressHandleBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;

import org.jetbrains.annotations.Nullable;

public class PressHandleBlockEntity extends BlockEntity {
    private double angle_normalized = 0d; //angle in radians between 0 and 2π (TODO: rendering!)

    public PressHandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_HANDLE_BLOCK_ENTITY, pos, state);
    }

    private void onAngleChanged() {
        this.markDirty();
        if (getWorld() instanceof ServerWorld server) {
            final BlockState state = getCachedState();
            server.updateListeners(pos, state, state, 0);
            server.getChunkManager().markForUpdate(pos);
        }
    }

    //packets for syncing rendering (TODO)
    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void update(boolean up)
    {
        float progress_delta = up ? -0.05f : 0.05f;

        if(world.getBlockEntity(pos.offset(world.getBlockState(pos).get(PressHandleBlock.FACING).rotateClockwise(Axis.Y))) instanceof PressTopBlockEntity ptbe)
        {
            ptbe.addProgress(progress_delta);
            angle_normalized = ptbe.getProgress() * 12.566370614359172 * PressTopBlockEntity.SCALE; //that number is 4pi
            onAngleChanged();
        }
    }

    //TODO: ticker and renderer
}
