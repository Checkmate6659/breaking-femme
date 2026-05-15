package com.breakingfemme.block;

import com.breakingfemme.BreakingFemme;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CausticFluidBlock extends FluidBlock {
    final float DAMAGE;

    public CausticFluidBlock(FlowableFluid fluid, AbstractBlock.Settings settings, float damage) {
        super(fluid, settings);
        DAMAGE = damage;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        FluidState fluid_state = world.getFluidState(pos);
        float height = fluid_state.getHeight();
        if(entity.isLiving() && entity.getY() < pos.getY() + height) { //check if touching fluid
            entity.setOnFireFor(10); //10 seconds of fire! (except if fluid has the water tag)
            entity.damage(new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.SODIUM)), DAMAGE
            );
        }

        super.onEntityCollision(state, world, pos, entity);
    }
}
