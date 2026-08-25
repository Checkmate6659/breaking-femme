package com.breakingfemme.recipe.datagen;

import com.breakingfemme.recipe.PressingRecipe;
import com.breakingfemme.registries.press.PressHead;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PressingRecipeJsonBuilder extends RecipeJsonBuilder implements CraftingRecipeJsonBuilder {
    private final RecipeCategory category;
    private final ItemStack output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.createUntelemetered();
    @Nullable
    private String group;
    private Ingredient input = null;
    private PressingRecipe.PressHeadIngredient headInput = null;

    public PressingRecipeJsonBuilder(RecipeCategory category, ItemStack stack) {
        this.output = stack.copy();
        this.category = category;
    }

    public PressingRecipeJsonBuilder input(TagKey<Item> item) {
        return this.input(Ingredient.fromTag(item));
    }

    public PressingRecipeJsonBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public PressingRecipeJsonBuilder input(ItemConvertible... items) {
        this.input = Ingredient.ofItems(items);
        return this;
    }

    public PressingRecipeJsonBuilder head(TagKey<PressHead> head) {
        return this.head(new PressingRecipe.PressHeadIngredient(null, head));
    }

    public PressingRecipeJsonBuilder head(PressingRecipe.PressHeadIngredient ingredient) {
        this.headInput = ingredient;
        return this;
    }

    public PressingRecipeJsonBuilder head(PressHead head) {
        return this.head(new PressingRecipe.PressHeadIngredient(head, null));
    }

    public PressingRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
        this.advancementBuilder.criterion(string, criterionConditions);
        return this;
    }

    public PressingRecipeJsonBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output.getItem();
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancementBuilder
                .parent(ROOT)
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(CriterionMerger.OR);
        exporter.accept(
                new PressingRecipeJsonProvider(
                        getCraftingCategory(this.category),
                        recipeId,
                        this.group == null ? "" : this.group,
                        this.output,
                        this.input,
                        this.headInput,
                        this.advancementBuilder,
                        recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")
                )
        );
    }

    private void validate(Identifier recipeId) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static class PressingRecipeJsonProvider extends RecipeJsonBuilder.CraftingRecipeJsonProvider {
        private final Identifier recipeId;
        private final String group;
        private final ItemStack output;
        private final Ingredient input;
        private final PressingRecipe.PressHeadIngredient headInput;
        private final Advancement.Builder advancementBuilder;
        private final Identifier advancementId;

        protected PressingRecipeJsonProvider(CraftingRecipeCategory craftingCategory, Identifier recipeId, String group, ItemStack output, Ingredient input, PressingRecipe.PressHeadIngredient headInput, Advancement.Builder advancementBuilder, Identifier advancementId) {
            super(craftingCategory);
            this.recipeId = recipeId;
            this.group = group;
            this.output = output;
            this.input = input;
            this.headInput = headInput;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            var recipe = new PressingRecipe(recipeId, input, output, headInput);
            recipe.getSerializer().write(json, recipe);
            // yippe!
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return PressingRecipe.Serializer.INSTANCE;
        }

        @Override
        public @Nullable JsonObject toAdvancementJson() {
            return this.advancementBuilder.toJson();
        }

        @Override
        public @Nullable Identifier getAdvancementId() {
            return this.advancementId;
        }
    }

}
