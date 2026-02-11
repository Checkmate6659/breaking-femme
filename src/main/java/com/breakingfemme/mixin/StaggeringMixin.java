package com.breakingfemme.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.KineticsAttachments;

@Mixin(PlayerEntity.class)
public class StaggeringMixin {
	@Inject(at = @At("RETURN"), method = "tickMovement") //why this not get called.
	private void updateLevels(CallbackInfo info) {
        PlayerEntity player = ((PlayerEntity)(Object)this);

        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);
        float stagger = etoh + 1.5f * ach - 0.0625f; //coef of acetaldehyde pulled out of my ass, threshold not so much
        if(stagger < 0) //not drunk enough to get an effect
            return;
        stagger = (float)Math.tanh(stagger * stagger * 200) * 0.5f; //formula pulled out of my ass once again

        Random random = player.getWorld().random;
        PerlinNoiseSampler sampler = new PerlinNoiseSampler(random); //to make walk less shaky
        float time = (player.getWorld().getTime() & 65535) * 9.587379924285257e-05f; //time in 65536-ticks, but *2pi (period of the staggering is that long, about an hour, should be unnoticeable)
        float noiseval = (float)sampler.sample(Math.cos(time) * 64 + 0.42069, Math.sin(time) * 64 + 6.7, 0.5);

        float coef = noiseval * stagger;
        Vec3d vel = player.getVelocity();
        player.setVelocity(vel.add(vel.multiply(coef, 0, coef).crossProduct(new Vec3d(0, 1, 0)))); //add horizontal velocity perpendicular to current vel
    }
}
