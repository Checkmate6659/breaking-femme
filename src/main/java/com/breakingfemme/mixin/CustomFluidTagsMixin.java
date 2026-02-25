package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.datagen.ModFluidTagProvider;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class CustomFluidTagsMixin {
    //NOTE: this is NOT enough to do everything we want a fluid to do, we would need loads of stuff in LivingEntity as well
    @Inject(at = @At("RETURN"), method = "updateWaterState", cancellable = true)
    private void breakingfemme$addCustomFluidMovement(CallbackInfoReturnable<Boolean> cir)
    {
        if(!cir.getReturnValue())
            cir.setReturnValue(((Entity)(Object)this).updateMovementInFluid(ModFluidTagProvider.FLAMMABLE, 0.014)); //same speed as water
    }
}
