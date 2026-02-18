package com.breakingfemme.networking;

import com.breakingfemme.BreakingFemme;
import com.breakingfemme.KineticsAttachments;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier KINETICS_SYNC_ID = new Identifier(BreakingFemme.MOD_ID, "kinetics_sync");

    public static void registerC2SPackets()
    {
        //
    }

    public static void registerS2CPackets()
    {
        ClientPlayNetworking.registerGlobalReceiver(KINETICS_SYNC_ID,
            (client, handler, buf, responseSender) ->
            {
                //corresponding sending is in StaggeringMixin
                float etoh = buf.readFloat();
                float ach = buf.readFloat();

                client.execute(() -> {
                    PlayerEntity player = client.player;

                    KineticsAttachments.setLevel(player, KineticsAttachments.ETHANOL, etoh);
                    KineticsAttachments.setLevel(player, KineticsAttachments.ACETALDEHYDE, ach);
                });
            }
        );
    }
}
