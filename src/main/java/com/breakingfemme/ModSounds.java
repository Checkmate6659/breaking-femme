package com.breakingfemme;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    private static SoundEvent registerSound(String id) {
		Identifier identifier = Identifier.of(BreakingFemme.MOD_ID, id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static final SoundEvent GRINDING = registerSound("grinding");
	public static final SoundEvent CRANK = registerSound("crank");

	public static void registerSounds() {
        //
	}
}
