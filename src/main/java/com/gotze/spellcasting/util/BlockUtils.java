package com.gotze.spellcasting.util;

import org.bukkit.Location;
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

    public static List<Block> getPositiveDiagonalBlocks(Block block, BlockFace playerFacing) {
        List<Block> blocks = new ArrayList<>();
        switch (playerFacing) {
            case NORTH -> {
                blocks.add(block.getRelative(-1, -1, 0));
                blocks.add(block.getRelative(1, 1, 0));
            }
            case SOUTH -> {
                blocks.add(block.getRelative(1, -1, 0));
                blocks.add(block.getRelative(-1, 1, 0));
            }
            case EAST -> {
                blocks.add(block.getRelative(0, -1, -1));
                blocks.add(block.getRelative(0, 1, 1));
            }
            case WEST -> {
                blocks.add(block.getRelative(0, -1, 1));
                blocks.add(block.getRelative(0, 1, -1));
            }
        }
        return blocks;
    }

    public static List<Block> getNegativeDiagonalBlocks(Block block, BlockFace playerFacing) {
        List<Block> blocks = new ArrayList<>();
        switch (playerFacing) {
            case NORTH -> {
                blocks.add(block.getRelative(1, -1, 0));
                blocks.add(block.getRelative(-1, 1, 0));
            }
            case SOUTH -> {
                blocks.add(block.getRelative(-1, -1, 0));
                blocks.add(block.getRelative(1, 1, 0));
            }
            case EAST -> {
                blocks.add(block.getRelative(0, -1, 1));
                blocks.add(block.getRelative(0, 1, -1));
            }
            case WEST -> {
                blocks.add(block.getRelative(0, -1, -1));
                blocks.add(block.getRelative(0, 1, 1));
            }
        }
        return blocks;
    }

    public static List<Block> getHorizontalBlocks(Block block, BlockFace playerFacing) {
        List<Block> blocks = new ArrayList<>();
        switch (playerFacing) {
            case NORTH, SOUTH -> {
                blocks.add(block.getRelative(1, 0, 0));
                blocks.add(block.getRelative(-1, 0, 0));
            }
            case EAST, WEST -> {
                blocks.add(block.getRelative(0, 0, 1));
                blocks.add(block.getRelative(0, 0, -1));
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInCylinderPattern(Block block, int radius, int height) {
        List<Block> blocks = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        blocks.add(block.getRelative(x, -y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInDiamondPattern(Block block, int size) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) <= size) {
                        blocks.add(block.getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInConePattern(Block block, Vector direction, int radius, int height) {
        List<Block> blocks = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            double currentRadius = radius * (1 - (double) y / height);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= currentRadius * currentRadius) {
                        blocks.add(block.getLocation().clone().add(direction.clone().multiply(y)).getBlock().getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInRingPattern(Block center, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z == radius * radius) {
                    blocks.add(center.getRelative(x, 0, z));
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInPyramidPattern(Block center, int size) {
        List<Block> blocks = new ArrayList<>();
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

    public static List<Block> getBlocksInCrossPattern(Block center, int size) {
        List<Block> blocks = new ArrayList<>();
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

    public static List<Block> getBlocksInSquarePattern(Block center, int size) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    blocks.add(center.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInSpherePattern(Block center, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        blocks.add(center.getRelative(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksInLine(Block startingBlock, Vector direction, int length) {
        List<Block> blocksInLine = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            Location blockLocation = startingBlock.getLocation().add(direction.multiply(i));
            blocksInLine.add(blockLocation.getBlock());
        }
        return blocksInLine;
    }
}