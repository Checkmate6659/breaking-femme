package com.breakingfemme.client.utils;

import com.breakingfemme.registries.press.PressHead;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class PressUtils {
    private PressUtils() {
        throw new IllegalAccessError();
    }

    public static ModelIdentifier headModelId(Identifier id) {
        return new ModelIdentifier(id.withPrefixedPath("press/"), BlockModels.propertyMapToString(Map.of(PressHead.DIE, true)));
    }

    public static ModelIdentifier dieModelId(Identifier id) {
        return new ModelIdentifier(id.withPrefixedPath("press/"), BlockModels.propertyMapToString(Map.of(PressHead.DIE, false)));
    }
}
