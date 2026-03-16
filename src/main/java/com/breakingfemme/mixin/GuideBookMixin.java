package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;

import vazkii.patchouli.client.book.gui.GuiBookLanding;
import net.minecraft.util.Identifier;

@Mixin(GuiBookLanding.class)
public class GuideBookMixin {
    @Inject(method = "drawHeader", at = @At(value = "HEAD", target = "drawString"), cancellable = true)
    private void breakingfemme$hijack(CallbackInfo ci)
    {
        String name = ((GuiBookLanding)(Object)this).book.name;
        BreakingFemme.LOGGER.error("HIJACK? " + name);
        if(name.contentEquals("book.breakingfemme.name"))
        {
            BreakingFemme.LOGGER.error("GOTCHA");
            ci.cancel();
        }
    }
}
