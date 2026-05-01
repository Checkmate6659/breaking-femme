package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.book.gui.GuiBookLanding;

//https://github.com/VazkiiMods/Patchouli/blob/1.21.x/Xplat/src/main/java/vazkii/patchouli/client/book/gui/GuiBookLanding.java
@Mixin(GuiBookLanding.class)
public class GuideBookMixin {
    @ModifyConstant(method = "drawHeader", constant = @Constant(intValue = 31))
    private int breakingfemme$largerHeader(int value)
    {
        if(((GuiBookLanding)(Object)this).book.name.contentEquals("book.breakingfemme.name"))
            return 37;
        return value;
    }

    @Inject(method = "drawHeader", at = @At(value = "INVOKE", target = "drawText"), cancellable = true)
    private void breakingfemme$hijack(CallbackInfo ci)
    {
        if(((GuiBookLanding)(Object)this).book.name.contentEquals("book.breakingfemme.name"))
            ci.cancel();
    }
}
