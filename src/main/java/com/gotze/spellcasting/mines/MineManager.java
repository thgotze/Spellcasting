package com.gotze.spellcasting.mines;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MineManager {
    private final JavaPlugin plugin;
    private final Location corner1;
    private final Location corner2;
    private WeightedBlockSelector blockSelector;

    public MineManager(JavaPlugin plugin, Location corner1, Location corner2) {
        this.plugin = plugin;
        this.corner1 = corner1;
        this.corner2 = corner2;
        setupBlocks();
    }

    private void setupBlocks() {
        blockSelector = new WeightedBlockSelector();

        blockSelector.addBlock(Material.DIAMOND_ORE, 0.25);
        blockSelector.addBlock(Material.GOLD_ORE, 1.00);
        blockSelector.addBlock(Material.DEEPSLATE_GOLD_ORE, 0.50);
        blockSelector.addBlock(Material.RAW_GOLD_BLOCK, 0.25);
        blockSelector.addBlock(Material.COPPER_ORE, 3.00);
        blockSelector.addBlock(Material.DEEPSLATE_COPPER_ORE, 1.50);
        blockSelector.addBlock(Material.RAW_COPPER_BLOCK, 0.75);
        blockSelector.addBlock(Material.IRON_ORE, 2.00);
        blockSelector.addBlock(Material.DEEPSLATE_IRON_ORE, 1.00);
        blockSelector.addBlock(Material.RAW_IRON_BLOCK, 0.50);
        blockSelector.addBlock(Material.STONE, 89.25);
    }

    public void startAutoRefill() {
        // Run every 10 minutes (12000 ticks = 600 seconds)
        Bukkit.getScheduler().runTaskTimer(plugin, this::refillMine, 12000L, 12000L);

        // TODO: debug
        // Run every 2 minute (2400 ticks = 120 seconds)
        Bukkit.getScheduler().runTaskTimer(plugin, this::refillMine, 2400L, 2400L);
    }

    private void refillMine() {
        List<Block> blocks = getBlocksInRegion();
        int batchSize = 1000;

        plugin.getServer().broadcastMessage("Refilling mine with " + blocks.size() + " blocks");

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                int end = Math.min(index + batchSize, blocks.size());

                for (int i = index; i < end; i++) {
                    blocks.get(i).setType(blockSelector.getRandomBlock());
                }

                index = end;
                if (index >= blocks.size()) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private List<Block> getBlocksInRegion() {
        List<Block> blocks = new ArrayList<>();
        World world = corner1.getWorld();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }
}