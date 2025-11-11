package com.gotze.spellcasting.mine;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Mine {
    private final World world;
    private final WeightedBlockSelector weightedBlockSelector;
    private final Location corner1;
    private final Location corner2;
    private final List<Block> blocks;

    public Mine(World world, WeightedBlockSelector weightedBlockSelector, Location corner1, Location corner2) {
        this.world = world;
        this.weightedBlockSelector = weightedBlockSelector;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.blocks = getBlocksInRegion();
    }

    public void refillMine() {
        int batchSize = 1000;

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                int end = Math.min(index + batchSize, blocks.size());

                for (int i = index; i < end; i++) {
                    blocks.get(i).setType(weightedBlockSelector.getRandomBlock());
                }

                index = end;
                if (index >= blocks.size()) {
                    cancel();
                }
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
    }

    private List<Block> getBlocksInRegion() {
        List<Block> blocks = new ArrayList<>();

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
