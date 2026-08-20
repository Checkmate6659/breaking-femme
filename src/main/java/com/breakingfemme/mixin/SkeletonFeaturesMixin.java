package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.Entity;

@Mixin(value = SkeletonEntityModel.class)
public class SkeletonFeaturesMixin {
	//feature positioning in animateModel function
	//references BipedFeaturesMixin code. this is only here because it wouldn't be called otherwise.
	@Inject(method = "animateModel(Lnet/minecraft/entity/Entity;FFF)V", at = @At("HEAD"))
    private void breakingfemme$position_features(Entity entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci) {
		((BipedFeaturesMixin)(Object)this).breakingfemme$position_features(entity, limbAngle, limbDistance, tickDelta, ci);
	}
}
