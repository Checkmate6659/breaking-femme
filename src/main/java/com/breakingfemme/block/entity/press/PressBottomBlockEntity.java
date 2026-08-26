package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.block.press.PressBottomBlock;
import com.breakingfemme.block.press.PressHandleBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PressBottomBlockEntity extends BlockEntity implements SidedInventory {
    // all data stored here top and bottom are just dummies
    private final MultiblockVerifier multiblockVerifier = this.new MultiblockVerifier();
    private final Ticker ticker = this.new Ticker();
    private final DefaultedList<ItemStack> slots = DefaultedList.ofSize(2, ItemStack.EMPTY);
    /**
     * array of all the blocks that are valid for this multiblock aside from myself
     */


    public PressBottomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_BOTTOM_BLOCK_ENTITY, pos, state);
    }

    private BlockPos getPressTopPosition() {
        return pos.up();
    }


    public BlockEntityTicker<PressBottomBlockEntity> getTicker() {
        return ticker;
    }

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private Direction front() {
        return this.getCachedState().get(PressBottomBlock.FACING);
    }

    private Direction back() {
        return front().getOpposite();
    }

    private Direction bottom() {
        return Direction.DOWN;
    }

    private List<Direction> outputSlotDirections() {
        return List.of(bottom(), front());
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == back()) {
            return new int[]{INPUT_SLOT};
        }
        if (outputSlotDirections().contains(side)) {
            return new int[]{OUTPUT_SLOT};
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (stack.isEmpty()) {
            return true;
        }
        if (dir == back() && slot == INPUT_SLOT) {
            if (slots.get(INPUT_SLOT).isEmpty()) {
                return true;
            }
            return ItemStack.canCombine(slots.get(INPUT_SLOT), stack);
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (stack.isEmpty()) return true;
        return slot == OUTPUT_SLOT && outputSlotDirections().contains(dir);
    }

    @Override
    public int size() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        return this.slots.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.slots.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.slots, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.slots, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.slots.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return SidedInventory.super.isValid(slot, stack);
    }

    @Override
    public void clear() {
        this.slots.clear();
    }

    public @Nullable PressTopBlockEntity getTop() {
        if (world == null) return null;
        return world.getBlockEntity(pos.up(), ModBlockEntities.PRESS_TOP_BLOCK_ENTITY).orElse(null);
    }

    private class Ticker implements BlockEntityTicker<PressBottomBlockEntity> {
        private boolean valid = false;

        @Override
        public void tick(World world, BlockPos pos, BlockState state, PressBottomBlockEntity blockEntity) {
            actualTick(world, pos, state);
        }

        private void actualTick(World world, BlockPos pos, BlockState state) {
            if (!world.isClient()) {
                tickServer((ServerWorldAccess) world, pos, state);
            }
        }

        @Environment(EnvType.SERVER)
        private void tickServer(ServerWorldAccess world, BlockPos pos, BlockState state) {
            if (multiblockVerifier.isValid(world)) setValid();
            else setInvalid();

            if (!valid) return;
            var top = getTop();
            assert top != null;
            if (top.isEmpty()) {
            } //we have no head so we can't work
            /// todo: do multiblock shit
        }

        private void setValid() {
            if (!valid) {
                onValidChanged(true);
                valid = true;
            }

        }

        private void setInvalid() {
            if (valid) {
                onValidChanged(false);
                valid = false;
            }
        }

        private void onValidChanged(boolean valid) {
            //
        }
    }

    private class MultiblockVerifier {
        @Nullable BlockPos validLever = null;

        private MultiblockVerifier() {
        }

        private boolean isValid(ServerWorldAccess world) {
            if (!world.getBlockState(getPressTopPosition()).isOf(ModBlocks.PRESS_TOP))
                return false;

            var lever = checkForLever(world);
            if (lever == null) return false;
            validLever = lever;
            return true;
        }

        @Nullable
        private BlockPos checkForLever(ServerWorldAccess world) {
            var topPos = pos.up();
            for (Direction facing : Direction.HORIZONTAL) {
                // we check all the sideways directions
                var leverPos = topPos.offset(facing);
                var leverState = world.getBlockState(leverPos);
                if (!leverState.isOf(ModBlocks.PRESS_CRANK)) continue;
                // we get facing
                var leverFacing = leverState.get(PressHandleBlock.FACING);
                // we found our match!
                if (leverFacing.getOpposite().equals(facing)) return leverPos;
            }
            return null;
        }

    }
}
