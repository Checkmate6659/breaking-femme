package com.breakingfemme.mixin;

import com.breakingfemme.ModSounds;
import net.minecraft.entity.ItemEntity;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static com.breakingfemme.ModTags.Item.METAL_PIPE;

@Mixin(ItemEntity.class)
public class MetalPipeClangMixin {
    @Unique
    public void onLanding() {
        ItemEntity entity = (ItemEntity)(Object)this;
        if (entity.fallDistance > 0.015625 && entity.getStack().isIn(METAL_PIPE))
            entity.getWorld().playSound(entity.getX(), entity.getY(), entity.getZ(), ModSounds.METAL_PIPE, SoundCategory.MASTER, 4.0f, 1.0f, true);
        entity.fallDistance = 0;
	}
}
