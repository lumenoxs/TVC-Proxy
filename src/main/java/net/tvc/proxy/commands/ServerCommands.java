package net.tvc.proxy.commands;

import java.util.Optional;

import org.slf4j.Logger;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ServerCommands {
    public static Logger logger;

    public ServerCommands(ProxyServer proxy, Logger logger) {
        ServerCommands.logger = logger;
    }

    public static BrigadierCommand createServerCommand(final ProxyServer proxy, String commandServerName) {
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder(commandServerName)
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    Optional<ServerConnection> server = player.getCurrentServer();
                    if (server.isPresent()) {
                        String serverName = server.get().getServerInfo().getName();
                        if (serverName.equals(commandServerName)) {
                            if (serverName == "arena") {
                                return BrigadierCommand.FORWARD;
                            } else {
                                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                                return Command.SINGLE_SUCCESS;
                            }
                        } else {
                            Optional<RegisteredServer> target = proxy.getServer(commandServerName);
                            if (target.isPresent()) {
                                player.createConnectionRequest(target.get()).connect().thenAccept(result -> {
                                    switch (result.getStatus()) {
                                        case SUCCESS -> source.sendMessage(MiniMessage.miniMessage().deserialize("<dark_aqua>Sent you to <gold>" + commandServerName + "</gold>!</dark_aqua>"));
                                        case ALREADY_CONNECTED -> source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                                        case CONNECTION_IN_PROGRESS -> source.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Sending you to <gold>" + commandServerName + "</gold>...</aqua>"));
                                        default -> source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to connect to <gold>" + commandServerName + "</gold>.</red>"));
                                    }
                                });
                            } else {
                                logger.info("Server " + commandServerName + " doesn't exist!");
                            }
                        }
                    } else {
                        logger.info("Player " + player.getUsername() + " is on a server that doesn't exist???");
                    }
                } else {
                    source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You need to be a player to run this command!</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }

    public static BrigadierCommand createLobbyCommand(final ProxyServer proxy) {
        return createServerCommand(proxy, "lobby");
    }

    public static BrigadierCommand createArenaCommand(final ProxyServer proxy) {
        return createServerCommand(proxy, "arena");
    }

    public static BrigadierCommand createWindfallCommand(final ProxyServer proxy) {
        return createServerCommand(proxy, "windfall");
    }

    public static BrigadierCommand createCoreCommand(final ProxyServer proxy) {
        return createServerCommand(proxy, "core");
    }
}
