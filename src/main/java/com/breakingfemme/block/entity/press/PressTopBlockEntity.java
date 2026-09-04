package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.registries.press.PressHead;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Optional;

public class PressTopBlockEntity extends BlockEntity implements SingleStackInventory {
    public static final double SCALE = 0.4375; //going up and down for rendering; just barely causes zfighting

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);
    @Range(from = 0, to = 1)
    private float progress = 0;
    private boolean freezeProgress = false;
    public PressTopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_TOP_BLOCK_ENTITY, pos, state);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Optional<PressHead> getHead() {
        return PressHead.getPressHead(getStack());
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (freezeProgress) return;
        var clampedProgress = MathHelper.clamp(progress, 0, 1);
        if (this.progress != clampedProgress) this.markDirty();
        this.progress = clampedProgress;
    }

    public void addProgress(float added)
    {
        boolean previously_in_air = progress != 1f; //i think float equality can be checked here because of clamp function
        setProgress(progress + added);
        if(previously_in_air && progress == 1f)
        {
            craft(); //apply recipe
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS); //play sound
        }
    }

    public void freezeProgress() {
        freezeProgress = true;
    }

    public void unfreezeProgress() {
        freezeProgress = false;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world instanceof ServerWorld server) {
            BlockState state = getCachedState();
            server.updateListeners(pos, state, state, 0);
            server.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.writeNbt(nbt, stacks);
        nbt.putFloat("progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (world != null && world.isClient()) stacks.clear();
        Inventories.readNbt(nbt, stacks);
        progress = MathHelper.clamp(nbt.getFloat("progress"), 0, 1);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        return this.stacks.get(0);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0) return ItemStack.EMPTY;
        if (stacks.get(0).isEmpty()) return ItemStack.EMPTY;
        var inv = Inventories.splitStack(stacks, 0, amount);
        markDirty();
        return inv;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0) return;
        this.stacks.set(0, stack);
        if (this.getStack().getCount() > this.getMaxCountPerStack()) {
            this.getStack().setCount(1);
        }
        markDirty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (stack.isEmpty()) return true;
        if (slot != 0) return false;
        return PressHead.isPressHead(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    //TODO: apply recipe
    void craft()
    {
        //
    }
}
