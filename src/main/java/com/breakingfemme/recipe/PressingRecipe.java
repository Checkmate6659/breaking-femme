package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.ModRegistries;
import com.breakingfemme.registries.press.PressHead;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.EitherCodec;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

public class PressingRecipe implements Recipe<PressingRecipe.Input> {

    private final PressHeadIngredient headIngredient;
    private final Ingredient inputItem;
    private final ItemStack resultItem;
    private final Identifier id;
    public PressingRecipe(Identifier id, Ingredient inputItem, ItemStack resultItem, PressHeadIngredient head) {
        this.inputItem = inputItem;
        this.resultItem = resultItem;
        this.id = id;
        this.headIngredient = head;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(Input inventory, World world) {
        if (inventory.isEmpty()) return false;
        for (int i = 0; i < inventory.size(); i++) if (inputItem.test(inventory.getStack(i))) return true;
        return false;
    }

    @Override
    public ItemStack craft(Input inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return resultItem;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public record PressHeadIngredient(@Nullable PressHead head,
                                      @Nullable TagKey<PressHead> tag) implements Predicate<PressHead> {
        public static final Codec<PressHeadIngredient> CODEC =
                new EitherCodec<>(
                        ModRegistries.PRESS_HEAD_REGISTRY
                                .getCodec()
                                .fieldOf("head").codec()
                        , TagKey
                        .codec(ModRegistries.Keys.PRESS_HEAD_KEY)
                        .fieldOf("tag").codec())
                        .xmap(PressHeadIngredient::decodeEither, PressHeadIngredient::encodeAsEither);

        public PressHeadIngredient {
            assert !(head != null && tag != null);
            assert !(head == null && tag == null);
        }

        public static PressHeadIngredient fromPacket(PacketByteBuf buf) {
            var either = buf.readEither(c -> buf.readRegistryValue(ModRegistries.PRESS_HEAD_REGISTRY),
                    c -> TagKey.of(ModRegistries.Keys.PRESS_HEAD_KEY, c.readIdentifier()));
            return decodeEither(either);
        }

        public static PressHeadIngredient fromJson(final JsonElement json) {
            return CODEC.decode(JsonOps.INSTANCE, json).resultOrPartial(BreakingFemme.LOGGER::error).orElseThrow().getFirst();
        }

        private static PressHeadIngredient decodeEither(Either<PressHead, TagKey<PressHead>> it) {
            if (it.left().isPresent()) {
                return new PressHeadIngredient(it.left().get(), null);
            } else {
                assert it.right().isPresent();
                return new PressHeadIngredient(null, it.right().get());
            }
        }

        public void write(PacketByteBuf buf) {
            buf.writeEither(encodeAsEither(), (l, h) -> l.writeRegistryValue(ModRegistries.PRESS_HEAD_REGISTRY, h), (l, t) -> l.writeIdentifier(t.id()));
        }

        public JsonElement toJson() {
            return CODEC.encodeStart(JsonOps.INSTANCE, this).resultOrPartial(BreakingFemme.LOGGER::error).orElseThrow();
        }

        private Either<PressHead, TagKey<PressHead>> encodeAsEither() {
            if (this.head != null && this.tag != null)
                throw new RuntimeException("ingredient had both tag and head set!! only one can be set at a time!!");
            else if (this.head != null) return Either.left(this.head);
            else if (this.tag != null) return Either.right(this.tag);
            else throw new RuntimeException("tried to encode empty recipe!!");
        }

        @Override
        public boolean test(PressHead head) {
            if (this.head != null) return this.head.equals(head);
            else if (this.tag != null) {
                for (RegistryEntry<PressHead> entry : ModRegistries.PRESS_HEAD_REGISTRY.iterateEntries(this.tag)) {
                    if (head.equals(entry.value())) {
                        return true;
                    }
                }
                return false;
            }
            throw new AssertionError();
        }
    }

    public static class Type implements RecipeType<PressingRecipe> {
        private Type() {
        }

        public static Type INSTANCE = new Type();

        public static final String ID = "pressing";
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe> {
        public static final String ID = "pressing";

        @Override
        public PressingRecipe read(Identifier id, JsonObject json) {
            var recipeJson = new Gson().fromJson(json, PressingRecipeJsonFormat.class);
            if (recipeJson.input == null) throw new JsonSyntaxException("Missing required input item");
            if (recipeJson.output == null) throw new JsonSyntaxException("Missing required output item");
            if (recipeJson.head == null) throw new JsonSyntaxException("Missing required press head");
            var input = Ingredient.fromJson(recipeJson.input);
            var output = ShapedRecipe.outputFromJson(recipeJson.output);
            var head = PressHeadIngredient.fromJson(recipeJson.head);

            return new PressingRecipe(id, input, output, head);
        }

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public PressingRecipe read(Identifier id, PacketByteBuf buf) {
            var input = Ingredient.fromPacket(buf);
            var output = buf.readItemStack();
            var head = PressHeadIngredient.fromPacket(buf);
            return new PressingRecipe(id, input, output, head);
        }

        public void write(JsonObject json, PressingRecipe recipe) {
            var recipeJsonFormat = new PressingRecipeJsonFormat(
                    recipe.inputItem.toJson().getAsJsonObject(),
                    recipe.headIngredient.toJson().getAsJsonObject(),
                    recipeJson(recipe)
            );
            var fields = new Gson().toJsonTree(recipeJsonFormat);
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : fields.getAsJsonObject().asMap().entrySet())
                json.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
        }

        @Override
        public void write(PacketByteBuf buf, PressingRecipe recipe) {
            recipe.inputItem.write(buf);
            buf.writeItemStack(recipe.resultItem);
            recipe.headIngredient.write(buf);
        }

        private JsonObject recipeJson(PressingRecipe recipe) {
            JsonObject inputJson = new JsonObject();
            inputJson.addProperty("item", Registries.ITEM.getId(recipe.resultItem.getItem()).toString());
            if (recipe.resultItem.getCount() > 1) {
                inputJson.addProperty("count", recipe.resultItem.getCount());
                return inputJson;
            }
            return inputJson;
        }

        private record PressingRecipeJsonFormat(
                JsonObject input,
                JsonObject head,
                JsonObject output
        ) {
        }

    }

    public static class Input extends SimpleInventory {
        public Input() {
            super(2);
        }
    }

}
