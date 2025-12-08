package com.gotze.spellcasting.islands;

import com.fastasyncworldedit.core.FaweAPI;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.util.SoundUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class IslandManager implements Listener {

    private static final int ISLAND_STARTING_DISTANCE = 3000;
    private static final int ISLAND_SEPARATION_DISTANCE = 2000;

    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        event.setCancelled(true);

        Location playerLocation = player.getLocation();

        player.teleport(new Location(
                playerLocation.getWorld(),
                playerLocation.getX(),
                325,
                playerLocation.getZ()
        ));
    }

    public static boolean isLocationOnPlayerIsland(Player player, Location location) {
        IslandData islandData = IslandData.fromPlayer(player);
        if (islandData == null) return false;

        Location center = islandData.getIslandCenter();

        double minX = center.getX() - islandData.getIslandRadius();
        double maxX = center.getX() + islandData.getIslandRadius();
        double minZ = center.getZ() - islandData.getIslandRadius();
        double maxZ = center.getZ() + islandData.getIslandRadius();

        return location.getX() >= minX && location.getX() <= maxX &&
                location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    public static void createIsland(Player player) {
        IslandData islandData = IslandData.fromPlayer(player);
        if (islandData != null) {
            player.sendMessage(text("You already have an island!", RED));
            return;
        }

        final int playerIslandsCount = Spellcasting.getPlugin().getConfig().getInt("player-islands-count", 0);
        Spellcasting.getPlugin().getConfig().set("player-islands-count", playerIslandsCount + 1);
        Spellcasting.getPlugin().saveConfig();

        // Find the location for the island
        World world = player.getWorld();
        int x = playerIslandsCount * ISLAND_SEPARATION_DISTANCE + ISLAND_STARTING_DISTANCE;
        int y = 100;
        int z = 8;
        
        // Save the island data to the player profile
        Location islandCenter = new Location(world, x, y, z);
        islandData = new IslandData(islandCenter);
        PlayerProfileManager.getPlayerProfile(player).setIslandData(islandData);
        
        // Paste island schematic
        File islandSchematicFile = new File(Spellcasting.getPlugin().getDataFolder(), "default-player-island.schem");
        if (!islandSchematicFile.exists()) {
            player.sendMessage(text("Default island schematic file not found! Contact admin", RED));
            return;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(islandSchematicFile);
        if (format == null) return;

        try (ClipboardReader reader = format.getReader(new FileInputStream(islandSchematicFile))) {
            Clipboard clipboard = reader.read();
            com.sk89q.worldedit.world.World weWorld = FaweAPI.getWorld(world.getName());

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, y, z - 1))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(text("Your island has been successfully created!", GREEN));
        SoundUtils.playSuccessSound(player);
    }

    public static void teleportToIslandHome(Player player) {
        IslandData islandData = IslandData.fromPlayer(player);
        if (islandData == null) {
            player.sendMessage(text("You do not have an island yet!", RED));
            return;
        }

        Location islandHome = islandData.getIslandHome();

        player.teleport(islandHome);
        player.sendMessage(text("Teleported to island home!", GREEN));

        WorldBorder islandBorder = Bukkit.createWorldBorder();
        islandBorder.setCenter(islandData.getIslandCenter());
        islandBorder.setSize(islandData.getIslandRadius());
        player.setWorldBorder(islandBorder);
    }

    public static void setIslandHome(Player player) {
        IslandData islandData = IslandData.fromPlayer(player);
        if (islandData == null) {
            player.sendMessage(text("You do not have an island yet!", RED));
            return;
        }

        Location playerLocation = player.getLocation();
        islandData.setIslandHome(playerLocation);
        player.sendMessage(text("Your island home has been successfully set!", GREEN));
        SoundUtils.playSuccessSound(player);
    }

    public static void resetIslandHome(Player player) {
        IslandData islandData = IslandData.fromPlayer(player);
        if (islandData == null) {
            player.sendMessage(text("You do not have an island yet!", RED));
            return;
        }

        islandData.setIslandHome(islandData.getIslandCenter());
        player.sendMessage(text("Your island home has been successfully reset!", GREEN));
        SoundUtils.playSuccessSound(player);
    }
}