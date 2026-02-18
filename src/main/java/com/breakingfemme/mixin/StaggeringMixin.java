package com.breakingfemme.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.KineticsAttachments;

@Mixin(LivingEntity.class)
public class StaggeringMixin {
    @Inject(at = @At("HEAD"), method = "tickMovement")
	private void addStagger(CallbackInfo info) {
        if(!((LivingEntity)(Object)this).isPlayer())
            return;

        PlayerEntity player = ((PlayerEntity)(Object)this);

        if(!player.getWorld().isClient()) //don't execute on the server
            return;
        
        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

        float stagger = etoh + 1.5f * ach - 0.0625f; //coef of acetaldehyde pulled out of my ass, threshold not so much
        if(stagger < 0) //not drunk enough to get an effect
            return;
        stagger = (float)Math.tanh(stagger * stagger * 200) * 0.5f; //formula pulled out of my ass once again

        Random random = player.getWorld().random;
        SimplexNoiseSampler sampler = new SimplexNoiseSampler(random); //why is it shaky? theres not supposed to be octaves. but ig its fine, it sells the effect a bit more.
        float time = (player.getWorld().getTime() & 65535) * 9.587379924285257e-05f; //time in 65536-ticks, but *2pi (period of the staggering is that long, about an hour, should be unnoticeable); this var goes from 0 to 2pi in 65536t.
        float noiseval = (float)sampler.sample(Math.cos(time) * 1024 + 0.42069, Math.sin(time) * 1024 + 6.7); //what multiplier for cos and sin of time? even 64 too much?!?!

        float coef = noiseval * stagger;
        Vec3d vel = player.getVelocity();
        player.move(MovementType.SELF, vel.multiply(coef, 0, coef).crossProduct(new Vec3d(0, 1, 0))); //add horizontal velocity perpendicular to current vel
        //player.sendMessage(Text.literal("coords " + Math.cos(time) * 1024 + 0.42069 + ", " + Math.sin(time) * 1024 + 6.7));
        //player.sendMessage(Text.literal("noise " + noiseval));
    }

    //TODO: make eye position slowly drift
    //TODO (different mixin): make camera move around slower, like brewin' n chewin'

    //this is called separately on the client and the server
    //on the server it does nothing
    //on the client it actually does sth
    //ig for players specifically the client is entirely responsible for the player movement, but the server has the attachments
    @Inject(at = @At("RETURN"), method = "getMovementSpeed", cancellable = true)
	private void addSlowdown(CallbackInfoReturnable<Float> cir) {
        if(!((LivingEntity)(Object)this).isPlayer())
            return;

        PlayerEntity player = ((PlayerEntity)(Object)this);

        if(!player.getWorld().isClient()) //don't execute on the server
            return;

        //TODO: disable during creative flying

        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL); //why this 0
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

        float slowdown = etoh + 1.5f * ach - 0.125f; //coef of acetaldehyde pulled out of my ass, threshold not so much (based on just etoh)
        if(slowdown < 0f)
            slowdown = 0f;
        slowdown *= 0.125f; //we want about 25% speed when reaching 2g/L (theres also acetaldehyde)
        if(slowdown > 0.75f) //at least keep a quarter of original speed
            slowdown = 0.75f;

        cir.setReturnValue(cir.getReturnValue() * (1.0f - slowdown));
	}
}
