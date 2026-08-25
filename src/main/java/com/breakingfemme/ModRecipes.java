package com.breakingfemme;

import com.breakingfemme.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.breakingfemme.BreakingFemme.id;

//https://www.youtube.com/watch?v=4N5BY2aHins
public class ModRecipes {
    public static void registerRecipes()
    {
        //grinding
        Registry.register(Registries.RECIPE_SERIALIZER, id(GrindingRecipe.Serializer.ID), GrindingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(GrindingRecipe.Type.ID), GrindingRecipe.Type.INSTANCE);

        //fermenting
        Registry.register(Registries.RECIPE_SERIALIZER, id(FermentingRecipe.Serializer.ID), FermentingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(FermentingRecipe.Type.ID), FermentingRecipe.Type.INSTANCE);

        //distilling
        Registry.register(Registries.RECIPE_SERIALIZER, id(DistillingRecipe.Serializer.ID), DistillingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(DistillingRecipe.Type.ID), DistillingRecipe.Type.INSTANCE);

        //filtering
        Registry.register(Registries.RECIPE_SERIALIZER, id(FilteringRecipe.Serializer.ID), FilteringRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(FilteringRecipe.Type.ID), FilteringRecipe.Type.INSTANCE);

        //pressing
        Registry.register(Registries.RECIPE_SERIALIZER, id(PressingRecipe.Serializer.ID), PressingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(PressingRecipe.Type.ID), PressingRecipe.Type.INSTANCE);
    }
}
