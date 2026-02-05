package com.breakingfemme.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.breakingfemme.KineticsAttachments;

@Mixin(PlayerEntity.class)
public class KineticsMixin {
	private float clampZero(float f)
	{
		if(f > 0f) return f;
		return 0f;
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void updateLevels(CallbackInfo info) {
		PlayerEntity player = ((PlayerEntity)(Object)this);

		//Ethanol and acetaldehyde kinetics
		//https://pmc.ncbi.nlm.nih.gov/articles/PMC12345593 in part 4
		float buf_etoh = player.getAttachedOrSet(KineticsAttachments.BUFFERED_ETHANOL, 0f);
		float etoh = player.getAttachedOrSet(KineticsAttachments.ETHANOL, 0f);
		float ach = player.getAttachedOrSet(KineticsAttachments.ACETALDEHYDE, 0f);

		//ethanol absorption
		//TODO: 2 compartment Michaelis-Menten model ig?
		//TODO: check this out https://en.wikipedia.org/wiki/Pharmacology_of_ethanol#Pharmacokinetics

		//Michaelis-Menten kinetics for ethanol metabolism
		//https://pubmed.ncbi.nlm.nih.gov/7332732/
		//https://pmc.ncbi.nlm.nih.gov/articles/PMC3484320/ has a nice table btw, but different kinetic model
		//TODO: take into account fed/fasted state (hunger bars)
		float etoh_metabolism_rate = 0.12f / (etoh + 0.03f) //unit of this calculation: g/L/h
		    * 0.001f; //1 hour = 10^3 ticks; this means metabolism_rate is in g/L/tick now
		etoh -= etoh_metabolism_rate;
		ach += (etoh > 0f ? etoh_metabolism_rate : (etoh + etoh_metabolism_rate)) * 0.96f; //prevalence of oxidative metabolism of ethanol

		//Acetaldehyde metabolism
		//https://www.sciencedirect.com/science/article/pii/S1568786424001587
		ach -= 0.01f; //TODO: proper number.

		player.setAttached(KineticsAttachments.BUFFERED_ETHANOL, clampZero(buf_etoh));
		player.setAttached(KineticsAttachments.ETHANOL, clampZero(etoh));
		player.setAttached(KineticsAttachments.ACETALDEHYDE, clampZero(ach));
	}
}
