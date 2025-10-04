package net.tvc.proxy;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigHandler {
    public static String[] cfgHandle() throws IOException {
        new File("plugins/VelocityPersistent/").mkdirs();
        File configPath = new File(ProxyInstance.getInstance().getDataFolder(), "config.json");
        String [] config = new String[3];
        if (!configPath.exists()) {
            configWrite(configPath);
            config[0] = "0";
            config[1] = "0";
            return config;
        }
        config = configRead(configPath);

        return config;
    }

    public static void configWrite(File configPath) throws IOException {
        FileWriter fileWriter = new FileWriter(configPath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("// New players will connect to this server\n" +
            "default_server:windfall\n" +
            "// Message to display on kick when the server they tried to connect to is down. Use %server% to replaced with the server they tried to connect to, and %reason% for the reason\n" +
            "kick_text:Unable to connect to %server%: %reason%\n");
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
        bufferedReader.close();
        return configReturn;
    }
}