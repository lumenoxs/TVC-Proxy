package net.tvc.proxy.commands;

import net.tvc.proxy.logic.VBanLogic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
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
                    CommandSource source = context.getSource();
                    Player banPlayer = proxy.getPlayer(context.getArgument("player", String.class)).get();

                    if (source instanceof Player player) {
                        VBanLogic.vBanPlayer(banPlayer.getUniqueId(), "", player.getUsername());
                    } else {
                        VBanLogic.vBanPlayer(banPlayer.getUniqueId(), "", "SERVER");
                    }

                    context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Banned the player " + banPlayer.getUsername() + "!</green>"));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("reason", StringArgumentType.greedyString()))
                    .requires(source -> source.hasPermission("tvc.vban"))

                    .executes(context -> {
                        CommandSource source = context.getSource();
                        Player banPlayer = proxy.getPlayer(context.getArgument("player", String.class)).get();
                        String reason = context.getArgument("reason", String.class);

                        if (source instanceof Player player) {
                            VBanLogic.vBanPlayer(banPlayer.getUniqueId(), reason, player.getUsername());
                        } else {
                            VBanLogic.vBanPlayer(banPlayer.getUniqueId(), reason, "SERVER");
                        }

                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Banned the player " + banPlayer.getUsername() + "!</green>"));
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
                    VBanLogic.getVBannedPlayers().forEach(player -> builder.suggest(player));
                    return builder.buildFuture();
                })

                .executes(context -> {
                    CommandSource source = context.getSource();
                    Player pardonPlayer = proxy.getPlayer(context.getArgument("player", String.class)).get();

                    if (!VBanLogic.getVBannedPlayers().contains(pardonPlayer.getUsername())) {
                        source.sendMessage(MiniMessage.miniMessage().deserialize("<red>The player " + pardonPlayer.getUsername() + " is not banned!</red>"));
                    } else {
                        VBanLogic.vPardonPlayer(pardonPlayer.getUniqueId());
                        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Pardoned the player " + pardonPlayer.getUsername() + "!</green>"));
                    }

                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();

        return new BrigadierCommand(vPardonCommand);
    }
}