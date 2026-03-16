package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;

import vazkii.patchouli.client.book.gui.GuiBookLanding;
import net.minecraft.util.Identifier;

//https://github.com/VazkiiMods/Patchouli/blob/1.21.x/Xplat/src/main/java/vazkii/patchouli/client/book/gui/GuiBookLanding.java
@Mixin(GuiBookLanding.class)
public class GuideBookMixin {
    @Inject(method = "drawHeader", at = @At(value = "INVOKE", target = "drawText"), cancellable = true)
    private void breakingfemme$hijack(CallbackInfo ci)
    {
        if(((GuiBookLanding)(Object)this).book.name.contentEquals("book.breakingfemme.name"))
            ci.cancel();
    }
}
