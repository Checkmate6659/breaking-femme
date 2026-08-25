package com.breakingfemme.block.entity.press;

import com.breakingfemme.ModBlockEntities;
import com.breakingfemme.ModBlocks;
import com.breakingfemme.block.press.PressHandleBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PressBottomBlockEntity extends BlockEntity {
    // all data stored here top and bottom are just dummies
    private final MultiblockVerifier multiblockVerifier = this.new MultiblockVerifier();
    private final Ticker ticker = this.new Ticker();

    /**
     * array of all the blocks that are valid for this multiblock aside from myself
     */


    public PressBottomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS_BOTTOM_BLOCK_ENTITY, pos, state);
    }

    private BlockPos getPressTopPosition() {
        return pos.up();
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    public BlockEntityTicker<PressBottomBlockEntity> getTicker() {
        return ticker;
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

            if (!valid) {
            }
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
