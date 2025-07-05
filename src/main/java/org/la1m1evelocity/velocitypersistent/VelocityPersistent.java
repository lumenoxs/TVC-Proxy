package org.la1m1evelocity.velocitypersistent;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;
@SuppressWarnings("ResultOfMethodCallIgnored")
@Plugin(
    id = "persistentserver2_0",
    name = "PersistentServer2.0",
    version = "1.1"
)
public class VelocityPersistent {
    @Getter
    private final Logger logger;
    @Getter
    private final ProxyServer proxy;

    String defaultServer;
    String kickText;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        new File("PersistentServerData/").mkdirs();
        String [] configData;
         configData = ConfigHandle.cfgHandle();
         if (Objects.equals(configData[0], "0") && Objects.equals(configData[1], "0")){
             logger.warn("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             logger.warn("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             logger.warn("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             proxy.shutdown();
         } else {

             this.defaultServer = configData[0];
             this.kickText = configData[1];
             this.reconnect = Boolean.parseBoolean(configData[2]);
             this.fallBack = configData[3];
             logger.info("Default server: " + defaultServer);
             logger.info("Kick message: " + kickText);
         }

    }

    @Inject
    public VelocityPersistent(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        logger.info("Persistent Server Loaded!");
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
            logger.info(event.getPlayer().getUsername() + " Failed to connect to " + targetServer);
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
}
