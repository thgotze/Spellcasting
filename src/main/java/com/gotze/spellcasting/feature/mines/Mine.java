package com.gotze.spellcasting.feature.mines;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.WorldUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

public class Mine {
    private final Rank requiredRank;
    private int refillDelayTicks;
    private Location corner1;
    private Location corner2;
    private CuboidRegion cuboidRegion;
    private Location safetyTeleportLocation;
    private final RandomPattern blockPattern = new RandomPattern();
    private BukkitTask mineRefiller;

    public Mine(Rank requiredRank) {
        this.requiredRank = requiredRank;
    }

    public Mine refillDelayTicks(int refillDelayTicks) {
        this.refillDelayTicks = refillDelayTicks;
        return this;
    }

    public Mine corner1(double x, double y, double z) {
        this.corner1 = new Location(WorldUtils.getWorld(), x, y, z);
        return this;
    }

    public Mine corner2(double x, double y, double z) {
        this.corner2 = new Location(WorldUtils.getWorld(), x, y, z);
        return this;
    }

    public Mine safetyTeleportLocation(double x, double y, double z) {
        this.safetyTeleportLocation = new Location(WorldUtils.getWorld(), x, y, z);
        return this;
    }

    public Mine block(BlockType blockType, double chance) {
        this.blockPattern.add(blockType, chance);
        return this;
    }

    public Mine build() {
        this.cuboidRegion = new CuboidRegion(
                BukkitAdapter.adapt(WorldUtils.getWorld()),
                BlockVector3.at(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()),
                BlockVector3.at(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ())
        );

        this.mineRefiller = Bukkit.getScheduler().runTaskTimer(Spellcasting.getPlugin(), this::refillMine, refillDelayTicks, refillDelayTicks);
        return this;
    }

    private void refillMine() {
        List<Player> playersInMine = getPlayersInMine();
        playersInMine.forEach(player -> {
            player.sendRichMessage("<yellow>The mine has reset! You've been teleported to safety");
            teleportPlayerToSafety(player);
        });

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(cuboidRegion.getWorld())) {
            editSession.setBlocks((Region) cuboidRegion, blockPattern);
            editSession.flushQueue();

        } catch (Exception e) {
            Spellcasting.getPlugin().getLogger().warning("Failed to refill mine " + mineName + " using FAWE: " + e.getMessage());
        }
    }

    public List<Player> getPlayersInMine() {
        World world = BukkitAdapter.adapt(cuboidRegion.getWorld());

        return world.getPlayers().stream()
                .filter(this::contains)
                .collect(Collectors.toList());
    }

    public void stopRefilling() {
        this.mineRefiller.cancel();
    }

    public void teleportPlayerToSafety(Player player) {
        player.teleport(safetyTeleportLocation);
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

    public Rank getRequiredRank() {
        return requiredRank;
    }
}
