package com.breakingfemme.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.SpeedVectorAccessor;

@Mixin(Entity.class)
public class ExtractSpeedMixin implements SpeedVectorAccessor {
	@Unique
	public Vec3d lastPos = null; //can't initialize this just yet => we drop 1 tick
	@Unique
	public Vec3d speed = Vec3d.ZERO;

	@Inject(at = @At("HEAD"), method = "tick")
	private void breakingfemme$grabLastPos(CallbackInfo info) {
		Vec3d pos = ((Entity)(Object)this).getPos();
		if(lastPos != null)
			speed = pos.subtract(lastPos); //speed in blocks/tick
		lastPos = pos;
	}

	public Vec3d breakingfemme$getSpeedVector()
	{
		return speed;
	}
}
