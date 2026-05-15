package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.datagen.ModFluidTagProvider;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;

@Mixin(Entity.class)
public class CustomFluidTagsMixin {
    @Inject(at = @At("RETURN"), method = "updateWaterState", cancellable = true)
    private void breakingfemme$addCustomFluidMovement(CallbackInfoReturnable<Boolean> cir)
    {
        if(!cir.getReturnValue())
            cir.setReturnValue(((Entity)(Object)this).updateMovementInFluid(ModFluidTagProvider.WATER_LIKE, 0.014)); //same speed as water
    }

    //this one does the flowing!!
    @WrapOperation(method = "getVelocityMultiplier", at = @At(value = "INVOKE", target = "isOf"))
    private boolean breakingfemme$isWaterLikeVSM(BlockState state, Block block, Operation<Boolean> original)
    {
        return state.getFluidState().isIn(ModFluidTagProvider.WATER_LIKE) || original.call(state, block);
    }

    @WrapOperation(method = "updateSwimming", at = @At(value = "INVOKE", target = "isIn"))
    private boolean breakingfemme$isWaterLikeSwim(FluidState state, TagKey<Fluid> tag, Operation<Boolean> original)
    {
        return state.isIn(ModFluidTagProvider.WATER_LIKE) || original.call(state, tag);
    }

    //this method would put out fires, so we need another mixin
    @WrapOperation(method = "checkWaterState", at = @At(value = "INVOKE", target = "updateMovementInFluid"))
    private boolean breakingfemme$isWaterLikeWSt(Entity entity, TagKey<Fluid> tag, double d, Operation<Boolean> original)
    {
        //this way, if the original call would have interacted with a fluid, it doesn't process WATER_LIKE
        return original.call(entity, tag, d) || original.call(entity, ModFluidTagProvider.WATER_LIKE, d);
    }

    //introducing
    @Redirect(method = "checkWaterState", at = @At(value = "INVOKE", target = "extinguish"))
    private void breakingfemme$stayOnFire(Entity entity)
    {
        //you want to get extinguished? *don't.*
        return;
    }

    //and its older sister
    @Redirect(method = "isWet", at = @At(value = "INVOKE", target = "isTouchingWater"))
    private boolean breakingfemme$isTouchingREALWater(Entity entity)
    {
        //check if its actually water. not just water-like. those dont get you wet, or extinguish fires for that matter.
        return entity.getFluidHeight(FluidTags.WATER) > 0;
    }

    @WrapOperation(method = "updateSubmergedInWaterState", at = @At(value = "INVOKE", target = "isSubmergedIn"))
    private boolean breakingfemme$isWaterLikeSub(Entity entity, TagKey<Fluid> tag, Operation<Boolean> original)
    {
        //this way, if the original call would have detected submerged in a fluid (water, or not if other mods installed), it still works
        return original.call(entity, tag) || original.call(entity, ModFluidTagProvider.WATER_LIKE);
    }
}
