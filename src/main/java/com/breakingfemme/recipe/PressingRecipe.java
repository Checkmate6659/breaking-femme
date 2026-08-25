package com.breakingfemme.recipe;

import com.breakingfemme.ModRegistries;
import com.breakingfemme.registries.press.PressHead;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

public class PressingRecipe implements Recipe<PressingRecipe.Input> {

    public static class PressHeadIngredient implements Predicate<PressHead> {
        private @Nullable TagKey<PressHead> tag = null;
        public static final Codec<PressHead> codec = RecordCodecBuilder.create(i -> i.group(
                RegistryKey.createCodec(ModRegistries.Keys.PRESS_HEAD_KEY).optionalFieldOf("press"),
                TagKey.codec(ModRegistries.Keys.PRESS_HEAD_KEY).optionalFieldOf("tag")
        ));

        public PressHeadIngredient(@Nullable TagKey<PressHead> tag, @Nullable PressHead press) {
            this.tag = tag;
            this.press = press;
        }

        private @Nullable PressHead press = null;

        public PressHeadIngredient(@Nullable TagKey<PressHead> tag) {
            this.tag = tag;
        }

        public PressHeadIngredient(@Nullable PressHead press) {
            this.press = press;
        }

        @Override
        public boolean test(PressHead head) {
            return false;
        }
    }
    private final Ingredient inputItem;
    private final ItemStack resultItem;
    private final Identifier id;

    public PressingRecipe(Identifier id, Ingredient inputItem, ItemStack resultItem) {
        this.inputItem = inputItem;
        this.resultItem = resultItem;
        this.id = id;
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
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public static class Type implements RecipeType<PressingRecipe> {
        private Type() {
        }

        public static Type INSTANCE = new Type();

        public static final String id = "pressing";
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe> {
        private record PressingRecipeJsonFormat(
                JsonObject input,
                JsonObject output
        ) {
        }

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public PressingRecipe read(Identifier id, JsonObject json) {
            var recipeJson = new Gson().fromJson(json, PressingRecipeJsonFormat.class);
            if (recipeJson.input == null) throw new JsonSyntaxException("Missing required input item");
            if (recipeJson.output == null) throw new JsonSyntaxException("Missing required output item");
            var input = Ingredient.fromJson(recipeJson.input);
            var output = ShapedRecipe.outputFromJson(recipeJson.output);

            return new PressingRecipe(id, input, output);
        }

        @Override
        public PressingRecipe read(Identifier id, PacketByteBuf buf) {
            var input = Ingredient.fromPacket(buf);
            var output = buf.readItemStack();

            return new PressingRecipe(id, input, output);
        }

        public void write(JsonObject json, PressingRecipe recipe) {
            var recipeJsonFormat = new PressingRecipeJsonFormat(
                    recipe.inputItem.toJson().getAsJsonObject(),
                    recipeJson(recipe)
            );
            var fields = new Gson().toJsonTree(recipeJsonFormat);
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : fields.getAsJsonObject().asMap().entrySet())
                json.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
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

        @Override
        public void write(PacketByteBuf buf, PressingRecipe recipe) {
            recipe.inputItem.write(buf);
            buf.writeItemStack(recipe.resultItem);
        }
    }

    public static class Input extends SimpleInventory {
        public Input() {
            super(1);
        }
    }

}
