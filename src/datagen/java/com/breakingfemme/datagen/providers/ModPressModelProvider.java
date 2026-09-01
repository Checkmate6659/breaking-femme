package com.breakingfemme.datagen.providers;

import com.breakingfemme.ModPresses;
import com.breakingfemme.data.PressModelProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static com.breakingfemme.BreakingFemme.id;

public class ModPressModelProvider extends PressModelProvider {
    public ModPressModelProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output);
    }

    @Override
    public void registerVariants() {
        this.registerHead(ModPresses.PILL_HEAD, id("block/press/pill_head"), id("block/press/pill_die"));
        this.registerHead(ModPresses.MASHING_HEAD, id("block/press/mash_head"), null);
    }
}
