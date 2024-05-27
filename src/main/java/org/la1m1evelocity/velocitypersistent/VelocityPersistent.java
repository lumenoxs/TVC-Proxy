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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        new File("PersistentServerData/").mkdirs();
        new File("plugins/persistentserver2/").mkdirs();
        File config = new File("plugins/persistentserver2/config.json");
        if (!config.exists()) {
            FileWriter fileWriter = new FileWriter(config);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("default_server:SERVERNAME");
            bufferedWriter.close();
            logger.info("Please Edit the PersistentServer config!");
        }
        else {
            FileReader fileReader = new FileReader(config);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            this.defaultServer = bufferedReader.readLine().split(":")[1];
            logger.info("Persistent server config loaded! Default server: " + defaultServer);
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
        }


        if (!proxy.getServer(targetServer).isPresent()) {
            event.getPlayer().disconnect(Component.text("Ваш мир ещё не запущен!"));
        }
        else {
            RegisteredServer target = proxy.getServer(targetServer).orElse(null);

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