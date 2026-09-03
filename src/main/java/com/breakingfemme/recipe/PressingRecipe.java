package com.breakingfemme.recipe;

import com.breakingfemme.registries.press.PressHead;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Ingredient getInputItem() {
        return inputItem;
    }

    @Override
    public Serializer getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(Input inventory, World world) {
        if (inventory.isEmpty()) return false;
        var head = inventory.getHead();
        if (head.isEmpty()) return false;
        inputItem.test(inventory.ingredientStack);
        headIngredient.test(head.get());
        return false;
    }

    @Override
    public ItemStack craft(Input inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 1 && height == 2;
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

    public static class Type implements RecipeType<PressingRecipe>, IIdentifiableSubtype {
        private Type() {
        }

        public static Type INSTANCE = new Type();

        public static final String ID = "pressing";

        @Override
        public String getId() {
            return ID;
        }
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe>, IIdentifiableSubtype {
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

        @Override
        public String getId() {
            return ID;
        }

        private record PressingRecipeJsonFormat(
                JsonObject input,
                JsonObject head,
                JsonObject output
        ) {
        }

    }

    public static class Input implements Inventory, RecipeInputInventory {
        public Input(@NotNull ItemStack headStack, @NotNull ItemStack ingredientStack) {
            this.headStack = headStack;
            this.ingredientStack = ingredientStack;
        }

        public Input() {
        }

        private @NotNull ItemStack headStack = ItemStack.EMPTY;
        private @NotNull ItemStack ingredientStack = ItemStack.EMPTY;

        private ItemStack getSlotStack(int slot) {
            return switch (slot) {
                case 1 -> headStack;
                case 0 -> ingredientStack;
                default -> throw new ArrayIndexOutOfBoundsException();
            };
        }

        private void setSlotStack(@NotNull ItemStack stack, int slot) {
            switch (slot) {
                case 1 -> this.headStack = stack;
                case 0 -> this.ingredientStack = stack;
                default -> throw new ArrayIndexOutOfBoundsException();
            }
        }

        public boolean isValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 1 -> PressHead.isPressHead(stack);
                case 0 -> true;
                default -> throw new ArrayIndexOutOfBoundsException();
            };
        }

        public @NotNull ItemStack getHeadStack() {
            return headStack.copy();
        }

        public Optional<PressHead> getHead() {
            if (this.headStack.isEmpty()) {
                return Optional.empty();
            }
            return PressHead.getPressHead(this.getHeadStack());
        }

        public @NotNull ItemStack getIngredientStack() {
            return ingredientStack.copy();
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return headStack.isEmpty() && ingredientStack.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return getSlotStack(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            assert amount >= 0;
            var stack = getSlotStack(slot);
            if (stack.isEmpty() || amount == 0) return ItemStack.EMPTY;

            int amountToRemove = Math.min(amount, stack.getCount());
            if (amountToRemove <= 0) return ItemStack.EMPTY;

            int newCount = stack.getCount() - amountToRemove;

            if (newCount == 0) setSlotStack(ItemStack.EMPTY, slot);
            else setSlotStack(stack.copyWithCount(newCount), slot);

            return stack.copyWithCount(amountToRemove);
        }

        @Override
        public ItemStack removeStack(int slot) {
            var stack = getSlotStack(slot);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                setSlotStack(stack, slot);
                return stack.copy();
            }
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
        }

        @Override
        public int getMaxCountPerStack() {
            return Inventory.MAX_COUNT_PER_STACK;
        }

        @Override
        public void markDirty() {/*not needed */}

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return false;
        }

        @Override
        public void clear() {
            setSlotStack(ItemStack.EMPTY, 0);
            setSlotStack(ItemStack.EMPTY, 1);
        }

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 2;
        }

        @Override
        public List<ItemStack> getInputStacks() {
            return List.of(ingredientStack, headStack);
        }

        @Override
        public void provideRecipeInputs(RecipeMatcher finder) {
            finder.addInput(ingredientStack);
            finder.addInput(headStack);
        }
    }

}
