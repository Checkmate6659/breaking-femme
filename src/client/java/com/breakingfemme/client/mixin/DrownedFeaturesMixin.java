package com.breakingfemme.client.mixin;

import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DrownedEntityModel.class)
public class DrownedFeaturesMixin {
	//feature positioning in animateModel function
	//references BipedFeaturesMixin code. this is only here because it wouldn't be called otherwise.
	@Inject(method = "animateModel(Lnet/minecraft/entity/Entity;FFF)V", at = @At("HEAD"))
    private void breakingfemme$position_features(Entity entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo ci) {
		((BipedFeaturesMixin)(Object)this).breakingfemme$position_features(entity, limbAngle, limbDistance, tickDelta, ci);
	}
}
