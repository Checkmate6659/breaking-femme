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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TarBlock extends FluidBlock {
    public TarBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        FluidState fluid_state = world.getFluidState(pos);
        float height = fluid_state.getHeight();
        if(entity.getY() < pos.getY() + height) //check if touching fluid
        {
            entity.slowMovement(state, new Vec3d(0.0625, 0.015625, 0.0625)); //the state parameter is complete bogus here btw, not even used
            if(entity.isPlayer() && entity.getEyeY() < pos.getY() + height) //check if its a player and is submerged
                //what really should happen is a pitch (or tar) black overlay drawn over the player's screen, like the water overlay but opaque
                ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));
        }

        super.onEntityCollision(state, world, pos, entity);
    }
}
