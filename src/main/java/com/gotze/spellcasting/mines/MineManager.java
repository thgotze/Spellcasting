package com.gotze.spellcasting.mines;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.LifecycleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MineManager implements LifecycleManager {

    private final List<Mine> mines = new ArrayList<>();
    private BukkitTask tickTask;

    @Override
    public void start() {
        World world = Spellcasting.getPlugin().getServer().getWorld("world");

        WeightedBlockSelector weightedBlockSelector1 = new WeightedBlockSelector();
        weightedBlockSelector1.addBlock(Material.DIAMOND_ORE, 0.25);
        weightedBlockSelector1.addBlock(Material.GOLD_ORE, 1.00);
        weightedBlockSelector1.addBlock(Material.DEEPSLATE_GOLD_ORE, 0.50);
        weightedBlockSelector1.addBlock(Material.RAW_GOLD_BLOCK, 0.25);
        weightedBlockSelector1.addBlock(Material.COPPER_ORE, 3.00);
        weightedBlockSelector1.addBlock(Material.DEEPSLATE_COPPER_ORE, 1.50);
        weightedBlockSelector1.addBlock(Material.RAW_COPPER_BLOCK, 0.75);
        weightedBlockSelector1.addBlock(Material.IRON_ORE, 2.00);
        weightedBlockSelector1.addBlock(Material.DEEPSLATE_IRON_ORE, 1.00);
        weightedBlockSelector1.addBlock(Material.RAW_IRON_BLOCK, 0.50);
        weightedBlockSelector1.addBlock(Material.STONE, 89.25);

        mines.add(new Mine(world, weightedBlockSelector1,
                new Location(world, -24, 96, 25),
                new Location(world, 24, 36, 77)
        ));

        tickTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                Spellcasting.getPlugin(),
                () -> mines.forEach(Mine::refillMine),
                2400L, 2400L
        );
    }

    @Override
    public void stop() {
        if (tickTask != null) {
            tickTask.cancel();
        }
    }
}