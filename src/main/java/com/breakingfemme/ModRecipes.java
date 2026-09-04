package com.breakingfemme;

import com.breakingfemme.recipe.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.breakingfemme.BreakingFemme.id;

//https://www.youtube.com/watch?v=4N5BY2aHins
public class ModRecipes {
    public static final ModRecipe<GrindingRecipe, GrindingRecipe.Type, GrindingRecipe.Serializer>
            GRINDING_RECIPE = registerRecipe(
            GrindingRecipe.Type.INSTANCE,
            GrindingRecipe.Serializer.INSTANCE
    );
    public static final ModRecipe<FermentingRecipe, FermentingRecipe.Type, FermentingRecipe.Serializer>
            FERMENTING_RECIPE = registerRecipe(
            FermentingRecipe.Type.INSTANCE,
            FermentingRecipe.Serializer.INSTANCE
    );
    public static final ModRecipe<DistillingRecipe, DistillingRecipe.Type, DistillingRecipe.Serializer>
            DISTILLING_RECIPE = registerRecipe(
            DistillingRecipe.Type.INSTANCE,
            DistillingRecipe.Serializer.INSTANCE
    );
    public static final ModRecipe<FilteringRecipe, FilteringRecipe.Type, FilteringRecipe.Serializer>
            FILTERING_RECIPE = registerRecipe(
            FilteringRecipe.Type.INSTANCE,
            FilteringRecipe.Serializer.INSTANCE
    );
    public static final ModRecipe<PressingRecipe, PressingRecipe.Type, PressingRecipe.Serializer>
            PRESSING_RECIPE = registerRecipe(
            PressingRecipe.Type.INSTANCE,
            PressingRecipe.Serializer.INSTANCE
    );

    public record ModRecipe<R extends Recipe<?>, T extends RecipeType<R> & IIdentifiableSubtype, S extends RecipeSerializer<R> & IIdentifiableSubtype>(
            S serializer, T type) {
    }

    private static <R extends Recipe<?>, T extends RecipeType<R> & IIdentifiableSubtype, S extends RecipeSerializer<R> & IIdentifiableSubtype>
    ModRecipe<R, T, S> registerRecipe(T type, S serializer) {
        return new ModRecipe<>(
                Registry.register(Registries.RECIPE_SERIALIZER, id(serializer.getId()), serializer),
                Registry.register(Registries.RECIPE_TYPE, id(type.getId()), type)
        );
    }

    public static void registerRecipes() {
    }
}
