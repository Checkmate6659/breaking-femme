package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.breakingfemme.BreakingFemme;

import vazkii.patchouli.client.book.gui.GuiBookLanding;
import net.minecraft.util.Identifier;

@Mixin(GuiBookLanding.class)
public class GuideBookMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "translatable"))
    private static String breakingfemme$hijack(String original)
    {
        BreakingFemme.LOGGER.error("HIJACK? " + original);
        //fuck, this hits book.breakingfemme.landing instead. dammit.
        if(original.contentEquals("book.breakingfemme.name"))
        {
            BreakingFemme.LOGGER.error("GOTCHA");
            return "EEEEE"; //show no name
        }
        return original;
    }
}
