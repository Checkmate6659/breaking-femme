package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@Mixin(LivingEntity.class)
public class MetalPipeHitMixin {
    @Inject(at = @At("HEAD"), method = "onAttacking")
    public void breakingfemme$makeBonkSound(CallbackInfo ci)
    {
        LivingEntity entity = (LivingEntity)(Object)this;
        entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.MASTER, 4.0f, 1.0f, true);
        entity.sendMessage(Text.literal("client " + entity.getWorld().isClient()));
    }
}
