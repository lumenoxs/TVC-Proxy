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
        // this is the only function that ill be commenting as i myself dont understand it so dont get used to it
        
        // player uuid
        String UUID = (event.getPlayer().getUniqueId().toString());
        
        // folder storing player data
        File dataDir = new File("PersistentServerData");
        // if it doesnt exist create it
        if (!dataDir.exists()) dataDir.mkdirs();

        // the file storing the players last server
        File playerDataFile = new File("PersistentServerData/"+UUID+".txt");
        // server to connect the player to
        String targetServerName;

        // if file doesnt exist (new player)
        if (!playerDataFile.exists()) {
            // idk how this works something to do with writing to files ask La1m1e on github
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(playerDataFile));
            // write the default server to the file
            bufferedWriter.write(defaultServer);
            // close the writer
            bufferedWriter.close();
            // set the target server to the default server
            targetServerName = defaultServer;
            logger.info("This is the first time the player " + event.getPlayer().getUsername() + " connects!");
            logger.info("Defaulting to " + defaultServer + "...");
        } else {
            // player has already connected before
            // magic!1!!111!
            BufferedReader bufferedReader = new BufferedReader(new FileReader(playerDataFile));
            // get the last server from the file
            targetServerName = bufferedReader.readLine();
            // close the reader
            bufferedReader.close();
            // if for whatever reason its empty, set target server to default server
            if (targetServerName == null || targetServerName.isBlank()) {
                targetServerName = defaultServer;
                logger.info("The player " + event.getPlayer().getUsername() + " had an empty/null data file.");
                logger.info("Falling back to " + defaultServer + "...");
            }
        }
        // get the target server from the target server name
        Optional<RegisteredServer> targetServer = proxy.getServer(targetServerName);
        // if the server exists
        if (targetServer.isPresent()) {
            event.setInitialServer(targetServer.get());
        } else {
            // server didnt exist :(
            logger.info(event.getPlayer().getUsername() + " failed to connect to " + targetServerName  + " (server didnt exist)");
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
