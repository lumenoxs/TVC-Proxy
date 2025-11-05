package net.tvc.proxy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.MiniMessage;

public final class SendAllCommand {
    public static BrigadierCommand createSendAllCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> sendAllCommand = BrigadierCommand.literalArgumentBuilder("sendall")
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    if (player.getUsername().equals("RPiCPU")) {
                        proxy.getAllPlayers().forEach(p -> {
                            RegisteredServer targetServer = proxy.getServer("lobby").orElse(null);
                            if (targetServer != null) {
                                p.createConnectionRequest(targetServer).fireAndForget();
                            }
                        });
                        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>All players have been sent to the lobby!</green>"));
                    } else {
                        source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command!</red>"));
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
            .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word()))
                .executes(context -> {
                    String serverName = context.getArgument("server", String.class);
                    RegisteredServer targetServer = proxy.getServer(serverName).orElse(null);

                    if (targetServer == null) {
                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>Server not found!</red>"));
                        return Command.SINGLE_SUCCESS;
                    }

                    proxy.getAllPlayers().forEach(p -> {
                        p.createConnectionRequest(targetServer).fireAndForget();
                    });
                    context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>All players have been sent to " + serverName + "!</green>"));
                    return Command.SINGLE_SUCCESS;
                })
            .build();

        return new BrigadierCommand(sendAllCommand);
    }
}