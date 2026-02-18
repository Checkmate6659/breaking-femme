package com.breakingfemme.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.KineticsAttachments;
import com.breakingfemme.networking.ModNetworking;

import io.netty.buffer.ByteBuf;

@Mixin(LivingEntity.class)
public class StaggeringMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void syncAttachments(CallbackInfo info) //mb this should be in a separate mixin? idk
    {
        if(!((LivingEntity)(Object)this).isPlayer())
            return;

        PlayerEntity player = ((PlayerEntity)(Object)this);

        if(!player.getWorld().isClient()) //we want to execute this only on the server
        {
            //send a packet to the client to update its levels
            //corresponding receiving is in ModNetworking
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL));
            buf.writeFloat(KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE));
            ServerPlayNetworking.send((ServerPlayerEntity)player, ModNetworking.KINETICS_SYNC_ID, buf);
        }
    }

	@Inject(at = @At("HEAD"), method = "tickMovement")
	private void addStagger(CallbackInfo info) {
        if(!((LivingEntity)(Object)this).isPlayer())
            return;

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
        float noiseval = (float)sampler.sample(Math.cos(time) + 0.42069, Math.sin(time) + 6.7, 0.5); //what multiplier for cos and sin of time? even 64 too much?!?!

        float coef = noiseval * stagger;
        Vec3d vel = player.getVelocity();
        //player.move(MovementType.SELF, vel.multiply(coef, 0, coef).crossProduct(new Vec3d(0, 1, 0))); //add horizontal velocity perpendicular to current vel
        //player.move(MovementType.SELF, new Vec3d(0.03, 0.0, 0.0)); //doesn't pass adjustMovementForCollision??
        player.setPosition(player.getPos().add(new Vec3d(0.03, 0.0, 0.0))); //even forcefully changing the player position doesn't work???? wtf is going on?
        //player.sendMessage(Text.literal("stagger " + stagger + " noise " + noiseval));
        //player.sendMessage(Text.literal("isclient sideways " + player.getWorld().isClient()));
        //player.input.movementSideways = player.input.movementForward * noiseval * stagger;
    }

    //TODO: make eye position slowly drift
    //TODO (different mixin): make camera move around slower, like brewin' n chewin'

    //this is called separately on the client and the server
    //on the server it does nothing and attachments are right
    //on the client it actually does sth and the attachments are wrong
    //ig for players specifically the client is entirely responsible for the player movement, but the server has the attachments
    @Inject(at = @At("RETURN"), method = "getMovementSpeed", cancellable = true)
	private void addSlowdown(CallbackInfoReturnable<Float> cir) {
        if(!((LivingEntity)(Object)this).isPlayer())
            return;

        PlayerEntity player = ((PlayerEntity)(Object)this);

        if(!player.getWorld().isClient()) //don't execute on the server!
            return;

        float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL); //why this 0
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

        float slowdown = etoh + 1.5f * ach - 0.125f; //coef of acetaldehyde pulled out of my ass, threshold not so much (based on just etoh)
        if(slowdown < 0f)
            slowdown = 0f;
        slowdown *= 0.125f; //we want about 25% speed when reaching 2g/L (theres also acetaldehyde)
        if(slowdown > 0.75f) //at least keep a quarter of original speed
            slowdown = 0.75f;

        player.sendMessage(Text.literal("player " + player.getEntityName()));
        player.sendMessage(Text.literal("etoh " + etoh));
        player.sendMessage(Text.literal("ach " + ach));
        player.sendMessage(Text.literal("slowdown " + slowdown));
        //player.sendMessage(Text.literal("isclient " + player.getWorld().isClient()));
        cir.setReturnValue(cir.getReturnValue() * (1.0f - slowdown));
	}
}
