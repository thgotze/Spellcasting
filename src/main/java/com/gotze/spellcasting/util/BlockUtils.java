package com.gotze.spellcasting.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static void breakBlocksInLineOfSight(Player player, int displayIndex) {
        List<Block> blocksInLineOfSight = player.getLineOfSight(null, 5);
        BlockFace playerFacing = player.getFacing();
        double reachDistance = 4.5;

        ArrayList<Block> blocksToBreak = new ArrayList<>();

        for (Block block : blocksInLineOfSight) {
            if (getDistanceSquaredToNearestPoint(player.getEyeLocation(), block, player.getWorld()) > reachDistance * reachDistance) continue;
            blocksToBreak.add(block);

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            switch (displayIndex) {
                case 0, 1 -> addVerticalBlocks(player.getWorld(), x, y, z, reachDistance, blocksToBreak);
                case 2 -> addDiagonalBlocks45(player.getWorld(),x, y, z, playerFacing, reachDistance, blocksToBreak);
                case 3 -> addDiagonalBlocksNeg45(player.getWorld(),x, y, z, playerFacing, reachDistance, blocksToBreak);
                case 4, 5 -> addHorizontalBlocks(player.getWorld(),x, y, z, playerFacing, reachDistance, blocksToBreak);
            }
        }

        for (Block block : blocksToBreak) {
            if (!block.getType().isAir()) {
                block.breakNaturally(true);
            }
        }
    }

    public static double getDistanceSquaredToNearestPoint(Location eyeLocation, Block block, World world) {
        Location blockLocation = block.getLocation();

        double nearestX = Math.max(blockLocation.getX(), Math.min(eyeLocation.getX(), blockLocation.getX() + 1));
        double nearestY = Math.max(blockLocation.getY(), Math.min(eyeLocation.getY(), blockLocation.getY() + 1));
        double nearestZ = Math.max(blockLocation.getZ(), Math.min(eyeLocation.getZ(), blockLocation.getZ() + 1));

        return eyeLocation.distanceSquared(new Location(blockLocation.getWorld(), nearestX, nearestY, nearestZ));
    }

    public static void addVerticalBlocks(World world, int x, int y, int z, double reachDistance, ArrayList<Block> blocksToBreak) {
        blocksToBreak.add(world.getBlockAt(x, y + 1, z));
        blocksToBreak.add(world.getBlockAt(x, y - 1, z));
//        addBlockIfInReach(world.getBlockAt(x, y + 1, z), reachDistance, blocksToBreak);
//        addBlockIfInReach(world.getBlockAt(x, y - 1, z), reachDistance, blocksToBreak);
    }

    public static void addDiagonalBlocks45(World world, int x, int y, int z, BlockFace playerFacing, double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH -> {
                blocksToBreak.add(world.getBlockAt(x - 1, y - 1, z));
                blocksToBreak.add(world.getBlockAt(x + 1, y + 1, z));
//                addBlockIfInReach(world.getBlockAt(x - 1, y - 1, z), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x + 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case SOUTH -> {
                blocksToBreak.add(world.getBlockAt(x + 1, y - 1, z));
                blocksToBreak.add(world.getBlockAt(x - 1, y + 1, z));
//                addBlockIfInReach(world.getBlockAt(x + 1, y - 1, z), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x - 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case EAST -> {
                blocksToBreak.add(world.getBlockAt(x, y - 1, z - 1));
                blocksToBreak.add(world.getBlockAt(x, y + 1, z + 1));
//                addBlockIfInReach(world.getBlockAt(x, y - 1, z - 1), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x, y + 1, z + 1), reachDistance, blocksToBreak);
            }
            case WEST -> {
                blocksToBreak.add(world.getBlockAt(x, y - 1, z + 1));
                blocksToBreak.add(world.getBlockAt(x, y + 1, z - 1));
//                addBlockIfInReach(world.getBlockAt(x, y - 1, z + 1), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x, y + 1, z - 1), reachDistance, blocksToBreak);
            }
        }
    }

    public static void addDiagonalBlocksNeg45(World world, int x, int y, int z, BlockFace playerFacing, double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH -> {
                blocksToBreak.add(world.getBlockAt(x + 1, y - 1, z));
                blocksToBreak.add(world.getBlockAt(x - 1, y + 1, z));
//                addBlockIfInReach(world.getBlockAt(x + 1, y - 1, z), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x - 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case SOUTH -> {
                blocksToBreak.add(world.getBlockAt(x - 1, y - 1, z));
                blocksToBreak.add(world.getBlockAt(x + 1, y + 1, z));
//                addBlockIfInReach(world.getBlockAt(x - 1, y - 1, z), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x + 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case EAST -> {
                blocksToBreak.add(world.getBlockAt(x, y - 1, z + 1));
                blocksToBreak.add(world.getBlockAt(x, y + 1, z - 1));
//                addBlockIfInReach(world.getBlockAt(x, y - 1, z + 1), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x, y + 1, z - 1), reachDistance, blocksToBreak);
            }
            case WEST -> {
                blocksToBreak.add(world.getBlockAt(x, y - 1, z - 1));
                blocksToBreak.add(world.getBlockAt(x, y + 1, z + 1));
//                addBlockIfInReach(world.getBlockAt(x, y - 1, z - 1), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x, y + 1, z + 1), reachDistance, blocksToBreak);
            }
        }
    }

    public static void addHorizontalBlocks(World world, int x, int y, int z, BlockFace playerFacing,
                                     double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH, SOUTH -> {
                blocksToBreak.add(world.getBlockAt(x + 1, y, z));
                blocksToBreak.add(world.getBlockAt(x - 1, y, z));
//                addBlockIfInReach(world.getBlockAt(x + 1, y, z), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x - 1, y, z), reachDistance, blocksToBreak);
            }
            case EAST, WEST -> {
                blocksToBreak.add(world.getBlockAt(x, y, z + 1));
                blocksToBreak.add(world.getBlockAt(x, y, z - 1));
//                addBlockIfInReach(world.getBlockAt(x, y, z + 1), reachDistance, blocksToBreak);
//                addBlockIfInReach(world.getBlockAt(x, y, z - 1), reachDistance, blocksToBreak);
            }
        }
    }

    public static ArrayList<Block> getBlocksInShape(Player player, Location center, String shape, int size) {
        ArrayList<Block> blocks = new ArrayList<>();
        Block centerBlock = center.getBlock();

        switch (shape.toLowerCase()) {
            case "cross":
                blocks.addAll(getCrossPattern(centerBlock, size));
                break;
            case "square":
                blocks.addAll(getSquarePattern(centerBlock, size));
                break;
            case "sphere":
                blocks.addAll(getSpherePattern(centerBlock, size));
                break;
            case "line":
                blocks.addAll(getLinePattern(player, centerBlock, size));
                break;
            case "cylinder":
                blocks.addAll(getCylinderPattern(centerBlock, size, size * 2));
                break;
            case "diamond":
                blocks.addAll(getDiamondPattern(centerBlock, size));
                break;
            case "cone":
                Vector direction = player.getEyeLocation().getDirection().normalize();
                blocks.addAll(getConePattern(centerBlock, direction, size, size * 2));
                break;
            case "ring":
                blocks.addAll(getRingPattern(centerBlock, size));
                break;
            case "pyramid":
                blocks.addAll(getPyramidPattern(centerBlock, size));
                break;
            default:
                blocks.add(centerBlock);
        }

        return blocks;
    }

    public static ArrayList<Block> getCylinderPattern(Block center, int radius, int height) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation().add(0.5, 0.5, 0.5);

        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        blocks.add(center.getRelative(x, y, z));
                    }
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getDiamondPattern(Block center, int size) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation();

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) <= size) {
                        blocks.add(center.getRelative(x, y, z));
                    }
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getConePattern(Block center, Vector direction, int radius, int height) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation();

        for (int y = 0; y < height; y++) {
            double currentRadius = radius * (1 - (double) y / height);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= currentRadius * currentRadius) {
                        blocks.add(centerLoc.clone().add(direction.clone().multiply(y)).getBlock().getRelative(x, y, z));
                    }
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getRingPattern(Block center, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation().add(0.5, 0.5, 0.5);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z == radius * radius) {
                    blocks.add(center.getRelative(x, 0, z));
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getPyramidPattern(Block center, int size) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation();

        for (int y = 0; y < size; y++) {
            int currentSize = size - y;
            for (int x = -currentSize; x <= currentSize; x++) {
                for (int z = -currentSize; z <= currentSize; z++) {
                    blocks.add(center.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getCrossPattern(Block center, int size) {
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(center);

        // Add blocks in cardinal directions
        for (int i = 1; i <= size; i++) {
            blocks.add(center.getRelative(BlockFace.NORTH, i));
            blocks.add(center.getRelative(BlockFace.SOUTH, i));
            blocks.add(center.getRelative(BlockFace.EAST, i));
            blocks.add(center.getRelative(BlockFace.WEST, i));
            blocks.add(center.getRelative(BlockFace.UP, i));
            blocks.add(center.getRelative(BlockFace.DOWN, i));
        }
        return blocks;
    }

    public static ArrayList<Block> getSquarePattern(Block center, int size) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    blocks.add(center.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static ArrayList<Block> getSpherePattern(Block center, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        Location centerLoc = center.getLocation().add(0.5, 0.5, 0.5);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + y*y + z*z <= radius*radius) {
                        blocks.add(center.getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static ArrayList<Block> getLinePattern(Player player, Block center, int length) {
        ArrayList<Block> blocks = new ArrayList<>();
        Vector direction = player.getEyeLocation().getDirection().normalize();

        for (int i = 0; i <= length; i++) {
            Location lineLocation = center.getLocation().add(direction.clone().multiply(i));
            blocks.add(lineLocation.getBlock());
        }

        return blocks;
    }
}
