package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.breakingfemme.BreakingFemme;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.util.Identifier;

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
        if(id.getNamespace().equals(BreakingFemme.MOD_ID)) //if using the only breakingfemme advancement tab (i won't add more than one tab)
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
        //TODO: grab the values for prng, for mossy/cracked bricks! and differentiating between them
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
            args.set(0, new Identifier(BreakingFemme.MOD_ID, "textures/gui/adv1.png"));
        else if(hash >= -77)
            args.set(0, new Identifier(BreakingFemme.MOD_ID, "textures/gui/adv2.png"));
        else
            args.set(0, new Identifier(BreakingFemme.MOD_ID, "textures/gui/adv3.png"));
    }

    //TODO: draw graffiti on the bricks
}
