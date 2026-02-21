package com.breakingfemme.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.KineticsAttachments;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public abstract class AlteredVisionMixin {
	@Unique
	private static final Identifier ALTERED_VISION_ID = new Identifier("shaders/post/altered_vision.json");

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

		if(etoh > 1.25f)
		{
			//get proper name with this? if its null then ofc its not bound.
			//((GameRenderer)(Object)this).getPostProcessor().getName()

			if((player.getWorld().getTime() & 1) == 0 && ((GameRenderer)(Object)this).getPostProcessor() == null) //we do not want invokeSetPostProcessor to be called every frame, that breaks the game
			{
				//shader tutorial (GLSL, jsons): https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/3201009-tutorial-vanilla-minecraft-shaders-creating-post
				//https://docs.google.com/document/d/15TOAOVLgSNEoHGzpNlkez5cryH3hFF3awXL5Py81EMk/edit?tab=t.0
				//fix for 1.20.1 shader loading bug
				//https://discord.com/channels/507304429255393322/807617488313516032/1474509569739723015
				//has some code that actually works
				//invokeSetPostProcessor(Identifier.of("minecraft", "shaders/post/spider.json"));
				breakingfemme_invokeSetPostProcessor(ALTERED_VISION_ID);
			}

			//however we do want to set the uniforms often
			//why do they only get set every once in a while?
			if(((GameRenderer)(Object)this).getPostProcessor() != null && ((GameRenderer)(Object)this).getPostProcessor().getName().equals(ALTERED_VISION_ID.toString())) //triggers after getting set to not be null => cant use if else
			{
				float blur_strength = (etoh - 1.25f) * 0.5f; //goes between 0 and 1: more drunk => see worse
				if(blur_strength > 1.0f) blur_strength = 1.0f;
				
				//set uniforms
				List<PostEffectPass> passes = ((PostEffectPassAccessor)(((GameRenderer)(Object)this).getPostProcessor())).breakingfemme$getPasses();

				//TODO: fix the issue that the blur shaders actually change the brightness when given a small, non-integer radius
				passes.get(1).getProgram().getUniformByNameOrDummy("Radius").set(64f * blur_strength);
				passes.get(2).getProgram().getUniformByNameOrDummy("Radius").set(16f * blur_strength); //more horizontal blur => primitive diplopia emulation (if can't use custom shaders)
			}
		}
		else if((player.getWorld().getTime() & 1) == 0)
		{
			breakingfemme_invokeClearPostProcessor();
		}
    }
}
