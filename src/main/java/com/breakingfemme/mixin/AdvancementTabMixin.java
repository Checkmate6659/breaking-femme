package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.breakingfemme.BreakingFemme;

import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.util.Identifier;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {
    private boolean breakingfemme$large_texture = false;

    @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0))
    private Identifier breakingfemme$checkIfLargeTexture(Identifier id)
    {
        breakingfemme$large_texture = false;
        if(id.getNamespace().equals(BreakingFemme.MOD_ID)) //if using the only breakingfemme advancement tab (i won't add more than one tab)
            breakingfemme$large_texture = true;
        return id;
    }

    //TODO: more elegant way to do this? this is kinda scuffed, changes every single constant of value 16.
    //how does this work with other mods?
    @ModifyConstant(method = "render", constant = @Constant(intValue = 16))
    private int breakingfemme$change16s(int value)
    {
        if(breakingfemme$large_texture) return 32; //size of my background texture
        return value;
    }
}
