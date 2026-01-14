package com.breakingfemme.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoisonousFluidBlock extends FluidBlock {
    public PoisonousFluidBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        FluidState fluid_state = world.getFluidState(pos);
        float height = fluid_state.getHeight();
        if(entity.isLiving() && entity.getY() < pos.getY() + height) //check if touching fluid
            ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2));

        super.onEntityCollision(state, world, pos, entity);
    }
}
