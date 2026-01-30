package com.breakingfemme.recipe;

import com.breakingfemme.BreakingFemme;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

//https://www.youtube.com/watch?v=4N5BY2aHins
public class ModRecipes {
    public static void registerRecipes()
    {
        //grinding
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(BreakingFemme.MOD_ID, GrindingRecipe.Serializer.ID), GrindingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(BreakingFemme.MOD_ID, GrindingRecipe.Type.ID), GrindingRecipe.Type.INSTANCE);

        //fermenting
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(BreakingFemme.MOD_ID, FermentingRecipe.Serializer.ID), FermentingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(BreakingFemme.MOD_ID, FermentingRecipe.Type.ID), FermentingRecipe.Type.INSTANCE);
    }
}
