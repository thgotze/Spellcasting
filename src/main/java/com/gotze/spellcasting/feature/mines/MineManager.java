package com.gotze.spellcasting.feature.mines;

import com.gotze.spellcasting.util.LifecycleManager;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MineManager implements LifecycleManager {

    private static final List<Mine> mines = new ArrayList<>();

    @Override
    public void start() {
        // A Mine
        mines.add(new Mine(Rank.A)
                .corner1(-24, 96, 25)
                .corner2(24, 36, 77)
                .safetyTeleportLocation(0.5, 97, 22.5)
                .block(BlockTypes.STONE, 89.25)
                .block(BlockTypes.DIAMOND_ORE, 0.25)
                .block(BlockTypes.GOLD_ORE, 1.00)
                .block(BlockTypes.DEEPSLATE_GOLD_ORE, 0.50)
                .block(BlockTypes.RAW_GOLD_BLOCK, 0.25)
                .block(BlockTypes.COPPER_ORE, 3.00)
                .block(BlockTypes.DEEPSLATE_COPPER_ORE, 1.50)
                .block(BlockTypes.RAW_COPPER_BLOCK, 0.75)
                .block(BlockTypes.IRON_ORE, 2.00)
                .block(BlockTypes.DEEPSLATE_IRON_ORE, 1.00)
                .block(BlockTypes.RAW_IRON_BLOCK, 0.50)
                .build());

        // B Mine
        mines.add(new Mine(Rank.B)
                .corner1(-24 + 500, 96, 25)
                .corner2(24 + 500, 36, 77)
                .safetyTeleportLocation(0.5 + 500, 97, 22.5)
                .block(BlockTypes.STONE, 100)
                .block(BlockTypes.DIAMOND_ORE, 0.25 * 2)
                .block(BlockTypes.GOLD_ORE, 1.00 * 2)
                .block(BlockTypes.DEEPSLATE_GOLD_ORE, 0.50 * 2)
                .block(BlockTypes.RAW_GOLD_BLOCK, 0.25 * 2)
                .block(BlockTypes.COPPER_ORE, 3.00 * 2)
                .block(BlockTypes.DEEPSLATE_COPPER_ORE, 1.50 * 2)
                .block(BlockTypes.RAW_COPPER_BLOCK, 0.75 * 2)
                .block(BlockTypes.IRON_ORE, 2.00 * 2)
                .block(BlockTypes.DEEPSLATE_IRON_ORE, 1.00 * 2)
                .block(BlockTypes.RAW_IRON_BLOCK, 0.50 * 2)
                .build());

        // C Mine
        mines.add(new Mine(Rank.C)
    }

    @Override
    public void stop() {
        mines.forEach(Mine::stopRefilling);
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
}
