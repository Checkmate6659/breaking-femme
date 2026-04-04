package com.breakingfemme.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;

//https://www.youtube.com/watch?v=HbZ6ocABo-M
//3:26
public class BreakingFemmeREICompatPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry)
    {
        REIClientPlugin.super.registerCategories(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry)
    {
        REIClientPlugin.super.registerDisplays(registry);
    }

    @Override
    public void registerScreens(ScreenRegistry registry)
    {
        REIClientPlugin.super.registerScreens(registry);
    }
}
