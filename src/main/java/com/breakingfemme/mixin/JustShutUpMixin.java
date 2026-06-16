package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrassilTelemetrySession;

@Mixin(value = YggdrassilTelemetrySession.class, priority = 1312)
public class JustShutUpMixin {
    @Overwrite
    void sendEvent(final String type, final JsonObject data) {
    }
}
