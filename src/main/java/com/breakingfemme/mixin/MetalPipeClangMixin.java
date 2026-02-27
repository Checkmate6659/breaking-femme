package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.breakingfemme.ModSounds;
import com.breakingfemme.datagen.ModItemTagProvider;

import net.minecraft.entity.ItemEntity;
import net.minecraft.sound.SoundCategory;

@Mixin(ItemEntity.class)
public class MetalPipeClangMixin {
	public void onLanding() {
        ItemEntity entity = (ItemEntity)(Object)this;
		if(entity.fallDistance > 0.015625 && entity.getStack().isIn(ModItemTagProvider.METAL_PIPE))
            entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(), ModSounds.METAL_PIPE, SoundCategory.MASTER, 4.0f, 1.0f, true);
        entity.fallDistance = 0;
	}
}
