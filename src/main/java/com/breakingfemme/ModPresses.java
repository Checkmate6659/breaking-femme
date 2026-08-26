package com.breakingfemme;

import com.breakingfemme.registries.press.PressHead;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.breakingfemme.BreakingFemme.id;

public class ModPresses {
    public static final PressHead PILL_HEAD = create(id("pill_press_head"), new FabricItemSettings().maxCount(1));
    public static final PressHead MASHING_HEAD = create(id("mashing_press_head"), new FabricItemSettings().maxCount(1));

    public static void register() {
    }

    private static PressHead create(Identifier id, FabricItemSettings settings) {
        var itm = Registry.register(Registries.ITEM, id, new Item(settings));
        return register(itm);
    }

    private static <I extends Item> PressHead register(I item) {
        return register(Registries.ITEM.getId(item), new PressHead(item));
    }

    private static <T extends PressHead> T register(Identifier id, T head) {
        return Registry.register(ModRegistries.PRESS_HEAD_REGISTRY, id, head);
    }
}
