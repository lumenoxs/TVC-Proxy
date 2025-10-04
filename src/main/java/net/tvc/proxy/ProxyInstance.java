package net.tvc.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Plugin(
    id = "TVCProxy",
    name = "TVCProxy",
    version = "1.1"
)
public class ProxyInstance {
    private final Logger logger;
    private final ProxyServer proxy;

    String defaultServer;
    String kickText;

    public static ProxyInstance instance;

    @SuppressWarnings("unchecked")
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        new File("PersistentServerData/").mkdirs();
        Object[] rawConfig = ConfigHandler.getConfig();

        String[] config = (String[]) rawConfig[0];
        this.defaultServer = config[0];
        this.kickText = config[1];
        
        HashMap<String, String> forcedHosts = (HashMap<String, String>) rawConfig[1];

        logger.info("Default server: " + defaultServer);
        logger.info("Kick message: " + kickText);
        logger.info("Forced hosts: " + forcedHosts.toString());
        logger.info("TVC-Proxy Initialized!");
    }

    @Inject
    public ProxyInstance(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        ProxyInstance.instance = this;

        logger.info("TVC-Proxy Loaded!");
    }

    public static ProxyInstance getInstance() {
        return instance;
    }

    @Subscribe
    public void preConnectEvent(PlayerChooseInitialServerEvent event) throws IOException {
        String UUID = (event.getPlayer().getUniqueId().toString());

        File dataDir = new File("PersistentServerData");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File lastServer = new File("PersistentServerData/"+UUID+".txt");
        String targetServer;
        if (!lastServer.exists()) {
            FileWriter fileWriter = new FileWriter(lastServer);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(defaultServer);
            bufferedWriter.close();
            targetServer = defaultServer;
        }
        else {
            FileReader fileReader = new FileReader(lastServer);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            targetServer = bufferedReader.readLine();
            bufferedReader.close();
            if (targetServer == null || targetServer.isBlank()) {
                targetServer = defaultServer;
            }
        }
        Optional<RegisteredServer> target = proxy.getServer(targetServer);
        if (target.isPresent()) {
            event.setInitialServer(target.get());
        } else {
            logger.info(event.getPlayer().getUsername() + " failed to connect to " + targetServer  + " (server didnt exist)");
            logger.info("Falling back to " + defaultServer + "...");
        }
    }

    @Subscribe
    public void PostConnect(ServerPostConnectEvent event) throws IOException {
        String UUID = (event.getPlayer().getUniqueId().toString());
        File lastServer = new File("PersistentServerData/"+UUID+".txt");
        FileWriter fileWriter = new FileWriter(lastServer);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(String.valueOf(event.getPlayer().getCurrentServer()).split("> ")[1].split("]")[0]);
        bufferedWriter.close();
    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent event) {
        Optional<Component> serverKickReason = event.getServerKickReason();

        String serverKickReasonString = serverKickReason.get().toString();

        String kickMessage = kickText.replace("%server%", event.getServer().getServerInfo().getName());

        if (serverKickReason.isPresent()) {
            kickMessage = kickMessage.replace("%reason%", serverKickReasonString);
        } else {
            kickMessage = kickMessage.replace("%reason%", "The server is currently down.");
        }
        
        event.getPlayer().disconnect(Component.text(kickMessage));
    }
}
