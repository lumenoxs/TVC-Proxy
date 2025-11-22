package net.tvc.proxy.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.time.Instant;

import net.tvc.proxy.ProxyInstance;

public class VBanLogic {
    private static final String DB_FILE = ProxyInstance.getDataDirectory().resolve("bans.db").toString();

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS bans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,           -- "UUID" or "IP"
                    value TEXT NOT NULL,          -- UUID string or IP
                    reason TEXT NOT NULL,
                    info TEXT NOT NULL            -- time banned + banned by
                );
            """;
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void vBanPlayer(UUID uuid, String reason, String bannedBy) {
        vBan("UUID", uuid.toString(), reason, bannedBy);
    }

    public static void vBanIP(String ip, String reason, String bannedBy) {
        vBan("IP", ip, reason, bannedBy);
    }

    private static void vBan(String type, String value, String reason, String bannedBy) {
        String info = "Banned by " + bannedBy + " at " + Instant.now().toString();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO bans(type, value, reason, info) VALUES(?,?,?,?)")) {

            ps.setString(1, type);
            ps.setString(2, value);
            ps.setString(3, reason);
            ps.setString(4, info);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void vPardonPlayer(UUID uuid) {
        vPardon("UUID", uuid.toString());
    }

    public static void vPardonIP(String ip) {
        vPardon("IP", ip);
    }

    private static void vPardon(String type, String value) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM bans WHERE type = ? AND value = ?")) {

            ps.setString(1, type);
            ps.setString(2, value);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getBanlist() {
        List<String[]> bans = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT type, value, reason, info FROM bans")) {

            while (rs.next()) {
                bans.add(new String[]{
                        rs.getString("type"),
                        rs.getString("value"),
                        rs.getString("reason"),
                        rs.getString("info")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bans;
    }

    public static boolean isVBanned(String type, String ip_player) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM bans WHERE type = ? AND value = ? LIMIT 1")) {

            ps.setString(1, type);
            ps.setString(2, ip_player);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getBanReason(String type, String ip_player) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("SELECT reason FROM bans WHERE type = ? AND value = ? LIMIT 1")) {

            ps.setString(1, type);
            ps.setString(2, ip_player);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("reason");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }
    
    public static String getBanInfo(String type, String ip_player) {
        if (type == null || ip_player == null || ip_player.isBlank()) return "";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("SELECT info FROM bans WHERE type = ? AND value = ? LIMIT 1")) {

            ps.setString(1, type);
            ps.setString(2, ip_player);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("info");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<String> getVBannedPlayers() {
        List<String> players = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            PreparedStatement ps = conn.prepareStatement("SELECT value FROM bans WHERE type = 'UUID'")) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("value"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }
}
