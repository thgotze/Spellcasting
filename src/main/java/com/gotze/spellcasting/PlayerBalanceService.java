package com.gotze.spellcasting;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerBalanceService {

    private static final Map<Player, Double> PLAYER_BALANCE_MAP = new HashMap<>();

    public static double getBalance(Player player) {
        return PLAYER_BALANCE_MAP.getOrDefault(player, 0.0);
    }

    public static void setBalance(Player player, double balance) {
        PLAYER_BALANCE_MAP.put(player, balance);
        saveBalanceDataToYAML(player);
    }

    public static void addBalance(Player player, double amount) {
        setBalance(player, getBalance(player) + amount);
    }
    
    public static void loadBalanceFromYAML(Player player) {
        if (PLAYER_BALANCE_MAP.containsKey(player)) return;

        UUID uuid = player.getUniqueId();
        File playerFile = new File(Spellcasting.getPlugin().getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        if (!playerFile.exists() || !yamlConfiguration.contains("balance")) {
            PLAYER_BALANCE_MAP.put(player, 0.0);
            saveBalanceDataToYAML(player);
            return;
        }

        PLAYER_BALANCE_MAP.put(player, yamlConfiguration.getDouble("balance"));
    }

    public static void saveBalanceDataToYAML(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(Spellcasting.getPlugin().getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        yamlConfiguration.set("balance", getBalance(player));

        try {
            yamlConfiguration.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}