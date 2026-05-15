package com.breakingfemme.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.breakingfemme.datagen.ModFluidTagProvider;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.RaycastContext;

@Mixin(RaycastContext.FluidHandling.class)
public class CustomFluidTagsRaycastMixin {
    @Definition(id = "WATER", field = "Lnet/minecraft/world/RaycastContext$FluidHandling;WATER:Lnet/minecraft/world/RaycastContext$FluidHandling;")
    @Expression("WATER = @(new ?(?, ?, ?))")
    @ModifyArg(method = "<clinit>", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static Predicate<FluidState> breakingfemme$isWaterLike(Predicate<FluidState> predicate) {
        return state -> {
            return predicate.test(state) || state.isIn(ModFluidTagProvider.WATER_LIKE);
        };
    }
}
