package com.gotze.spellcasting.util.block;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static List<Block> getVerticalBlocks(Block block) {
        List<Block> blocks = new ArrayList<>();

        blocks.add(block.getRelative(0, 1, 0));
        blocks.add(block.getRelative(0, -1, 0));

        return blocks;
    }

    public static List<Block> getPositiveDiagonalBlocks(Block origin, BlockFace blockFace, int distance) {
        List<Block> blocks = new ArrayList<>();

        switch (blockFace) {
            case UP -> {
                blocks.add(origin.getRelative(distance, 0, -distance));
                blocks.add(origin.getRelative(-distance, 0, distance));
            }
            case DOWN -> {
                blocks.add(origin.getRelative(-distance, 0, -distance));
                blocks.add(origin.getRelative(distance, 0, distance));
            }
            case NORTH -> {
                blocks.add(origin.getRelative(-distance, -distance, 0));
                blocks.add(origin.getRelative(distance, distance, 0));
            }
            case SOUTH -> {
                blocks.add(origin.getRelative(distance, -distance, 0));
                blocks.add(origin.getRelative(-distance, distance, 0));
            }
            case EAST -> {
                blocks.add(origin.getRelative(0, -distance, -distance));
                blocks.add(origin.getRelative(0, distance, distance));
            }
            case WEST -> {
                blocks.add(origin.getRelative(0, -distance, distance));
                blocks.add(origin.getRelative(0, distance, -distance));
            }
        }
        return blocks;
    }

    public static List<Block> getNegativeDiagonalBlocks(Block origin, BlockFace blockFace, int distance) {
        List<Block> blocks = new ArrayList<>();

        switch (blockFace) {
            case UP -> {
                blocks.add(origin.getRelative(-distance, 0, -distance));
                blocks.add(origin.getRelative(distance, 0, distance));
            }
            case DOWN -> {
                blocks.add(origin.getRelative(distance, 0, -distance));
                blocks.add(origin.getRelative(-distance, 0, distance));
            }
            case NORTH -> {
                blocks.add(origin.getRelative(distance, -distance, 0));
                blocks.add(origin.getRelative(-distance, distance, 0));
            }
            case SOUTH -> {
                blocks.add(origin.getRelative(-distance, -distance, 0));
                blocks.add(origin.getRelative(distance, distance, 0));
            }
            case EAST -> {
                blocks.add(origin.getRelative(0, -distance, distance));
                blocks.add(origin.getRelative(0, distance, -distance));
            }
            case WEST -> {
                blocks.add(origin.getRelative(0, -distance, -distance));
                blocks.add(origin.getRelative(0, distance, distance));
            }
        }
        return blocks;
    }

    public static List<Block> getHorizontalBlocks(Block origin, BlockFace playerFacing) {
        List<Block> blocks = new ArrayList<>();

        switch (playerFacing) {
            case NORTH, SOUTH -> {
                blocks.add(origin.getRelative(1, 0, 0));
                blocks.add(origin.getRelative(-1, 0, 0));
            }
            case EAST, WEST -> {
                blocks.add(origin.getRelative(0, 0, 1));
                blocks.add(origin.getRelative(0, 0, -1));
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInCylinderPattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfDepth = depth / 2;

        for (int y = 0; y < height; y++) {
            for (int x = -halfWidth; x < width - halfWidth; x++) {
                for (int z = -halfDepth; z < depth - halfDepth; z++) {
                    double ellipseCheck = (x * x) / (double) (halfWidth * halfWidth) + (z * z) / (double) (halfDepth * halfDepth);
                    if (ellipseCheck <= 1) {
                        blocks.add(origin.getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }


    public static List<Block> getBlocksInConePattern(Block origin, Vector direction, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfDepth = depth / 2;

        for (int y = 0; y < height; y++) {
            double currentWidth = halfWidth * (1 - (double) y / height);
            double currentDepth = halfDepth * (1 - (double) y / height);
            for (int x = -halfWidth; x < width - halfWidth; x++) {
                for (int z = -halfDepth; z < depth - halfDepth; z++) {
                    if (currentWidth > 0 && currentDepth > 0) {
                        if (x * x / (currentWidth * currentWidth) + z * z / (currentDepth * currentDepth) <= 1) {
                            blocks.add(origin.getLocation().clone().add(direction.clone().multiply(y)).getBlock().getRelative(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInRingPattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int halfDepth = depth / 2;

        for (int x = -halfWidth; x < width - halfWidth; x++) {
            for (int y = -halfHeight; y < height - halfHeight; y++) {
                for (int z = -halfDepth; z < depth - halfDepth; z++) {
                    if (halfWidth > 0 && halfHeight > 0 && halfDepth > 0) {
                        double distance = (x * x) / (double) (halfWidth * halfWidth) + (y * y) / (double) (halfHeight * halfHeight) + (z * z) / (double) (halfDepth * halfDepth);
                        if (distance >= 0.8 && distance <= 1.2) { // Ring thickness tolerance TODO: find out what AI is cooking here
                            blocks.add(origin.getRelative(x, y, z));
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInPyramidPattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfDepth = depth / 2;

        for (int y = 0; y < height; y++) {
            int currentWidth = halfWidth * (height - y) / height;
            int currentDepth = halfDepth * (height - y) / height;
            for (int x = -currentWidth; x <= currentWidth; x++) {
                for (int z = -currentDepth; z <= currentDepth; z++) {
                    blocks.add(origin.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInCrossPattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        if (width > 1) {
            int halfWidth = width / 2;
            for (int x = -halfWidth; x <= halfWidth; x++) {
                blocks.add(origin.getRelative(x, 0, 0));
            }
        }


        if (height > 1) {
            int halfHeight = height / 2;
            for (int y = -halfHeight; y <= halfHeight; y++) {
                blocks.add(origin.getRelative(0, y, 0));
            }
        }

        if (depth > 1) {
            int halfDepth = depth / 2;
            for (int z = -halfDepth; z <= halfDepth; z++) {
                blocks.add(origin.getRelative(0, 0, z));
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInSquarePattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int halfDepth = depth / 2;

        for (int x = -halfWidth; x < width - halfWidth; x++) {
            for (int y = -halfHeight; y < height - halfHeight; y++) {
                for (int z = -halfDepth; z < depth - halfDepth; z++) {
                    blocks.add(origin.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInSpherePattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        double radiusX = width / 2.0;
        double radiusY = height / 2.0;
        double radiusZ = depth / 2.0;

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int halfDepth = depth / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
                for (int z = -halfDepth; z <= halfDepth; z++) {

                    double normalizedDistance = 0.0;
                    if (halfWidth > 0) normalizedDistance += (x * x) / (radiusX * radiusX);
                    if (halfHeight > 0) normalizedDistance += (y * y) / (radiusY * radiusY);
                    if (halfDepth > 0) normalizedDistance += (z * z) / (radiusZ * radiusZ);

                    if (normalizedDistance <= 1) {
                        blocks.add(origin.getRelative(x, y, z));
                    }

                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInDiamondPattern(Block origin, int width, int height, int depth) {
        List<Block> blocks = new ArrayList<>();

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int halfDepth = depth / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
                for (int z = -halfDepth; z <= halfDepth; z++) {
                    int distanceX = Math.abs(x);
                    int distanceY = Math.abs(y);
                    int distanceZ = Math.abs(z);

                    double normalizedDistance = 0.0;
                    if (halfWidth > 0) normalizedDistance += (double) distanceX / halfWidth;
                    if (halfHeight > 0) normalizedDistance += (double) distanceY / halfHeight;
                    if (halfDepth > 0) normalizedDistance += (double) distanceZ / halfDepth;

                    if (normalizedDistance <= 1.0) {
                        blocks.add(origin.getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInLine(Block origin, Vector direction, int length) {
        List<Block> blocksInLine = new ArrayList<>();

        for (int i = 1; i <= length; i++) {
            Location blockLocation = origin.getLocation().add(direction.clone().multiply(i));
            blocksInLine.add(blockLocation.getBlock());
        }
        return blocksInLine;
    }

    public static List<Location> getBlockOutlineForParticles(Location location, double particleDistance) {
        List<Location> result = Lists.newArrayList();
        World world = location.getWorld();
        double minX = location.getBlockX();
        double minY = location.getBlockY();
        double minZ = location.getBlockZ();
        double maxX = location.getBlockX() + 1;
        double maxY = location.getBlockY() + 1;
        double maxZ = location.getBlockZ() + 1;

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }
}