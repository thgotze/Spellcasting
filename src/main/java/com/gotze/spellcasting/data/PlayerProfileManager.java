package com.gotze.spellcasting.data;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.feature.islands.IslandData;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.LifecycleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class PlayerProfileManager implements Listener, LifecycleManager {
public class PlayerProfileManager implements Listener {

    private static final Map<Player, PlayerProfile> PLAYER_PROFILE_MAP = new HashMap<>();
    private static final Map<Player, LocalDateTime> SESSION_START_MAP = new HashMap<>();

    public static PlayerProfile getPlayerProfile(Player player) {
        return PLAYER_PROFILE_MAP.get(player);
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
        Rank rank = Rank.valueOf(yamlConfiguration.getString("rank"));

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

        // Island Data
        IslandData islandData = null;
        if (yamlConfiguration.contains("island-data")) {
            String islandCenterWorld = yamlConfiguration.getString("island-data.island-center.world");
            double islandCenterX = yamlConfiguration.getDouble("island-data.island-center.x");
            double islandCenterY = yamlConfiguration.getDouble("island-data.island-center.y");
            double islandCenterZ = yamlConfiguration.getDouble("island-data.island-center.z");
            Location islandCenterLocation = new Location(Bukkit.getWorld(islandCenterWorld), islandCenterX, islandCenterY, islandCenterZ);

            String islandHomeWorld = yamlConfiguration.getString("island-data.island-home.world");
            double islandHomeX = yamlConfiguration.getDouble("island-data.island-home.x");
            double islandHomeY = yamlConfiguration.getDouble("island-data.island-home.y");
            double islandHomeZ = yamlConfiguration.getDouble("island-data.island-home.z");
            Location islandHomeLocation = new Location(Bukkit.getWorld(islandHomeWorld), islandHomeX, islandHomeY, islandHomeZ);

            islandData = new IslandData(islandCenterLocation, islandHomeLocation);
        }
//        // Vaults
//        ConfigurationSection vaultSection = yamlConfiguration.getConfigurationSection("private-vaults");
//        Map<Integer, ItemStack[]> privateVaults = new HashMap<>();
//        if (vaultSection != null) {
//            for (String key : vaultSection.getKeys(false)) {
//                int vaultNumber = Integer.parseInt(key);
//                String base64 = vaultSection.getString(key);
//
//                ItemStack[] items = ItemSerializer.deserializeInventory(base64);
//                privateVaults.put(vaultNumber, items);
//            }
//        }

        // Create profile with loaded data
        playerProfile = new PlayerProfile(joinDate, lastSeen, playTime, balance, rank, pickaxeData, islandData/*, privateVaults*/);
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
            playerProfile.addPlayTime(sessionDuration);
        }

        saveProfile(playerProfile, playerFile, yamlConfiguration);

        PLAYER_PROFILE_MAP.remove(player);
        SESSION_START_MAP.remove(player);
    }

    public static void saveAllProfiles() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile playerProfile = PLAYER_PROFILE_MAP.get(player);
            if (playerProfile == null) continue;

            UUID uuid = player.getUniqueId();
            File playerFile = new File(Spellcasting.getPlugin().getDataFolder() + "/playerdata", uuid + ".yml");
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

            playerProfile.setLastSeen(LocalDateTime.now());

            LocalDateTime sessionStart = SESSION_START_MAP.get(player);
            if (sessionStart != null) {
                Duration sessionDuration = Duration.between(sessionStart, LocalDateTime.now());
                playerProfile.addPlayTime(sessionDuration);
            }

            saveProfile(playerProfile, playerFile, yamlConfiguration);
        }

        PLAYER_PROFILE_MAP.clear();
        SESSION_START_MAP.clear();
    }

    private static void saveProfile(PlayerProfile playerProfile, File playerFile, YamlConfiguration yamlConfiguration) {
        yamlConfiguration.set("join-date", playerProfile.getJoinDate().toString());
        yamlConfiguration.set("last-seen", playerProfile.getLastSeen().toString());
        yamlConfiguration.set("playtime-seconds", playerProfile.getPlayTime().getSeconds());
        yamlConfiguration.set("balance", playerProfile.getBalance());
        yamlConfiguration.set("rank", playerProfile.getRank().name());

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

        // Island Data
        IslandData islandData = playerProfile.getIslandData();
        if (islandData != null) {
            Location islandCenter = islandData.getIslandCenter();
            yamlConfiguration.set("island-data.island-center.world", islandCenter.getWorld().getName());
            yamlConfiguration.set("island-data.island-center.x", islandCenter.getX());
            yamlConfiguration.set("island-data.island-center.y", islandCenter.getY());
            yamlConfiguration.set("island-data.island-center.z", islandCenter.getZ());

            Location islandHome = islandData.getIslandHome();
            yamlConfiguration.set("island-data.island-home.world", islandHome.getWorld().getName());
            yamlConfiguration.set("island-data.island-home.x", islandHome.getX());
            yamlConfiguration.set("island-data.island-home.y", islandHome.getY());
            yamlConfiguration.set("island-data.island-home.z", islandHome.getZ());
        }

//        // Vaults
//        Map<Integer, ItemStack[]> vaults = playerProfile.getPrivateVaults();
//
//        for (Map.Entry<Integer, ItemStack[]> entry : vaults.entrySet()) {
//            int vaultNumber = entry.getKey();
//            ItemStack[] items = entry.getValue();
//
//            String base64 = ItemSerializer.serializeItems(items);
//            yamlConfiguration.set("private-vaults." + vaultNumber, base64);
//        }

        try {
            yamlConfiguration.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
