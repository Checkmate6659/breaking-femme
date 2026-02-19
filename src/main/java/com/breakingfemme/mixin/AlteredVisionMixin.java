package com.breakingfemme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.KineticsAttachments;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

@Mixin(GameRenderer.class)
public abstract class AlteredVisionMixin {
    @Invoker("disablePostProcessor") //TODO: doesn't actually need an invoker
	public abstract void invokeClearPostProcessor();

	@Invoker("loadPostProcessor")
	public abstract void invokeSetPostProcessor(Identifier id);

	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void applyAlteredVision(CallbackInfo info) {
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
			//FIXME: does this leak resources??
			if(((GameRenderer)(Object)this).getPostProcessor() == null) //we do not want invokeSetPostProcessor to be called again and again
			{
				//todo: set uniforms
				//shader tutorial (GLSL, jsons): https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/3201009-tutorial-vanilla-minecraft-shaders-creating-post
				//https://docs.google.com/document/d/15TOAOVLgSNEoHGzpNlkez5cryH3hFF3awXL5Py81EMk/edit?tab=t.0
				//invokeSetPostProcessor(Identifier.of("minecraft", "shaders/post/spider.json"));
				invokeSetPostProcessor(Identifier.of(BreakingFemme.MOD_ID, "shaders/post/altered_vision.json"));
			}
		}
		else
		{
			invokeClearPostProcessor();
		}
    }
}
