package com.breakingfemme.block;

import com.breakingfemme.BreakingFemme;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AndrostadienedioneBlock extends FluidBlock {
    public AndrostadienedioneBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        FluidState fluid_state = world.getFluidState(pos);
        float height = fluid_state.getHeight();
        if(entity.getY() < pos.getY() + height) //check if touching fluid
            BreakingFemme.LOGGER.info("AAAA"); //TODO: novikov self consistency principle

        super.onEntityCollision(state, world, pos, entity);
    }
}
