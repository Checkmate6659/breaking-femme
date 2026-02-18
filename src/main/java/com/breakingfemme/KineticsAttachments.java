package com.breakingfemme;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class KineticsAttachments {

	//attachments for levels of different chemicals
	static final float MAX_FLOAT = 3.4028235e38f;
	public static final AttachmentType<Float> BUFFERED_ETHANOL = AttachmentRegistry.createPersistent( //unit: g
		Identifier.of(BreakingFemme.MOD_ID, "level_buffered_ethanol"), Codec.floatRange(0f, MAX_FLOAT));
	//public static final AttachmentType<Float> ETHANOL = AttachmentRegistry.createPersistent( //unit: g/L in blood
	//	Identifier.of(BreakingFemme.MOD_ID, "level_ethanol"), Codec.floatRange(0f, MAX_FLOAT));
	public static final AttachmentType<Float> ETHANOL = AttachmentRegistry.<Float>builder() //unit: g/L in blood
		.persistent(Codec.floatRange(0f, MAX_FLOAT))
		.buildAndRegister(Identifier.of(BreakingFemme.MOD_ID, "level_ethanol"));
	public static final AttachmentType<Float> ACETALDEHYDE = AttachmentRegistry.createPersistent( //unit: g/L in blood
		Identifier.of(BreakingFemme.MOD_ID, "level_acetaldehyde"), Codec.floatRange(0f, MAX_FLOAT));

	//TODO (in other mixins): apply effects of ethanol
	//https://www.youtube.com/watch?v=z9QqEf93sBY
	//0.3-0.5 g/L: disinhibition, more talkative, mild euphoria
	//0.8-1.2 g/L: slower reaction time, beginning of staggering and slurred speech
	//2 g/L: gross intoxication and incapacitation
	//3 g/L: great risk of death
	//apply effects of acetaldehyde
	//https://pmc.ncbi.nlm.nih.gov/articles/PMC6527032/
	//apparently some effects are the same as for ethanol

	//https://github.com/Petrolpark-Mods/Destroy/blob/1.20.1/src/main/java/com/petrolpark/destroy/content/product/alcohol/HangoverMobEffect.java
	//hangover effect implementation here: player is 10% slower and gets hurt when a loud enough sound is heard, by headache

	public static float getLevel(PlayerEntity player, AttachmentType<Float> att)
	{
		return player.getAttachedOrElse(att, 0f);
	}

	public static void setLevel(PlayerEntity player, AttachmentType<Float> att, float value)
	{
		value = (value < 0f) ? 0f : value; //clamp to be at least zero
		value = (value > MAX_FLOAT) ? MAX_FLOAT : value; //clamp to be at most MAX_FLOAT
		player.setAttached(att, value);
	}

	public static void incLevel(PlayerEntity player, AttachmentType<Float> att, float value)
	{
		float cur_value = player.getAttachedOrElse(att, 0f);
		cur_value += value; //add value
		cur_value = (cur_value < 0f) ? 0f : cur_value; //clamp to be at least zero
		cur_value = (cur_value > MAX_FLOAT) ? MAX_FLOAT : cur_value; //clamp to be at most MAX_FLOAT
		player.setAttached(att, cur_value);
	}

    public static void registerAttachments()
    {
        //
    }
}
