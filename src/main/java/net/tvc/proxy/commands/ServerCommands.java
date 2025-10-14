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

import net.kyori.adventure.text.Component;

public final class ServerCommands {
    public static Logger logger;

    public ServerCommands(ProxyServer proxy, Logger logger) {
        ServerCommands.logger = logger;
    }

    public static BrigadierCommand createLobbyCommand(final ProxyServer proxy) {
        String commandServerName = "lobby";
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder(commandServerName)
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    Optional<ServerConnection> server = player.getCurrentServer();
                    if (server.isPresent()) {
                        String serverName = server.get().getServerInfo().getName();
                        if (serverName.equals(commandServerName)) {
                            source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                        } else {
                            Optional<RegisteredServer> target = proxy.getServer(commandServerName);
                            if (target.isPresent()) {
                                player.createConnectionRequest(target.get()).connect().thenAccept(result -> {
                                    switch (result.getStatus()) {
                                        case SUCCESS -> source.sendMessage(Component.text("<dark_aqua>Sent you to <gold>" + commandServerName + "</gold> succesfully!</dark_aqua>"));
                                        case ALREADY_CONNECTED -> source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>.</red>"));
                                        case CONNECTION_IN_PROGRESS -> source.sendMessage(Component.text("<aqua>Sending you to <gold>" + commandServerName + "</gold>...</aqua>"));
                                        default -> source.sendMessage(Component.text("<red>Failed to connect to <gold>" + commandServerName + "</gold>.</red>"));
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
                    source.sendMessage(Component.text("<red>You need to be a player to run this command!</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }

    public static BrigadierCommand createArenaComman(final ProxyServer proxy) {
        String commandServerName = "arena";
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder(commandServerName)
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    Optional<ServerConnection> server = player.getCurrentServer();
                    if (server.isPresent()) {
                        String serverName = server.get().getServerInfo().getName();
                        if (serverName.equals(commandServerName)) {
                            source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                        } else {
                            Optional<RegisteredServer> target = proxy.getServer(commandServerName);
                            if (target.isPresent()) {
                                player.createConnectionRequest(target.get()).connect().thenAccept(result -> {
                                    switch (result.getStatus()) {
                                        case SUCCESS -> source.sendMessage(Component.text("<dark_aqua>Sent you to <gold>" + commandServerName + "</gold> succesfully!</dark_aqua>"));
                                        case ALREADY_CONNECTED -> source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>.</red>"));
                                        case CONNECTION_IN_PROGRESS -> source.sendMessage(Component.text("<aqua>Sending you to <gold>" + commandServerName + "</gold>...</aqua>"));
                                        default -> source.sendMessage(Component.text("<red>Failed to connect to <gold>" + commandServerName + "</gold>.</red>"));
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
                    source.sendMessage(Component.text("<red>You need to be a player to run this command!</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }

    public static BrigadierCommand createWindfallCommand(final ProxyServer proxy) {
        String commandServerName = "windfall";
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder(commandServerName)
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    Optional<ServerConnection> server = player.getCurrentServer();
                    if (server.isPresent()) {
                        String serverName = server.get().getServerInfo().getName();
                        if (serverName.equals(commandServerName)) {
                            source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                        } else {
                            Optional<RegisteredServer> target = proxy.getServer(commandServerName);
                            if (target.isPresent()) {
                                player.createConnectionRequest(target.get()).connect().thenAccept(result -> {
                                    switch (result.getStatus()) {
                                        case SUCCESS -> source.sendMessage(Component.text("<dark_aqua>Sent you to <gold>" + commandServerName + "</gold> succesfully!</dark_aqua>"));
                                        case ALREADY_CONNECTED -> source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>.</red>"));
                                        case CONNECTION_IN_PROGRESS -> source.sendMessage(Component.text("<aqua>Sending you to <gold>" + commandServerName + "</gold>...</aqua>"));
                                        default -> source.sendMessage(Component.text("<red>Failed to connect to <gold>" + commandServerName + "</gold>.</red>"));
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
                    source.sendMessage(Component.text("<red>You need to be a player to run this command!</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }

    public static BrigadierCommand createCoreCommand(final ProxyServer proxy) {
        String commandServerName = "core";
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder(commandServerName)
            .executes(context -> {
                CommandSource source = context.getSource();

                if (source instanceof Player player) {
                    Optional<ServerConnection> server = player.getCurrentServer();
                    if (server.isPresent()) {
                        String serverName = server.get().getServerInfo().getName();
                        if (serverName.equals(commandServerName)) {
                            source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>!</red>"));
                        } else {
                            Optional<RegisteredServer> target = proxy.getServer(commandServerName);
                            if (target.isPresent()) {
                                player.createConnectionRequest(target.get()).connect().thenAccept(result -> {
                                    switch (result.getStatus()) {
                                        case SUCCESS -> source.sendMessage(Component.text("<dark_aqua>Sent you to <gold>" + commandServerName + "</gold> succesfully!</dark_aqua>"));
                                        case ALREADY_CONNECTED -> source.sendMessage(Component.text("<red>You're already on <gold>" + commandServerName + "</gold>.</red>"));
                                        case CONNECTION_IN_PROGRESS -> source.sendMessage(Component.text("<aqua>Sending you to <gold>" + commandServerName + "</gold>...</aqua>"));
                                        default -> source.sendMessage(Component.text("<red>Failed to connect to <gold>" + commandServerName + "</gold>.</red>"));
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
                    source.sendMessage(Component.text("<red>You need to be a player to run this command!</red>"));
                }

                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }
}
