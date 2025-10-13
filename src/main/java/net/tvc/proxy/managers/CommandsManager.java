package net.tvc.proxy.managers;

import java.io.IOException;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;

import net.tvc.proxy.commands.InfoCommands;
import net.tvc.proxy.commands.ServerCommands;
import net.tvc.proxy.commands.VBanCommands;

public class CommandsManager {
    private final ProxyServer proxy;
    
    @Inject
    public CommandsManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public void registerCommands() throws IOException {
        CommandManager commandManager = proxy.getCommandManager();

        commandManager.register(commandManager.metaBuilder("vban").plugin(this).build(), VBanCommands.createVBanCommand(proxy));
        commandManager.register(commandManager.metaBuilder("vpardon").plugin(this).build(), VBanCommands.createVPardonCommand(proxy));
        
        commandManager.register(commandManager.metaBuilder("core").plugin(this).build(), ServerCommands.createCoreCommand(proxy));
        commandManager.register(commandManager.metaBuilder("windfall").plugin(this).build(), ServerCommands.createWindfallCommand(proxy));
        commandManager.register(commandManager.metaBuilder("arena").plugin(this).build(), ServerCommands.createArenaComman(proxy));
        commandManager.register(commandManager.metaBuilder("lobby").aliases("hub").plugin(this).build(), ServerCommands.createLobbyCommand(proxy));

        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), InfoCommands.createDiscordCommand(proxy));
    }
}
