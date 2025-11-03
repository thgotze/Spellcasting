package com.gotze.spellcasting.data;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class PlayerProfileManager implements Listener {

    private static final Map<Player, PlayerProfile> PLAYER_PROFILE_MAP = new HashMap<>();
    private static final Map<Player, LocalDateTime> SESSION_START_MAP = new HashMap<>();

    public static PlayerProfile getPlayerProfile(Player player) {
        return PLAYER_PROFILE_MAP.get(player);
    }

    public static double getBalance(Player player) {
        return PLAYER_PROFILE_MAP.get(player).getBalance();
    }

    public static PickaxeData getPickaxeData(Player player) {
        return PLAYER_PROFILE_MAP.get(player).getPickaxeData();
    }

    public static void resetPickaxeData(Player player) {
        PLAYER_PROFILE_MAP.get(player).setPickaxeData(new PickaxeData());
    }

    @EventHandler
    public void onPlayerJoinLoadProfile(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        File playerFile = new File(Spellcasting.getPlugin().getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        PlayerProfile playerProfile;

        if (!playerFile.exists()) {
            // Create profile with default data
            playerProfile = new PlayerProfile();
            PLAYER_PROFILE_MAP.put(player, playerProfile);
            // Track session start time
            SESSION_START_MAP.put(player, LocalDateTime.now());
            saveProfile(playerProfile, playerFile, yamlConfiguration);
            return;
        }

        LocalDateTime joinDate = LocalDateTime.parse(Objects.requireNonNull(yamlConfiguration.getString("join-date")));
        LocalDateTime lastSeen = LocalDateTime.parse(Objects.requireNonNull(yamlConfiguration.getString("last-seen")));
        Duration playTime = Duration.ofSeconds(yamlConfiguration.getLong("playtime-seconds"));
        double balance = yamlConfiguration.getDouble("balance");

        // Pickaxe Data
        PickaxeData pickaxeData = new PickaxeData();
        pickaxeData.setPickaxeMaterial(PickaxeMaterial.valueOf(yamlConfiguration.getString("pickaxe-data.pickaxe-type")));
        pickaxeData.setDurabilityDamage(yamlConfiguration.getInt("pickaxe-data.durability-damage"));
        pickaxeData.addBlocksBroken(yamlConfiguration.getInt("pickaxe-data.blocks-broken"));

        for (String string : yamlConfiguration.getStringList("pickaxe-data.enchantments")) {
            String[] split = string.split(" ");

            Enchantment.EnchantmentType enchantmentType = Enchantment.EnchantmentType.valueOf(split[0]);
            int level = Integer.parseInt(split[1]);

            try {
                Enchantment enchantment = enchantmentType.getEnchantmentClass().getDeclaredConstructor().newInstance();
                enchantment.setLevel(level);
                pickaxeData.addEnchantment(enchantment);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load enchantment " + enchantmentType.name() + " from " + playerFile.getPath(), e);
            }
        }

        for (String string : yamlConfiguration.getStringList("pickaxe-data.abilities")) {
            String[] split = string.split(" ");

            Ability.AbilityType abilityType = Ability.AbilityType.valueOf(split[0]);
            int level = Integer.parseInt(split[1]);

            try {
                Ability ability = abilityType.getAbilityClass().getDeclaredConstructor().newInstance();
                ability.setLevel(level);
                pickaxeData.addAbility(ability);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load ability " + abilityType.name() + " from " + playerFile.getPath(), e);
            }
        }

        // Create profile with loaded data
        playerProfile = new PlayerProfile(joinDate, lastSeen, playTime, balance, pickaxeData);
        PLAYER_PROFILE_MAP.put(player, playerProfile);

        // Track session start time
        SESSION_START_MAP.put(player, LocalDateTime.now());
    }

    @EventHandler
    public void onPlayerQuitSaveProfile(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        File playerFile = new File(Spellcasting.getPlugin().getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        PlayerProfile playerProfile = PLAYER_PROFILE_MAP.get(player);

        playerProfile.setLastSeen(LocalDateTime.now());

        LocalDateTime sessionStart = SESSION_START_MAP.get(player);
        if (sessionStart != null) {
            Duration sessionDuration = Duration.between(sessionStart, LocalDateTime.now());
            playerProfile.setPlayTime(playerProfile.getPlayTime().plus(sessionDuration));
        }

        saveProfile(playerProfile, playerFile, yamlConfiguration);

        PLAYER_PROFILE_MAP.remove(player);
        SESSION_START_MAP.remove(player);
    }

    private void saveProfile(PlayerProfile playerProfile, File playerFile, YamlConfiguration yamlConfiguration) {
        yamlConfiguration.set("join-date", playerProfile.getJoinDate().toString());
        yamlConfiguration.set("last-seen", playerProfile.getLastSeen().toString());
        yamlConfiguration.set("playtime-seconds", playerProfile.getPlayTime().getSeconds());
        yamlConfiguration.set("balance", playerProfile.getBalance());

        // Pickaxe Data
        PickaxeData pickaxeData = playerProfile.getPickaxeData();
        yamlConfiguration.set("pickaxe-data.pickaxe-type", pickaxeData.getPickaxeMaterial().name());
        yamlConfiguration.set("pickaxe-data.durability-damage", pickaxeData.getDurabilityDamage());
        yamlConfiguration.set("pickaxe-data.blocks-broken", pickaxeData.getBlocksBroken());

        List<String> enchantmentsSerialized = pickaxeData.getEnchantments().stream()
                .map(enchantment -> enchantment.getEnchantmentType().name() + " " + enchantment.getLevel())
                .toList();
        yamlConfiguration.set("pickaxe-data.enchantments", enchantmentsSerialized);

        List<String> abilitiesSerialized = pickaxeData.getAbilities().stream()
                .map(ability -> ability.getAbilityType().name() + " " + ability.getLevel())
                .toList();
        yamlConfiguration.set("pickaxe-data.abilities", abilitiesSerialized);

        try {
            yamlConfiguration.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}