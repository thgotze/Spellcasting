package com.gotze.spellcasting.mine;

import com.gotze.spellcasting.Spellcasting;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Mine {
    private final CuboidRegion cuboidRegion;
    private final Location teleportLocation;
    private final Pattern pattern;

    public Mine(Location corner1, Location corner2, Location teleportLocation, Pattern pattern) {
        if (corner1.getWorld() != corner2.getWorld()) {
            throw new IllegalArgumentException("Both corners must be in the same world!");
        }

        this.teleportLocation = teleportLocation;
        this.cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(corner1.getWorld()),
                BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()),
                BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ())
        );

        this.pattern = pattern;
    }

    public void refillMine() {
        List<Player> playersInMine = getPlayersInMine();
        playersInMine.forEach(this::teleportPlayerToSafety);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(cuboidRegion.getWorld())) {
            editSession.setBlocks((Region) cuboidRegion, pattern);
            editSession.flushQueue();

        } catch (Exception e) {
            Spellcasting.getPlugin().getLogger().warning("Failed to refill mine using FAWE: " + e.getMessage());
        }
    }

    public List<Player> getPlayersInMine() {
        World world = BukkitAdapter.adapt(cuboidRegion.getWorld());

        return world.getPlayers().stream()
                .filter(this::contains)
                .collect(Collectors.toList());
    }

    public void teleportPlayerToSafety(Player player) {
        player.teleport(teleportLocation);
        player.sendRichMessage("<yellow>The mine has reset! You've been teleported to safety");
    }

    public boolean contains(Block block) {
        return cuboidRegion.contains(BlockVector3.at(block.getX(), block.getY(), block.getZ()));
    }

    public boolean contains(Location location) {
        return cuboidRegion.contains(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
    }

    public boolean contains(Entity entity) {
        return cuboidRegion.contains(BlockVector3.at(entity.getX(), entity.getY(), entity.getZ()));
    }
}