package net.tvc.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigHandler {
    public static Object[] getConfig() throws IOException {
        Path dataDirectory = ProxyInstance.getDataDirectory();
        new File(dataDirectory.toAbsolutePath().toString()).mkdirs();
        
        File configPath = new File(dataDirectory.toAbsolutePath().toString()+"/config.json");
        Object[] config = new Object[2];
        if (!configPath.exists()) configWrite(configPath);
        config = configRead(configPath);

        return config;
    }

    public static void configWrite(File configPath) throws IOException {
        configPath.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configPath));
        bufferedWriter.write(
            "// New players will connect to this server\n" +
            "default_server:windfall\n" +
            "// Message to display on kick when the server they tried to connect to is down. Use %server% to replaced with the server they tried to connect to, and %reason% for the reason\n" +
            "kick_text:Unable to connect to %server%: %reason%\n" +
            "// Forced hostnames (separate multiple with commas)\n" +
            "forced_host_mc.truevanilla.net:default\n" +
            "forced_host_lobby.truevanilla.net:lobby\n" +
            "forced_host_core.truevanilla.net:core\n" +
            "forced_host_arena.truevanilla.net:arena\n" +
            "forced_host_windfall.truevanilla.net:windfall\n"
        );
        bufferedWriter.close();
    }
    public static Object[] configRead(File configPath) throws IOException {
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(configPath));
        int i = 0;
        String[] config = new String[2];
        Map<String, String> forcedHosts = new HashMap<>();

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("//")) continue;
            if (line.startsWith("forced_host_")) forcedHosts.put(line.split(":", 2)[0].replace("forced_host_",""), line.split(":", 2)[1]);
            else {
                String value = line.split(":", 2)[1];
                config[i] = value;
                i++;
            }
        }
        bufferedReader.close();
        return new Object[]{config, forcedHosts};
    }
}