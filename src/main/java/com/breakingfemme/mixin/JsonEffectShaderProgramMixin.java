package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
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
    private static final String TARGET = "breakingfemme_87yde7an4a3l9bbd_breakingfemme_";

    private static Identifier hijack(Identifier id)
    {
        String path = id.getPath();
        if(path.contains(TARGET))
        {
            path = path.split(TARGET)[1];
            BreakingFemme.LOGGER.info("Redirecting shader load " + path + " to breakingfemme:shaders/program/" + path);

            //extract stuff AFTER the marker, use that as the name
            return Identifier.of(BreakingFemme.MOD_ID, "shaders/program/" + path);
        }
        else
            return id;
    }

    @ModifyVariable(method = "<init>", at = @At("STORE"), ordinal = 0)
    private Identifier hijackConstructor(Identifier id)
    {
        return hijack(id);
    }

    @ModifyVariable(method = "loadEffect", at = @At("STORE"), ordinal = 0)
    private static Identifier hijackLoadEffect(Identifier id)
    {
        return hijack(id);
    }
}
