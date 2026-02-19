package com.breakingfemme.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

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
		//TODO: simulate during sleep!!! as in simulate kinetics when skipping time

		//notation: https://en.wikipedia.org/wiki/Pharmacokinetics#Metrics

		//Ethanol and acetaldehyde kinetics
		//https://pmc.ncbi.nlm.nih.gov/articles/PMC12345593 in part 4
		float buf_etoh = player.getAttachedOrSet(KineticsAttachments.BUFFERED_ETHANOL, 0f);
		float etoh = player.getAttachedOrSet(KineticsAttachments.ETHANOL, 0f);
		float ach = player.getAttachedOrSet(KineticsAttachments.ACETALDEHYDE, 0f);

		//hunger is important for ethanol kinetics: an empty stomach makes absorption faster
		HungerManager manager = player.getHungerManager();
		float food = clampZero(manager.getFoodLevel() * 0.5f + manager.getSaturationLevel() - manager.getExhaustion() * 0.25f) * 0.03225806451612903f; //goes from 0 to 1, number is 1/31

		//ethanol absorption
		//TODO: 2 compartment Michaelis-Menten model ig?
		//TODO: check this out https://en.wikipedia.org/wiki/Pharmacology_of_ethanol#Pharmacokinetics

		//https://wires.onlinelibrary.wiley.com/doi/epdf/10.1002/wfs2.1340
		//this paper uses mg%: 1 mg% = 0.01g/L
		//%ABV -> %weight: multiply by 0.789g/mL (or kg/L)
		//Vd = 7e-4 (in L/g, for male body)
		//first order kinetics depending on hunger (real kinetics are quite unpredictable so i just came up with numbers)
		float char_time = 200f + 1000f * food; //characteristic absorption time should be 1200t when hunger = 1 and 200t when its 0, in ticks
		float absorbed_mass = buf_etoh / char_time; //mass absorbed in a tick, in grams (multiplying by 1 tick, ie doing nothing)
		buf_etoh -= absorbed_mass;
		etoh += absorbed_mass * 0.03; //after 3 beers (fed? fasted? in the middle?) it should peak at 1g/L, worked out with a driving blood alcohol content chart and trial and error, assuming 80kg, or 180lb because the chart is in lb

		//Michaelis-Menten kinetics for ethanol metabolism
		//https://pubmed.ncbi.nlm.nih.gov/7332732/
		//https://pmc.ncbi.nlm.nih.gov/articles/PMC3484320/ has a nice table btw, but different kinetic model
		//taking into account fed/fasted state (hunger bars): https://pmc.ncbi.nlm.nih.gov/articles/PMC1165727/?page=3
		//in rat liver cells metabolism is twice as fast in the fed state than the fasted state, we're gonna use that here
		float etoh_metabolism_rate = 1.2e-4f * (1f + food) * etoh / (etoh + 0.03f); //unit of this calculation: g/L/tick (1 hour = 1000 ticks, so we needed to divide by 1000), original was 1.2e-4 (assuming fasted state here)
		etoh -= etoh_metabolism_rate;
		ach += (etoh > 0f ? etoh_metabolism_rate : (etoh + etoh_metabolism_rate)) * 0.96f; //prevalence of oxidative metabolism of ethanol

		//Acetaldehyde metabolism
		//https://www.sciencedirect.com/science/article/pii/S1568786424001587
		//https://pmc.ncbi.nlm.nih.gov/articles/PMC8370625/: a very complete paper (34 ODEs?! wow that's a lot)
		//it even has its sim code on a gh: https://github.com/LMSE/HH-PBPK-Ethanol
		//but its way more complex than what we do here, it has all the organs separate from eachother. we don't do that here.
		//it seems to be using another Michaelis-Menten model for each organ tho, with possible transfers (I do not do transfers tho, only blood)
		//K_m from https://pmc.ncbi.nlm.nih.gov/articles/PMC3929114/: 0.2umol/L = 8.8ug/L
		//V_max from... my ass. TODO: find a proper source. that doesn't make all kinetics fucked up and not like the real ones when im trying to impl it.
		ach -= 6.0e-5 * ach / (ach + 8.8e-6); //using K_m = 8.8ug/L and V_max = 60 mg/L/h

		player.setAttached(KineticsAttachments.BUFFERED_ETHANOL, clampZero(buf_etoh));
		player.setAttached(KineticsAttachments.ETHANOL, clampZero(etoh));
		player.setAttached(KineticsAttachments.ACETALDEHYDE, clampZero(ach));


		//sync between client and server
		World world = player.getWorld();
		if(!world.isClient() && ((world.getTime() & 31) == 0)) //sync every 32 ticks, or 1.5 seconds, for efficiency/data usage
            KineticsAttachments.syncClientValues((ServerPlayerEntity)player);
	}
}
