package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.yggdrasil.YggdrassilTelemetrySession;

@Mixin(value = YggdrassilTelemetrySession.class, priority = 1312)
public class JustShutUpMixin {
    @Inject(method = "sendEvent", at = @At("HEAD"), cancellable = true)
    void breakingfemme$zipit(CallbackInfo ci) {
        ci.cancel();
    }
}
