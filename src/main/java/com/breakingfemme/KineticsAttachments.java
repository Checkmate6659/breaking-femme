package com.breakingfemme;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class KineticsAttachments {

	//attachments for levels of different chemicals
	static final float MAX_FLOAT = 3.4028235e38f;
	public static final AttachmentType<Float> BUFFERED_ETHANOL = AttachmentRegistry.createPersistent( //unit: g
		Identifier.of(BreakingFemme.MOD_ID, "level_buffered_ethanol"), Codec.floatRange(0f, MAX_FLOAT));
	public static final AttachmentType<Float> ETHANOL = AttachmentRegistry.createPersistent( //unit: g/L in blood
		Identifier.of(BreakingFemme.MOD_ID, "level_ethanol"), Codec.floatRange(0f, MAX_FLOAT));
	public static final AttachmentType<Float> ACETALDEHYDE = AttachmentRegistry.createPersistent( //unit: g/L in blood
		Identifier.of(BreakingFemme.MOD_ID, "level_acetaldehyde"), Codec.floatRange(0f, MAX_FLOAT));

    public static void registerAttachments()
    {
        //
    }
}
