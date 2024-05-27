package org.la1m1evelocity.velocitypersistent;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
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

@Plugin(
        id = "persistentserver2_0",
        name = "PersistentServer2.0",
        version = "1.0-SNAPSHOT"
)
public class VelocityPersistent {
    private final List<UUID> connectedPlayers = new ArrayList<>();
    @Getter
    private final Logger logger;
    @Getter
    private final ProxyServer proxy;
    String defaultServer;
    String kickText;
    String fallBack;
    boolean reconnect;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        new File("PersistentServerData/").mkdirs();
        String [] configData;
         configData = ConfigHandle.cfgHandle();
         if (Objects.equals(configData[0], "0") && Objects.equals(configData[1], "0")){
             proxy.shutdown();
             logger.info("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             logger.info("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             logger.info("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             logger.info("EDIT THE VELOCITYPERSISTENT CONFIG AND RESTART");
             proxy.shutdown();
         }
         else {

             this.defaultServer = configData[0];
             this.kickText = configData[1];
             this.reconnect = Boolean.parseBoolean(configData[2]);
             this.fallBack = configData[3];
             logger.info(defaultServer);
             logger.info(kickText);
             logger.info(String.valueOf(reconnect));
             logger.info(fallBack);
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
        logger.info(UUID);
        logger.info(defaultServer);
        File lastServer = new File("PersistentServerData/"+UUID+".txt");
        String targetServer;
        if (!lastServer.exists()) {
            logger.info("FileNotExists");
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
        }
        RegisteredServer target = proxy.getServer(targetServer).orElse(null);
        logger.info(target.toString());
        if (!proxy.getServer(targetServer).isPresent()) {
//
        }
        else {

            event.setInitialServer(target);
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
    public void Post(KickedFromServerEvent event) throws IOException {
        if (String.valueOf(event.getServerKickReason()).equals("Optional.empty")) event.getPlayer().disconnect(Component.text("Ваш мир не запущен!"));
    }
}