package com.breakingfemme.compat;

import java.util.List;
import java.util.Optional;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.datagen.ModFluidTagProvider;
import com.breakingfemme.datagen.ModItemTagProvider;
import com.breakingfemme.recipe.FilteringRecipe;

import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.Identifier;

public class FilteringDisplay extends BasicDisplay {
    public final boolean harsh;
    public FilteringDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> identifier)
    {
        super(inputs, outputs, identifier);

        boolean harsh_future = false; //avoiding final variable problems
        try{ //yeah this is kinda fucked up as well
            @SuppressWarnings("unchecked")
            EntryStack<FluidStack> influid = (EntryStack<FluidStack>) inputs.get(0).get(0);
            harsh_future = influid.getValue().getFluid().isIn(ModFluidTagProvider.HARSH_ON_FILTERS);
        } catch (ClassCastException e)
        {
            BreakingFemme.LOGGER.error("Invalid Recipe passed onto FilteringDisplay constructor: FilteringDisplay encountered a first ingredient which is not a fluid.");
        }

        harsh = harsh_future;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return FilteringCategory.FILTERING;
    }

    public FilteringDisplay(FilteringRecipe recipe)
    {
        //doing my best to keep it readable, despite no flexible constructor bodies
        //calculating the gcd twice and dividing even when it is 1 kind of mentally damages me, but stupid java just can't cope
        //even if i deferred the two arguments of super into separate static functions, i still need to do caching of those values etc if i want to get anything meaningful out of it
        //this *should* only be called when opening up rei tho... hopefully its gonna be fine
        super(List.of(
            EntryIngredients.of(recipe.input.getFluid(), recipe.inputq / BreakingFemme.gcd(recipe.inputq, recipe.outputq)),
            EntryIngredients.ofItemTag(
                recipe.input.getFluid().isIn(ModFluidTagProvider.HARSH_ON_FILTERS) ?
                    ModItemTagProvider.RESISTANT_FILTER :
                    ModItemTagProvider.FILTER
                )
        ), List.of(
            EntryIngredients.of(recipe.output.getFluid(), recipe.outputq / BreakingFemme.gcd(recipe.inputq, recipe.outputq)),
            EntryIngredients.of(recipe.item_output)
        ));

        //tf I NEED TO DO THIS AFTER THE SUPER SO I NEED TO RECHECK
        harsh = recipe.input.getFluid().isIn(ModFluidTagProvider.HARSH_ON_FILTERS);
    }
}
