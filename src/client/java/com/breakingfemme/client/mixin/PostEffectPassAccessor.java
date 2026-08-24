package com.breakingfemme.client.mixin;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostEffectProcessor.class)
public interface PostEffectPassAccessor {
    @Accessor("passes")
    List<PostEffectPass> breakingfemme$getPasses();

    @Accessor("passes")
    void breakingfemme$setPasses(List<PostEffectPass> passes);
}
