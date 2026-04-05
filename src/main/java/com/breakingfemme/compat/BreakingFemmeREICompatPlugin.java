package com.breakingfemme.compat;

import com.breakingfemme.item.ModItems;
import com.breakingfemme.recipe.GrindingRecipe;
import com.breakingfemme.screen.FermenterScreen;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class BreakingFemmeREICompatPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        registry.add(new GrindingCategory());
        registry.addWorkstations(GrindingCategory.GRINDING, EntryStacks.of(ModItems.MORTAR_PESTLE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.registerRecipeFiller(GrindingRecipe.class, GrindingRecipe.Type.INSTANCE, GrindingDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry)
    {
        //TODO: make a custom screen for the grinding, just for the REI compatibility! ... if we need it ig
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), FermenterScreen.class, GrindingCategory.GRINDING);
    }
}
