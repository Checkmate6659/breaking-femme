package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;

@Mixin(value = MinecraftClient.class, priority = 1312)
public class PleaseShutUpMixin {
    @Inject(method = "isTelemetryEnabledByApi", at = @At("HEAD"), cancellable = true)
    void breakingfemme$no(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isOptionalTelemetryEnabled", at = @At("HEAD"), cancellable = true)
    void breakingfemme$zipit(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isOptionalTelemetryEnabledByApi", at = @At("HEAD"), cancellable = true)
    void breakingfemme$shutup(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
