package net.tvc.proxy.managers;

import java.io.IOException;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;

import net.tvc.proxy.ProxyInstance;
import net.tvc.proxy.commands.InfoCommands;
import net.tvc.proxy.commands.ServerCommands;
import net.tvc.proxy.commands.VBanCommands;
import net.tvc.proxy.commands.VListCommand;

public class CommandsManager {
    private final ProxyServer proxy;
    
    @Inject
    public CommandsManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public void registerCommands() throws IOException {
        CommandManager commandManager = proxy.getCommandManager();
        ProxyInstance pluginInst = ProxyInstance.getInstance();

        commandManager.register(commandManager.metaBuilder("vban").plugin(pluginInst).build(), VBanCommands.createVBanCommand(proxy));
        commandManager.register(commandManager.metaBuilder("vpardon").plugin(pluginInst).build(), VBanCommands.createVPardonCommand(proxy));
        
        commandManager.register(commandManager.metaBuilder("core").plugin(pluginInst).build(), ServerCommands.createCoreCommand(proxy));
        commandManager.register(commandManager.metaBuilder("windfall").plugin(pluginInst).build(), ServerCommands.createWindfallCommand(proxy));
        commandManager.register(commandManager.metaBuilder("arena").plugin(pluginInst).build(), ServerCommands.createArenaComman(proxy));
        commandManager.register(commandManager.metaBuilder("lobby").aliases("hub").plugin(pluginInst).build(), ServerCommands.createLobbyCommand(proxy));

        commandManager.register(commandManager.metaBuilder("vlist").plugin(pluginInst).build(), VListCommand.createVListCommand(proxy));

        commandManager.register(commandManager.metaBuilder("discord").plugin(pluginInst).build(), InfoCommands.createDiscordCommand(proxy));
    }
}
