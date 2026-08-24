package com.breakingfemme.client.mixin;

import com.breakingfemme.BreakingFemme;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.breakingfemme.BreakingFemme.id;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {
    @Unique
    private boolean breakingfemme$target_tab = false;

    @Unique //WARNING: wonky and probably not very well compatible code!! TODO: rewrite if needed!
    private int breakingfemme$copy_i = -32768, breakingfemme$copy_j = -32768;

    //this is checking if the background image has the breakingfemme namespace
    //mb a finer check could be better. like checking the actual path.
    @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0))
    private Identifier breakingfemme$checkIfTargetTab(Identifier id)
    {
        breakingfemme$target_tab = false;
        if(id.getNamespace().equals(BreakingFemme.MOD_ID) && id.getPath().equals("custom_background")) //if custom breakingfemme background selected
        {
            breakingfemme$target_tab = true;

            //reset i varable. which is responsible for recognizing if we are targeting i or j in the next mixin.
            breakingfemme$copy_i = -32768;
        }
        return id;
    }

    //add parallax effect, and "steal" i and j values for later use
    @Expression("@(?)%16")
    @ModifyExpressionValue(method = "render", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private int breakingfemme$parallax(int value)
    {
        if(breakingfemme$target_tab)
        {
            value /= 2; //move around slower
            value -= 4; //slight offset, not to have a perfect corner lineup
            if(breakingfemme$copy_i == -32768) //detection of if we are targeting i or j
                breakingfemme$copy_i = value;
            else
            {
                value -= 9; //same shit here
                breakingfemme$copy_j = value;
            }
        }
        return value;
    }

    //change the textures to be randomized bricks
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void breakingfemme$bricks(Args args)
    {
        if(!breakingfemme$target_tab) return; //don't do anything

        //select brick type
        int x = (breakingfemme$copy_i & -16) - ((int)args.get(1) & -16) + 1024;
        int y = (breakingfemme$copy_j & -16) - ((int)args.get(2) & -16) + 1024;

        int val = (x >> 4) * 80085 + (y >> 4) * 6659 + 676767;
        byte hash = (byte)((val * val * val) >> 20); //wait, theres patches of moss or no moss? better hash function? and why is that?

        //approximately 50% stone bricks, 30% mossy stone bricks, 20% cracked stone bricks
        if(hash >= 0)
            args.set(0, id("textures/gui/advancement/brick1.png"));
        else if(hash >= -77)
            args.set(0, id("textures/gui/advancement/brick2.png"));
        else
            args.set(0, id("textures/gui/advancement/brick3.png"));
    }

    //draw graffiti on the bricks, right after bricks ie right before drawing the actual advancements
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/advancement/AdvancementWidget;renderLines(Lnet/minecraft/client/gui/DrawContext;IIZ)V"))
    private void breakingfemme$draw_graffiti(DrawContext context, int x, int y, CallbackInfo info)
    {
        //NOTE: this does NOT support transparency!
        context.drawTexture(id("textures/gui/advancement/graffiti.png"),
            breakingfemme$copy_i, breakingfemme$copy_j, 0, 0, 256, 176);
    }
}
