package com.breakingfemme.mixin;

import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(VariantsBlockStateSupplier.class)
public interface VariantsBlockStateSupplierAccessor {
    @Accessor("variantMaps")
    List<BlockStateVariantMap> breakingfemme$getVariantMaps();
}
