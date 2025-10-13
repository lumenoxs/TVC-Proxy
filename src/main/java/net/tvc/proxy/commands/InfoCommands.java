package net.tvc.proxy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;

public class InfoCommands {
    public static BrigadierCommand createDiscordCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> serverCommand = BrigadierCommand.literalArgumentBuilder("discord")
            .executes(context -> {
                context.getSource().sendMessage(Component.text("<aqua>Join our discord to chat outside of the game!</aqua>\n<green><click:open_url:'https://discord.truevanilla.net'>https://discord.truevanilla.net</click></green> "));
                return Command.SINGLE_SUCCESS;
            })
            .build();

        return new BrigadierCommand(serverCommand);
    }
}
