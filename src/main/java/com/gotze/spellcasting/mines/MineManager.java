package com.gotze.spellcasting.mines;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.LifecycleManager;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MineManager implements LifecycleManager {

    private static final List<Mine> mines = new ArrayList<>();
    private BukkitTask tickTask;

    @Override
    public void start() {
        World world = Spellcasting.getPlugin().getServer().getWorld("world");

        RandomPattern minePattern1 = new RandomPattern();
        minePattern1.add(BlockTypes.DIAMOND_ORE.getDefaultState(), 0.25);
        minePattern1.add(BlockTypes.GOLD_ORE.getDefaultState(), 1.00);
        minePattern1.add(BlockTypes.DEEPSLATE_GOLD_ORE.getDefaultState(), 0.50);
        minePattern1.add(BlockTypes.RAW_GOLD_BLOCK.getDefaultState(), 0.25);
        minePattern1.add(BlockTypes.COPPER_ORE.getDefaultState(), 3.00);
        minePattern1.add(BlockTypes.DEEPSLATE_COPPER_ORE.getDefaultState(), 1.50);
        minePattern1.add(BlockTypes.RAW_COPPER_BLOCK.getDefaultState(), 0.75);
        minePattern1.add(BlockTypes.IRON_ORE.getDefaultState(), 2.00);
        minePattern1.add(BlockTypes.DEEPSLATE_IRON_ORE.getDefaultState(), 1.00);
        minePattern1.add(BlockTypes.RAW_IRON_BLOCK.getDefaultState(), 0.50);
        minePattern1.add(BlockTypes.STONE.getDefaultState(), 89.25);

        mines.add(new Mine(
                new Location(world, -24, 96, 25),
                new Location(world, 24, 36, 77),
                new Location(world, 0.5, 97, 22.5),
                minePattern1
        ));


        tickTask = Bukkit.getScheduler().runTaskTimer(
                Spellcasting.getPlugin(),
                () -> mines.forEach(Mine::refillMine),
                2400L, 2400L
        );
    }

    public static boolean isInAnyMine(Block block) {
        return mines.stream().anyMatch(mine -> mine.contains(block));
    }

    public static boolean isInAnyMine(Location location) {
        return mines.stream().anyMatch(mine -> mine.contains(location));
    }

    public static boolean isInAnyMine(Entity entity) {
        return mines.stream().anyMatch(mine -> mine.contains(entity));
    }

    public static List<Player> getAllPlayersInMines() {
        return mines.stream()
                .flatMap(mine -> mine.getPlayersInMine().stream())
                .collect(Collectors.toList());
    }

    public static void teleportPlayerToSafety(Player player) {
        for (Mine mine : mines) {
            if (mine.contains(player)) {
                mine.teleportPlayerToSafety(player);
                return;
            }
        }
    }

    public static void teleportAllPlayersToSafety() {
        mines.forEach(mine -> mine.getPlayersInMine().forEach(mine::teleportPlayerToSafety));
    }

    @Override
    public void stop() {
        if (tickTask != null) {
            tickTask.cancel();
        }
    }
}