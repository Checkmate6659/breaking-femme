package com.breakingfemme.block;

import com.breakingfemme.BreakingFemme;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AndrostadienedioneBlock extends PoisonousFluidBlock {
	//Novikov self-consistency damage type
	public static final RegistryKey<DamageType> NOVIKOV = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(BreakingFemme.MOD_ID, "novikov"));

    public AndrostadienedioneBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        FluidState fluid_state = world.getFluidState(pos);
        float height = fluid_state.getHeight();
        if(entity.isPlayer() && entity.getY() < pos.getY() + height) //check if touching fluid
            entity.damage(new DamageSource( //https://en.wikipedia.org/wiki/Novikov_self-consistency_principle
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(NOVIKOV)), 37921489645.0f
            );

        super.onEntityCollision(state, world, pos, entity);
    }
}
