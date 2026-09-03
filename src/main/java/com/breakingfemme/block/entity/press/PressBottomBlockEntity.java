package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.ModRecipes;
import com.breakingfemme.block.press.PressBottomBlock;
import com.breakingfemme.block.press.PressHandleBlock;
import com.breakingfemme.block.press.PressTopBlock;
import com.breakingfemme.recipe.PressingRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PressBottomBlockEntity extends BlockEntity implements SidedInventory {
    // all data stored here top and bottom are just dummies
    private final RecipeManager.MatchGetter<PressingRecipe.Input, PressingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(ModRecipes.PRESSING_RECIPE.type());

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


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.writeNbt(nbt, slots);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.readNbt(nbt, slots);
    }


    private class Ticker implements BlockEntityTicker<PressBottomBlockEntity> {
        private boolean valid = false;
        private @Nullable PressingRecipe currentRecipe = null;

        @Override
        public void tick(World world, BlockPos pos, BlockState state, PressBottomBlockEntity blockEntity) {
            actualTick(world, pos, state);
        }

        private void actualTick(World world, BlockPos pos, BlockState state) {
            if (!world.isClient()) {
                tickServer((ServerWorldAccess) world, pos, state);
            }
        }

        private final static float PROGRESS_PER_TICK = 20f / 100f;

        private void tickServer(ServerWorldAccess world, BlockPos pos, BlockState state) {
            if (multiblockVerifier.isValid(world)) setValid();
            else setInvalid();

            if (!valid) return;
            final var top = multiblockVerifier.validTop;
            final var lever = multiblockVerifier.validLever;
            assert top != null && lever != null;
            // we're cooking boys!
            final var in = new PressingRecipe.Input(top.getStack(), getStack(INPUT_SLOT));
            // we get the first recipe that this qualifies for
            final var match = matchGetter.getFirstMatch(in, (World) world).orElse(null);
            if (currentRecipe != match) {
                top.setProgress(0);
                top.freezeProgress();
                currentRecipe = match;
            }
            if (currentRecipe == null) {
                top.freezeProgress();
                top.setProgress(0);
                return;
            }
            // we check if we can even output
            final var recipeOutput = currentRecipe.getOutput(world.getRegistryManager());
            final var outputStack = getStack(OUTPUT_SLOT);
            if (recipeOutput.isEmpty()) return;
            if ((!ItemStack.canCombine(outputStack, recipeOutput)) && !outputStack.isEmpty() || (outputStack.getCount() + recipeOutput.getCount() > Math.min(getMaxCountPerStack(), outputStack.getMaxCount()))) {
                top.setProgress(0);
                top.freezeProgress();
                return;
            }
            if (getStack(INPUT_SLOT).isEmpty()) {
                top.setProgress(0);
                top.freezeProgress();
                return;
            }
            // check if we can even consume items
            if (!currentRecipe.getInputItem().test(getStack(INPUT_SLOT))) {
                top.freezeProgress();
                top.setProgress(0);
                return;
            }
            top.unfreezeProgress();
            // we can safely craft
            if (top.getProgress() >= 1f) {
                craftItem(world, top, in);
            }
        }

        private void craftItem(ServerWorldAccess world, PressTopBlockEntity top, PressingRecipe.Input input) {
            final var outputStack = getStack(OUTPUT_SLOT);
            assert currentRecipe != null;
            removeStack(INPUT_SLOT, 1);
            var actual_output = currentRecipe.craft(input, world.getRegistryManager());
            if (!actual_output.isEmpty()) {
                if (outputStack.isEmpty()) {
                    setStack(OUTPUT_SLOT, actual_output);
                } else {
                    actual_output.setCount(actual_output.getCount() + outputStack.getCount());
                    setStack(OUTPUT_SLOT, actual_output);
                }
            }
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

    public Direction facingInDirection() {
        return getCachedState().get(PressBottomBlock.FACING);
    }

    private class MultiblockVerifier {
        @Nullable PressHandleBlockEntity validLever = null;
        @Nullable PressTopBlockEntity validTop = null;

        private MultiblockVerifier() {
        }

        private boolean isValid(WorldAccess world) {
            if (!world.getBlockState(getPressTopPosition()).isOf(ModBlocks.PRESS_TOP)) return false;
            if (!world.getBlockState(getPressTopPosition()).get(PressTopBlock.FACING).equals(facingInDirection()))
                return false;
            validTop = world.getBlockEntity(pos, ModBlockEntities.PRESS_TOP_BLOCK_ENTITY).orElse(null);
            if (validTop == null) return false;
            var lever = checkForLever(world);
            if (lever == null) return false;
            validLever = world.getBlockEntity(lever, ModBlockEntities.PRESS_HANDLE_BLOCK_ENTITY).orElse(null);
            return validLever != null;
        }

        @Nullable
        private BlockPos checkForLever(WorldAccess world) {
            var leverPos = pos.up().offset(facingInDirection().rotateYCounterclockwise());
            if (!world.getBlockState(leverPos).isOf(ModBlocks.PRESS_HANDLE)) return null;
            if (!world.getBlockState(leverPos).get(PressHandleBlock.FACING).equals(facingInDirection())) return null;
            return pos;
        }

    }
}
