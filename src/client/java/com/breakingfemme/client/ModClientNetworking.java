package com.breakingfemme.client;

import com.breakingfemme.KineticsAttachments;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

import static com.breakingfemme.ModNetworking.KINETICS_SYNC_ID;

public class ModClientNetworking {

    private ModClientNetworking() {
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(KINETICS_SYNC_ID,
                (client, handler, buf, responseSender) ->
                {
                    //corresponding sending is in KineticsAttachments
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
