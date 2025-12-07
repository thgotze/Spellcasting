package com.gotze.islands;

import com.fastasyncworldedit.core.FaweAPI;
import com.gotze.spellcasting.Spellcasting;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IslandManager implements Listener {

    private final NamespacedKey KEY_ISLAND_X;
    private final NamespacedKey KEY_ISLAND_Y;
    private final NamespacedKey KEY_ISLAND_Z;
    private final NamespacedKey KEY_ISLAND_WORLD;

    public IslandManager() {
        this.KEY_ISLAND_X = new NamespacedKey(Spellcasting.getPlugin(), "island_x");
        this.KEY_ISLAND_Y = new NamespacedKey(Spellcasting.getPlugin(), "island_y");
        this.KEY_ISLAND_Z = new NamespacedKey(Spellcasting.getPlugin(), "island_z");
        this.KEY_ISLAND_WORLD = new NamespacedKey(Spellcasting.getPlugin(), "island_world");
    }

    public boolean hasIsland(Player player) {
        return player.getPersistentDataContainer().has(KEY_ISLAND_X, PersistentDataType.INTEGER);
    }

    public void createIsland(Player player) {
        if (hasIsland(player)) {
            player.sendMessage(Component.text("You already have an island!", NamedTextColor.RED));
            return;
        }

        // Calculate next location
        int nextX = Spellcasting.getPlugin().getConfig().getInt("islands.next-x", 1000);
        int y = 100;
        int z = 8;
        World world = player.getWorld(); // Or a specific island world

        // Paste Schematic
        File schematicFile = new File(Spellcasting.getPlugin().getDataFolder(), "island.schem");
        if (!schematicFile.exists()) {
            player.sendMessage(Component.text("Island schematic not found! Contact admin.", NamedTextColor.RED));
            return;
        }

        pasteSchematic(schematicFile, world, nextX, y, z);

        // Save data to Player
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(KEY_ISLAND_X, PersistentDataType.INTEGER, nextX);
        pdc.set(KEY_ISLAND_Y, PersistentDataType.INTEGER, y);
        pdc.set(KEY_ISLAND_Z, PersistentDataType.INTEGER, z);
        pdc.set(KEY_ISLAND_WORLD, PersistentDataType.STRING, world.getName());

        // Update next available spot
        Spellcasting.getPlugin().getConfig().set("islands.next-x", nextX + 2000);
        Spellcasting.getPlugin().saveConfig();

        player.sendMessage(Component.text("Island created!", NamedTextColor.GREEN));
        teleportHome(player);
    }

    public void teleportHome(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!hasIsland(player)) {
            player.sendMessage(Component.text("You do not have an island yet. Type /island create", NamedTextColor.RED));
            return;
        }

        Integer x = pdc.get(KEY_ISLAND_X, PersistentDataType.INTEGER);
        Integer y = pdc.get(KEY_ISLAND_Y, PersistentDataType.INTEGER);
        Integer z = pdc.get(KEY_ISLAND_Z, PersistentDataType.INTEGER);
        String worldName = pdc.get(KEY_ISLAND_WORLD, PersistentDataType.STRING);

        if (x != null && y != null && z != null && worldName != null) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                Location islandLocation = new Location(world, x, y, z);
//                Location islandLocation = new Location(world, 15000, 101, 8);

                player.teleport(islandLocation);
                player.sendMessage(Component.text("Teleported to island home!", NamedTextColor.GREEN));

                WorldBorder border = Bukkit.createWorldBorder();
                border.setCenter(islandLocation);
                border.setSize(64);
                player.setWorldBorder(border);
//                player.setWorldBorder(null);

            } else {
                player.sendMessage(Component.text("Island world not found!", NamedTextColor.RED));
            }
        }
    }

    private void pasteSchematic(File file, org.bukkit.World bukkitWorld, int x, int y, int z) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            Clipboard clipboard = reader.read();
            com.sk89q.worldedit.world.World weWorld = FaweAPI.getWorld(bukkitWorld.getName());
            
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, y, z - 1))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}