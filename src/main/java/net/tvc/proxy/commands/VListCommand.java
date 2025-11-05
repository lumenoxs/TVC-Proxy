package net.tvc.proxy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                
                StringBuilder message = new StringBuilder("<green>=================================</green>\n");
                message.append("<dark_aqua>Global:</dark_aqua> <gold>").append(proxy.getPlayerCount()).append("</gold><gray>/</gray><gold>").append(proxy.getConfiguration().getShowMaxPlayers()).append("</gold>\n");
                
                for (Map.Entry<String, List<String>> entry : serverPlayers.entrySet()) {
                    message.append("<aqua>").append(entry.getKey()).append(":</aqua> <dark_green>");

                    List<String> players = entry.getValue();
                    if (players.isEmpty()) message.append("</dark_green><light_purple>No players!</light_purple><dark_green>");
                    else message.append(String.join("</dark_green><gray>,</gray> <dark_green>", players));
                    message.append("</dark_green>\n");
                }
                
                message.append("<green>=================================</green>");
                source.sendMessage(MiniMessage.miniMessage().deserialize(message.toString()));
                return Command.SINGLE_SUCCESS;
            })
            .build();
            
        return new BrigadierCommand(builder);
    }
}