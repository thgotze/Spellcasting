package com.gotze.magicParticles;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LaserSpell extends AbstractSpell {

    public LaserSpell(JavaPlugin plugin, Location location, Player player) {
        super(plugin, location, player);
        spawn();
    }

    @Override
    protected void spawn() {
        if (player == null) return;

        World world = location.getWorld();
        if (world == null) return;

        // Get the direction the player is looking
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Location startLocation = player.getEyeLocation();

        // Play laser sound
        world.playSound(startLocation, Sound.ENTITY_GUARDIAN_ATTACK, 0.1f, 1.5f);

        // Create laser beam with particles and break blocks
        new BukkitRunnable() {
            double distance = 0;

            @Override
            public void run() {
                if (distance >= 20) {
                    this.cancel();
                    return;
                }

                // Calculate current position along the laser beam
                Location currentLocation = startLocation.clone().add(direction.clone().multiply(distance));

                // Spawn laser particles
                world.spawnParticle(Particle.DUST, currentLocation, 1,
                        new Particle.DustOptions(Color.RED, 1.0f));
                world.spawnParticle(Particle.FLAME, currentLocation, 1, 0, 0, 0, 0);

                // Check for block to break

                ArrayList<Block> blocksToBreak = getBlocksInShape(currentLocation, "diamond", 3);

                for (Block block : blocksToBreak) {
                    if (!block.getType().isAir() && block.getType() != Material.BEDROCK) {
                        // Break the block
                        block.setType(Material.AIR);
//                        block.breakNaturally();

                        // Spawn break particles
                        world.spawnParticle(Particle.BLOCK, currentLocation, 10,
                                0.3, 0.3, 0.3, 0.1, block.getType().createBlockData());

                        // Play break sound
//                        world.playSound(currentLocation, Sound.BLOCK_STONE_BREAK, 0.8f, 1.2f);
                    }
                }

                distance += 1;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Run every tick
    }

    private ArrayList<Block> getBlocksInShape(Location center, String shape, int size) {
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
                blocks.addAll(getLinePattern(centerBlock, size));
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

    private ArrayList<Block> getCylinderPattern(Block center, int radius, int height) {
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

    private ArrayList<Block> getDiamondPattern(Block center, int size) {
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

    private ArrayList<Block> getConePattern(Block center, Vector direction, int radius, int height) {
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

    private ArrayList<Block> getRingPattern(Block center, int radius) {
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

    private ArrayList<Block> getPyramidPattern(Block center, int size) {
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

    private ArrayList<Block> getCrossPattern(Block center, int size) {
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

    private ArrayList<Block> getSquarePattern(Block center, int size) {
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

    private ArrayList<Block> getSpherePattern(Block center, int radius) {
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

    private ArrayList<Block> getLinePattern(Block center, int length) {
        ArrayList<Block> blocks = new ArrayList<>();
        Vector direction = player.getEyeLocation().getDirection().normalize();

        for (int i = 0; i <= length; i++) {
            Location lineLocation = center.getLocation().add(direction.clone().multiply(i));
            blocks.add(lineLocation.getBlock());
        }

        return blocks;
    }

    @Override
    protected void remove() {
        // Nothing to remove for this spell as it's instantaneous
    }
}