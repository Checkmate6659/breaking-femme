package com.breakingfemme.registries.press;

import com.breakingfemme.ModRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PressHead implements ItemConvertible {
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
}
