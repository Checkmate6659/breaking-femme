package com.breakingfemme;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class KineticsCommand {
    public KineticsCommand() {
    }

    private static Text getKineticsSummary(Entity entity)
    {
        if(!(entity instanceof PlayerEntity))
            return Text.translatable("command.breakingfemme.kinetics.not_player");

        PlayerEntity player = (PlayerEntity)entity;
        return Text.translatable("command.breakingfemme.kinetics.summary", player.getDisplayName()).append("\n")
            .append(Text.translatable("command.breakingfemme.kinetics.buffered_ethanol")).append(": ").append(player.getAttachedOrElse(KineticsAttachments.BUFFERED_ETHANOL, 0f).toString()).append("\n")
            .append(Text.translatable("command.breakingfemme.kinetics.ethanol")).append(": ").append(player.getAttachedOrElse(KineticsAttachments.ETHANOL, 0f).toString()).append("\n")
            .append(Text.translatable("command.breakingfemme.kinetics.acetaldehyde")).append(": ").append(player.getAttachedOrElse(KineticsAttachments.ACETALDEHYDE, 0f).toString())
        ;
    }

    private static Text getKineticsVariable(Entity entity, MutableText name, AttachmentType<Float> attachment)
    {
        if(!(entity instanceof PlayerEntity))
            return Text.translatable("command.breakingfemme.kinetics.not_player");

        PlayerEntity player = (PlayerEntity)entity;
        return name.append(": ").append(player.getAttachedOrElse(attachment, 0f).toString());
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)((LiteralArgumentBuilder<ServerCommandSource>)CommandManager
            .literal("breakingfemme_kinetics")
            //Summary
            .executes(context -> {
                ServerCommandSource source = context.getSource();
                source.sendFeedback(() -> getKineticsSummary(source.getEntity()), false);
                return 1;
            })
            //Individual levels (subcommands)
            .then(CommandManager.literal("buffered_ethanol")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> getKineticsVariable(source.getEntity(), Text.translatable("command.breakingfemme.kinetics.buffered_ethanol"), KineticsAttachments.BUFFERED_ETHANOL), false);
                    return 1;
                })
            )
            .then(CommandManager.literal("ethanol")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> getKineticsVariable(source.getEntity(), Text.translatable("command.breakingfemme.kinetics.ethanol"), KineticsAttachments.ETHANOL), false);
                    return 1;
                })
            )
            .then(CommandManager.literal("acetaldehyde")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> getKineticsVariable(source.getEntity(), Text.translatable("command.breakingfemme.kinetics.acetaldehyde"), KineticsAttachments.ACETALDEHYDE), false);
                    return 1;
                })
            )
        ));
    }
}
