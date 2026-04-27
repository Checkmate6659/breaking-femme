package com.breakingfemme.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.KineticsAttachments;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class ScreenShakeMixin {
    //make the screen violently shake when the player is hung over
    @Inject(at = @At("HEAD"), method = "renderWorld", cancellable = true)
	private void breakingfemme$violentlyShakeScreen(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Random rng = player.getRandom();
        float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);
        ach -= 0.5f; //minimum threshold for screen shake
        if(ach <= 0.0f) ach = 0.0f;
        else ach = (float)Math.exp(-1.0f / ach);

        //TODO: create the quaternion directly, instead of rotating around one axis then another; these almost commute for small rotations but not quite
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((rng.nextFloat() * 2.0f - 1.0f) * ach * 0.125f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((rng.nextFloat() * 2.0f - 1.0f) * ach * 0.125f));
    }
}
