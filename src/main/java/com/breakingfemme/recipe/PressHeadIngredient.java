package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModRegistries;
import com.breakingfemme.registries.press.PressHead;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.EitherCodec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class PressHeadIngredient implements Predicate<PressHead> {
    private final Either<PressHead, TagKey<PressHead>> headOrTag;
    public static final Codec<PressHeadIngredient> CODEC =
            new EitherCodec<>(
                    ModRegistries.PRESS_HEAD_REGISTRY
                            .getCodec()
                            .fieldOf("head").codec()
                    , TagKey
                    .codec(ModRegistries.Keys.PRESS_HEAD_KEY)
                    .fieldOf("tag").codec())
                    .xmap(PressHeadIngredient::of, PressHeadIngredient::asEither);

    public PressHeadIngredient(@NotNull Either<PressHead, TagKey<PressHead>> headOrTag) {
        this.headOrTag = headOrTag;
    }

    public static PressHeadIngredient of(PressHead head) {
        return new PressHeadIngredient(Either.left(head));
    }

    public static PressHeadIngredient of(TagKey<PressHead> tag) {
        return new PressHeadIngredient(Either.right(tag));
    }

    public static PressHeadIngredient fromPacket(PacketByteBuf buf) {
        var either = buf.readEither(c -> buf.readRegistryValue(ModRegistries.PRESS_HEAD_REGISTRY),
                c -> TagKey.of(ModRegistries.Keys.PRESS_HEAD_KEY, c.readIdentifier()));
        return of(either);
    }

    public static PressHeadIngredient fromJson(final JsonElement json) {
        return CODEC.decode(JsonOps.INSTANCE, json).resultOrPartial(BreakingFemme.LOGGER::error).orElseThrow().getFirst();
    }

    private static PressHeadIngredient of(Either<PressHead, TagKey<PressHead>> it) {
        return new PressHeadIngredient(it);
    }

    public void write(PacketByteBuf buf) {
        buf.writeEither(this.headOrTag, (l, h) -> l.writeRegistryValue(ModRegistries.PRESS_HEAD_REGISTRY, h), (l, t) -> l.writeIdentifier(t.id()));
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).resultOrPartial(BreakingFemme.LOGGER::error).orElseThrow();
    }

    public Either<PressHead, TagKey<PressHead>> asEither() {
        return headOrTag;
    }

    public Stream<PressHead> entries() {
        return this.headOrTag.map(Stream::of, PressHead::getTagged);
    }

    @Override
    public String toString() {
        return this.headOrTag.map(i -> "head$" + i.toString(), i -> "tag$" + i.toString());
    }

    @Override
    public boolean test(PressHead head) {
        return this.entries().anyMatch(head::equals);
    }
}
