package org.la1m1evelocity.velocitypersistent;

import java.io.*;
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigHandle {
    public static String[] cfgHandle () throws IOException {

        new File("plugins/VelocityPersistent/").mkdirs();
        File configPath = new File("plugins/VelocityPersistent/config.json");
        String [] config = new String[3];
        if (!configPath.exists()) {
            configWrite(configPath);
            config[0] = "0"; config[1] = "0";
            return config;
        }
        config = configRead(configPath);

        return config;
    }
    public static void configWrite (File configPath) throws IOException {
        FileWriter fileWriter = new FileWriter(configPath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("// New players will connect to this server\n" +
                                    "default_server:WORLD1\n" +
                                    "// Message to display on kick when the server they tried to connect to is down\n" +
                                    "kick_text:Your server has not started yet!\n" +
                                    "// Allow players to join another server if their last server is offline (This will override the last server) WORK IN PROGRESS, NOT ACTIVE\n" +
                                    "reconnect_to_active:false\n" +
                                    "// Server to reconnect to if their last server is offline (e.g. lobby, this does nothing if reconnect_to_active is false) WORK IN PROGRESS, NOT ACTIVE\n" +
                                    "fallback_server:LOBBY");
        bufferedWriter.close();
    }
    public static String[] configRead (File configPath) throws IOException {
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(configPath));
        int i = 0;
        String [] configReturn = new String[4];
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("//")) continue;
            String value = line.split(":", 2)[1];
            configReturn[i]=value;
            i++;
        }
        return configReturn;
    }
}
