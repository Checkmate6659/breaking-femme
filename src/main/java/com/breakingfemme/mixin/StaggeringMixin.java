package com.breakingfemme.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.joml.SimplexNoise;
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
        
        if(!player.isOnGround()) //only stagger on the ground, not in the air
            return;

        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

        float stagger = etoh + 1.5f * ach - 0.75f; //coef of acetaldehyde pulled out of my ass, threshold not so much
        if(stagger < 0) //not drunk enough to get an effect
            return;
        stagger = (float)Math.tanh(stagger * stagger * 200); //formula pulled out of my ass once again

        float time = (player.getWorld().getTime() & 65535) * 9.587379924285257e-05f; //time in 65536-ticks, but *2pi (period of the staggering is that long, about an hour, should be unnoticeable); this var goes from 0 to 2pi in 65536t.
        float noiseval = SimplexNoise.noise((float)Math.cos(time) * 256 + 0.42069f, (float)Math.sin(time) * 256 + 6.7f);

        float coef = noiseval * stagger;
        Vec3d vel = player.getVelocity();
        Vec3d dir = vel.multiply(coef, 0, coef).crossProduct(new Vec3d(0, 1, 0)).add(0.0, -2.2737367544323206e-13, 0.0); //to make player touch ground and able to jump etc
        player.move(MovementType.SELF, dir); //add horizontal velocity perpendicular to current vel

        //make camera drift around
        //a bit too choppy like this tho... needs to be done per frame instead of per tick. also not overriding user input would be nice.
        /*float headp = SimplexNoise.noise((float)Math.sin(time) * 256 + 69007.47f, (float)Math.cos(time) * 256 + 137.42069f);
        float heady = SimplexNoise.noise(-(float)Math.cos(time) * 256 - 42.069f, (float)Math.sin(time) * 256 + 69420.874041f);
        float pitch = player.getPitch();
        float yaw = player.getHeadYaw();
        float cosp = (float)Math.cos(pitch * 0.017453292F); //pi/180 conversion factor
        pitch = pitch + headp * cosp;
        player.setPitch(pitch < -90 ? -90 : (pitch > 90 ? 90 : pitch)); //because i dont have access to Math.clamp for some reason
        player.setYaw(yaw + heady * cosp);*/
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

        float slowdown = etoh + 1.5f * ach - 1.0f; //coef of acetaldehyde pulled out of my ass, threshold not so much (based on just etoh)
        if(slowdown < 0f)
            slowdown = 0f;
        slowdown *= 0.125f; //we want about 25% speed when reaching 2g/L (theres also acetaldehyde)
        if(slowdown > 0.75f) //at least keep a quarter of original speed
            slowdown = 0.75f;

        cir.setReturnValue(cir.getReturnValue() * (1.0f - slowdown));
	}
}
