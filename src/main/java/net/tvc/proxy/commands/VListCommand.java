package net.tvc.proxy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;

public class VListCommand {
    public static BrigadierCommand createVListCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> builder = BrigadierCommand.literalArgumentBuilder("vlist")
            .executes(context -> {
                CommandSource source = context.getSource();
                
                Map<String, List<String>> serverPlayers = new HashMap<>();
                for (RegisteredServer registeredServer : proxy.getAllServers()) {
                    List<String> players = registeredServer.getPlayersConnected().stream().map(Player::getUsername).collect(Collectors.toList());
                    serverPlayers.put(registeredServer.getServerInfo().getName(), players);
                }
                
                StringBuilder message = new StringBuilder("§a=================================\n");
                message.append("§eGlobal: §6").append(proxy.getPlayerCount()).append("§7/§6").append(proxy.getConfiguration().getShowMaxPlayers()).append("§r\n");
                
                for (Map.Entry<String, List<String>> entry : serverPlayers.entrySet()) {
                    message.append("§b").append(entry.getKey()).append(": §2");

                    List<String> players = entry.getValue();
                    if (players.isEmpty()) message.append("§dNo players!");
                    else message.append(String.join("§7, §2", players));
                    message.append("\n");
                }
                
                message.append("§a=================================");
                source.sendMessage(Component.text(message.toString()));
                return Command.SINGLE_SUCCESS;
            })
            .build();
            
        return new BrigadierCommand(builder);
    }
}