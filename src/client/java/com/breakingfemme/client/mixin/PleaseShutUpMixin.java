package com.breakingfemme.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
