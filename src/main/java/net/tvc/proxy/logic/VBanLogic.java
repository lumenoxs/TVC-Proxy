package net.tvc.proxy.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tvc.proxy.ProxyInstance;

public class VBanLogic {
    public static final String filePath = ProxyInstance.getDataDirectory().toAbsolutePath().toString() + "/vbanned-players.txt";

    public static List<String[]> getBanlist() throws IOException {
        List<String[]> banlist = new ArrayList<>();
        String player;
        String reason;
        String line;

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            player = line.split(":", 2)[0];
            reason = line.split(":", 2)[1];
            banlist.add(new String[]{player, reason});
        }
        reader.close();

        return banlist;
    }

    public static void updateBanlist(List<String[]> banlist) throws IOException {
        String filePath = "";

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        StringBuffer inputBuffer = new StringBuffer();

        for (String[] banpair : banlist) inputBuffer.append(banpair[0] + ":" + banpair[1] + "\n");

        writer.write(inputBuffer.toString());
        writer.close();
    }

    public static boolean isVBanned(String playerName) throws IOException {
        List<String[]> banlist = getBanlist();
        
        for (String[] banpair : banlist) {
            if (banpair[0].equals(playerName)) {
                return true;
            }
        }
        
        return false;
    }

    public static String getBanReason(String playerName) throws IOException {
        List<String[]> banlist = getBanlist();
        
        for (String[] banpair : banlist) {
            if (banpair[0].equals(playerName)) {
                return banpair[1];
            }
        }
        
        return "";
    }

    public static void vBan(String playerName, String reason) throws IOException {
        if (reason.equals("")) reason = "You are banned from this proxy!";
        else reason = "You are banned from this proxy. Reason:\n" + reason;

        List<String[]> banlist = getBanlist();
        banlist.add(new String[]{playerName, reason});
        updateBanlist(banlist);
    }

    public static void vPardon(String playerName) throws IOException {
        List<String[]> banlist = getBanlist();
        banlist.remove(new String[]{playerName, getBanReason(playerName)});
        updateBanlist(banlist);
    }

    public static List<String> getVBannedPlayers() throws IOException {
        List<String> players = new ArrayList<>();
        List<String[]> banlist = getBanlist();
        for (String[] banpair : banlist) players.add(banpair[0]);
        return players;
    }
}
