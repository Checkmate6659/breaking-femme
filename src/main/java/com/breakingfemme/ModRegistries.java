package com.breakingfemme;

import com.breakingfemme.registries.press.PressHead;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import static com.breakingfemme.BreakingFemme.id;

public class ModRegistries {

    public static final Registry<PressHead> PRESS_HEAD_REGISTRY = FabricRegistryBuilder.createSimple(Keys.PRESS_HEAD_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private ModRegistries() {
        throw new AssertionError();
    }

    public static void init() {
        Keys.init();
    }

    public static class Keys {
        public static void init() {
        }

        private Keys() {
            throw new AssertionError();
        }

        public static final RegistryKey<Registry<PressHead>> PRESS_HEAD_KEY = RegistryKey.ofRegistry(id("press_heads"));
    }
}
