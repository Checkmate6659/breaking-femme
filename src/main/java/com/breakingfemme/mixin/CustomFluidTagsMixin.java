package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.breakingfemme.datagen.ModFluidTagProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;

@Debug(export = true)
@Mixin(Entity.class)
public class CustomFluidTagsMixin {
    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/entity/Entity;updateWaterState()Z", cancellable = true)
    private void breakingfemme$addCustomFluidMovement(CallbackInfoReturnable<Boolean> cir)
    {
        if(!cir.getReturnValue())
            cir.setReturnValue(((Entity)(Object)this).updateMovementInFluid(ModFluidTagProvider.WATER_LIKE, 0.014)); //same speed as water
    }

    //this one does the flowing!!
    @WrapOperation(method = "Lnet/minecraft/entity/Entity;getVelocityMultiplier()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$AbstractBlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean breakingfemme$isWaterLikeVSM(BlockState state, Block block, Operation<Boolean> original)
    {
        return state.getFluidState().isIn(ModFluidTagProvider.WATER_LIKE) || original.call(state, block);
    }

    @WrapOperation(method = "Lnet/minecraft/entity/Entity;updateSwimming()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean breakingfemme$isWaterLikeSwim(FluidState state, TagKey<Fluid> tag, Operation<Boolean> original)
    {
        return state.isIn(ModFluidTagProvider.WATER_LIKE) || original.call(state, tag);
    }

    //this method would put out fires, so we need another mixin
    @WrapOperation(method = "Lnet/minecraft/entity/Entity;checkWaterState()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    private boolean breakingfemme$isWaterLikeWSt(Entity entity, TagKey<Fluid> tag, double d, Operation<Boolean> original)
    {
        //this way, if the original call would have interacted with a fluid, it doesn't process WATER_LIKE
        return original.call(entity, tag, d) || original.call(entity, ModFluidTagProvider.WATER_LIKE, d);
    }

    //introducing
    @Inject(method = "Lnet/minecraft/entity/Entity;checkWaterState()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;extinguish()V"), cancellable = true)
    private void breakingfemme$stayOnFire(CallbackInfo ci) //not "returnable" since its void. lol.
    {
        //you want to get extinguished? *don't.*
        ci.cancel();
    }

    //and its older sister
    @ModifyExpressionValue(method = "Lnet/minecraft/entity/Entity;isWet()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    private boolean breakingfemme$isTouchingREALWater(boolean original)
    {
        //check if its actually water. not just water-like. those dont get you wet, or extinguish fires for that matter.
        Entity entity = (Entity)(Object)this;
        return original && entity.getFluidHeight(FluidTags.WATER) > 0;
    }

    @WrapOperation(method = "Lnet/minecraft/entity/Entity;updateSubmergedInWaterState()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean breakingfemme$isWaterLikeSub(Entity entity, TagKey<Fluid> tag, Operation<Boolean> original)
    {
        //this way, if the original call would have detected submerged in a fluid (water, or not if other mods installed), it still works
        return original.call(entity, tag) || original.call(entity, ModFluidTagProvider.WATER_LIKE);
    }
}
