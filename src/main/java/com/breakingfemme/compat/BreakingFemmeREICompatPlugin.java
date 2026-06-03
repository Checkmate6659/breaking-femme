package com.breakingfemme.compat;

import com.breakingfemme.block.ModBlocks;
import com.breakingfemme.item.ModItems;
import com.breakingfemme.recipe.FilteringRecipe;
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

        registry.add(new FilteringCategory());
        registry.addWorkstations(FilteringCategory.FILTERING, EntryStacks.of(ModBlocks.FUNNEL));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        registry.registerRecipeFiller(GrindingRecipe.class, GrindingRecipe.Type.INSTANCE, GrindingDisplay::new);
        registry.registerRecipeFiller(FilteringRecipe.class, FilteringRecipe.Type.INSTANCE, FilteringDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry)
    {
        //using the fermenter screen class because it poses no problems for when I don't want to make a custom screen class for things that do not have a screen.
        //i mean... it displays properly, but... the dragging to bookmark is off??? like its not the right draggable area or the right aspect ratio
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), FermenterScreen.class, GrindingCategory.GRINDING);
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), FermenterScreen.class, FilteringCategory.FILTERING);
    }
}
