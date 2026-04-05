package com.breakingfemme.compat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.breakingfemme.recipe.GrindingRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.Identifier;

public class GrindingDisplay extends BasicDisplay {
    public GrindingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> identifier)
    {
        super(inputs, outputs, identifier);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return GrindingCategory.GRINDING;
    }

    public GrindingDisplay(GrindingRecipe recipe)
    {
        super(List.of(EntryIngredients.ofIngredient(recipe.getInputIngredient())), List.of(EntryIngredients.of(recipe.getOutputItem())));
    }
}
