package com.breakingfemme.registries.press;

import com.breakingfemme.ModRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PressHead implements ItemConvertible {
    public static final BooleanProperty DIE = BooleanProperty.of("is_die");
    public PressHead(Item item) {
        assert item != null;
        this.item = item;
    }

    protected Item item;

    public Identifier getId() {
        // should be fine.
        return Optional.ofNullable(ModRegistries.PRESS_HEAD_REGISTRY.getId(this)).orElseThrow();
    }

    @Override
    public Item asItem() {
        return item;
    }

    @Override
    public String toString() {
        return "press_head#" + getId().toString();
    }

    public MutableText getName() {
        return Text.translatable(getTranslationKey());
    }

    public String getTranslationKey() {
        return item.getTranslationKey();
    }

    public static boolean isPressHead(ItemConvertible item) {
        return isPressHead(item.asItem());
    }

    public static boolean isPressHead(ItemStack itm) {
        return isPressHead(itm.getItem());
    }

    public static boolean isPressHead(Item item) {
        return getPressHead(item).isPresent();
    }

    public static Optional<PressHead> getPressHead(ItemStack itm) {
        return getPressHead(itm.getItem());
    }

    public static Optional<PressHead> getPressHead(ItemConvertible itm) {
        return getPressHead(itm.asItem());
    }

    public static Optional<PressHead> getPressHead(Item item) {
        return ModRegistries.PRESS_HEAD_REGISTRY.streamEntries()
                .filter(it -> it.value().asItem() == item)
                .findFirst().map(RegistryEntry.Reference::value);
    }

    @Environment(EnvType.CLIENT)
    public Identifier getVariantFile() {
        return getId().withPrefixedPath("block/press/");
    }

    public static Stream<PressHead> getTagged(TagKey<PressHead> tag) {
        return StreamSupport.stream(ModRegistries.PRESS_HEAD_REGISTRY.iterateEntries(tag).spliterator(), false)
                .map(RegistryEntry::value);
    }
}
