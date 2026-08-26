package com.breakingfemme.datagen.providers;

import com.breakingfemme.ModPresses;
import com.breakingfemme.ModRegistries;
import com.breakingfemme.ModTags;
import com.breakingfemme.registries.press.PressHead;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModPressHeadTagProvider extends FabricTagProvider<PressHead> {
    public ModPressHeadTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, ModRegistries.Keys.PRESS_HEAD_KEY, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(ModTags.Press.MASHES).add(ModPresses.MASHING_HEAD);
        getOrCreateTagBuilder(ModTags.Press.ENCASES_PILLS).add(ModPresses.PILL_HEAD);
    }
}
