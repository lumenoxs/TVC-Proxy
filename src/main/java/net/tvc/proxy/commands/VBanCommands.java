package net.tvc.proxy.commands;

import net.tvc.proxy.logic.VBanLogic;

import java.io.IOException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.minimessage.MiniMessage;

public final class VBanCommands {
    public static BrigadierCommand createVBanCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> vBanCommand = BrigadierCommand.literalArgumentBuilder("vban")
            .requires(source -> source.hasPermission("tvc.vban"))

            .executes(context -> {
                context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>You need a player argument!</red>\n<yellow>/vban <player></yellow>"));
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                .requires(source -> source.hasPermission("tvc.vban"))

                .suggests((ctx, builder) -> {
                    proxy.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                    return builder.buildFuture();
                })

                .executes(context -> {
                    String playerName = context.getArgument("player", String.class);
                    try {VBanLogic.vBan(playerName, "");} catch (IOException e) {}
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("reason", StringArgumentType.greedyString()))
                    .requires(source -> source.hasPermission("tvc.vban"))

                    .executes(context -> {
                        String reason = context.getArgument("reason", String.class);
                        String playerName = context.getArgument("player", String.class);
                        try {VBanLogic.vBan(playerName, reason);} catch (IOException e) {}
                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Banned the player " + playerName + "!</green>"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            .build();

        return new BrigadierCommand(vBanCommand);
    }
    
    public static BrigadierCommand createVPardonCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> vPardonCommand = BrigadierCommand.literalArgumentBuilder("vpardon")
            .requires(source -> source.hasPermission("tvc.vban"))

            .executes(context -> {
                context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>You need a player argument!</red>\n<yellow>/vpardon <player></yellow>"));
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                .requires(source -> source.hasPermission("tvc.vban"))

                .suggests((ctx, builder) -> {
                    try {VBanLogic.getVBannedPlayers().forEach(player -> builder.suggest(player));} catch (IOException e) {}
                    return builder.buildFuture();
                })

                .executes(context -> {
                    String playerName = context.getArgument("player", String.class);
                    try {
                        if (!VBanLogic.getVBannedPlayers().contains(playerName)) {
                            context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>The player " + playerName + " is not banned!</red>"));
                        } else {
                            VBanLogic.vPardon(playerName);
                            context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Pardoned the player " + playerName + "!</green>"));
                        }
                    } catch (IOException e) {}
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(vPardonCommand);
    }
}