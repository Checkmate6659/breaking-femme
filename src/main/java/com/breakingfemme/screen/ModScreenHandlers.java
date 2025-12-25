package com.breakingfemme.screen;

import com.breakingfemme.BreakingFemme;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<FermenterScreenHandler> FERMENTER_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(BreakingFemme.MOD_ID, "fermenting"),
        new ExtendedScreenHandlerType<>(FermenterScreenHandler::new));
    
    public static void registerScreenHandlers()
    {
        //
    }
}
