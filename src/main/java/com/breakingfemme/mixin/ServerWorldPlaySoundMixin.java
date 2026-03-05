package com.breakingfemme.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.KineticsAttachments;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Mixin(ServerWorld.class)
public class ServerWorldPlaySoundMixin {
    @Inject(method = "playSound", at = @At("HEAD"))
    private void breakingfemme$hurtClosePlayers(@Nullable PlayerEntity except, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci)
    {
        @SuppressWarnings("resource")
        ServerWorld sworld = (ServerWorld)(Object)this;
        List<ServerPlayerEntity> closePlayers = sworld.getPlayers(player -> !player.equals(except) && player.getPos().squaredDistanceTo(x, y, z) < 64);
        for(PlayerEntity player : closePlayers)
        {
            //TODO: move to separate method that gets called when block placed/broken, as sounds there are client-only; probably using gameevents
            float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);
            ach -= 0.5; //threshold for damage
            if(ach <= 0) continue;
            double strength = 1.015625 / (1 + player.getPos().squaredDistanceTo(x, y, z)) - 0.015625; //from 0 (at 8 blocks) to 1 (point blank)
            double damage = strength * ach * 2; //TODO: multiply by coefficient given by strength of sound (from array)
            if(damage < 0.5) return;
            player.damage(new DamageSource(
                sworld.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BreakingFemme.HEADACHE)), (float)damage - 0.5f
            );
        }
    }
}
