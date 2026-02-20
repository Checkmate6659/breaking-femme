package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.breakingfemme.BreakingFemme;

import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.util.Identifier;

//JsonEffectShaderProgram is made in a kinda modding-unfriendly way: it assumes the shaders are in assets/minecraft
//so i need to hijack the method to redirect the loaded path... but only if loading MY shaders. not anyone else's/minecraft's.
//this is kinda dirty. but i cant really find a better way
@Mixin(JsonEffectShaderProgram.class)
public class JsonEffectShaderProgramMixin {
    @Unique
    private static final String TARGET = "breakingfemme_";

    private static Identifier breakingfemme_hijack(Identifier id)
    {
        String path = id.getPath();
        if(path.contains(TARGET))
        {
            //path = path.split(TARGET)[1];
            //BreakingFemme.LOGGER.info("Redirecting shader load " + path + " to breakingfemme:shaders/program/" + path);
            BreakingFemme.LOGGER.info("Redirecting shader load " + path + " to breakingfemme:" + path);

            //extract stuff AFTER the marker, use that as the name
            //return Identifier.of(BreakingFemme.MOD_ID, "shaders/program/" + path);

            //no. just return the whole thing. to avoid black screen/cant find uniform issue.
            return Identifier.of(BreakingFemme.MOD_ID, path);
        }
        else
            return id;
    }

    @ModifyVariable(method = "<init>", at = @At("STORE"), ordinal = 0)
    private Identifier breakingfemme_hijackConstructor(Identifier id)
    {
        return breakingfemme_hijack(id);
    }

    @ModifyVariable(method = "loadEffect", at = @At("STORE"), ordinal = 0)
    private static Identifier breakingfemme_hijackLoadEffect(Identifier id)
    {
        id = breakingfemme_hijack(id);
        BreakingFemme.LOGGER.error("Loading effect " + id.toString());
        return id;
    }
}
