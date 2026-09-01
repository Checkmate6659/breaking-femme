package com.breakingfemme.client.mixin;

import com.breakingfemme.ModRegistries;
import com.breakingfemme.client.utils.PressUtils;
import com.breakingfemme.registries.press.PressHead;
import com.google.common.base.Splitter;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static net.minecraft.client.render.model.ModelLoader.BLOCK_STATES_FINDER;

@Debug(export = true)
@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    @Final
    private static Splitter COMMA_SPLITTER;
    @Shadow
    @Final
    private static Splitter KEY_VALUE_SPLITTER;
    @Shadow
    @Final
    private Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates;
    @Shadow
    @Final
    private ModelVariantMap.DeserializationContext variantMapDeserializationContext;

    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    @Shadow
    protected abstract void putModel(Identifier id, UnbakedModel unbakedModel);

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=blocks")
            , method = "<init>(Lnet/minecraft/client/color/block/BlockColors;Lnet/minecraft/util/profiler/Profiler;Ljava/util/Map;Ljava/util/Map;)V")
    private void modelLoader(CallbackInfo ci) {
        for (PressHead head : ModRegistries.PRESS_HEAD_REGISTRY) {
            this.addModel(PressUtils.headModelId(head.getId()));
            this.addModel(PressUtils.dieModelId(head.getId()));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "loadModel(Lnet/minecraft/util/Identifier;)V", cancellable = true)
    private void loadModel(Identifier id, CallbackInfo ci) {
        if (!(id instanceof ModelIdentifier modelIdentifier)) return;
        if (!modelIdentifier.getVariant().contains(PressHead.DIE.getName())) return;
        Identifier blockStatesId = BLOCK_STATES_FINDER.toResourcePath(id);
        var state = this.blockStates.get(blockStatesId);
        if (state == null) return;
        this.variantMapDeserializationContext.setStateFactory(Blocks.AIR.getStateManager());
        var variantMap = state.stream().map(it -> ModelVariantMap.fromJson(this.variantMapDeserializationContext, it.data())).findFirst().orElse(null);
        if (variantMap == null) return;
        var possibleModel = variantMap.getVariantMap().get(modelIdentifier.getVariant());
        if (possibleModel == null) return;
        this.putModel(id, possibleModel);
        ci.cancel();

    }
}
