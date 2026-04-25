package com.breakingfemme.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Accessor("weather")
    public Biome.Weather breakingfemme$getWeather();

    @Invoker("getTemperature")
    public float breakingfemme$getTemperature(BlockPos blockPos);
}
