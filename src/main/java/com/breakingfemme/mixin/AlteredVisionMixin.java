package com.breakingfemme.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.KineticsAttachments;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public abstract class AlteredVisionMixin {
    @Invoker("disablePostProcessor") //TODO: doesn't actually need an invoker
	public abstract void breakingfemme_invokeClearPostProcessor();

	@Invoker("loadPostProcessor")
	public abstract void breakingfemme_invokeSetPostProcessor(Identifier id);

	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void breakingfemme_applyAlteredVision(CallbackInfo info) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;

		if(player.isSpectator()) //don't do shit in spectator mode (TODO: clear the effect right after switching)
			return;

		float etoh = KineticsAttachments.getLevel(player, KineticsAttachments.ETHANOL);
        //float ach = KineticsAttachments.getLevel(player, KineticsAttachments.ACETALDEHYDE);

		if((player.getWorld().getTime() & 15) != 0)
			return;

		if(etoh > 1.25f)
		{
			//get proper name with this? if its null then ofc its not bound.
			//((GameRenderer)(Object)this).getPostProcessor().getName()

			//FIXME: does this leak resources??
			if(((GameRenderer)(Object)this).getPostProcessor() == null) //we do not want invokeSetPostProcessor to be called again and again
			{
				//TODO: set uniforms: PostEffectPass, line 73
				//shader tutorial (GLSL, jsons): https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/3201009-tutorial-vanilla-minecraft-shaders-creating-post
				//https://docs.google.com/document/d/15TOAOVLgSNEoHGzpNlkez5cryH3hFF3awXL5Py81EMk/edit?tab=t.0
				//invokeSetPostProcessor(Identifier.of("minecraft", "shaders/post/spider.json"));
				breakingfemme_invokeSetPostProcessor(Identifier.of(BreakingFemme.MOD_ID, "shaders/post/altered_vision.json"));

				//set uniforms
				List<PostEffectPass> passes = ((PostEffectPassAccessor)(((GameRenderer)(Object)this).getPostProcessor())).breakingfemme$getPasses();

				long time = player.getWorld().getTime();
				passes.get(1).getProgram().getUniformByNameOrDummy("Radius").set(30f);
				passes.get(2).getProgram().getUniformByNameOrDummy("Radius").set(30f - (time % 3000) / 100f); //using time turns it into a black screen?
			}
		}
		else
		{
			breakingfemme_invokeClearPostProcessor();
		}
    }
}
