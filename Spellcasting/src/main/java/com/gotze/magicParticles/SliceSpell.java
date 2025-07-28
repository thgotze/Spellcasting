package com.gotze.magicParticles;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SliceSpell extends AbstractSpell {

    private static final int DISPLAY_COUNT = 6;
    private static final int[] START_DELAYS = {0, 42, 74, 116, 148, 190};
    private static final int ANIMATION_CYCLE_LENGTH = 12;
    private static final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};
    private static final int[] ANIMATION_DELAYS = {0, 6, 2, 8, 4, 10};

    private final ItemDisplay[] displays = new ItemDisplay[DISPLAY_COUNT];

    public SliceSpell(JavaPlugin plugin, Location location, Player player) {
        super(plugin, location, player);
        spawn();
    }


    @Override
    protected void spawn() {
        final World world = location.getWorld();

        // Calculate initial spawn location
        Location initialSpawnLocation = calculateSpawnLocation();

        for (int i = 0; i < DISPLAY_COUNT; i++) {
            displays[i] = (ItemDisplay) world.spawnEntity(initialSpawnLocation, EntityType.ITEM_DISPLAY);
            displays[i].setBrightness(new Display.Brightness(15, 15));

            displays[i].setTransformationMatrix(new Matrix4f()
                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i]))
                    .rotateX((float) Math.toRadians(90f))
                    .scale(5f, 5f, 0.1f)
            );
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                for (int i = 0; i < DISPLAY_COUNT; i++) {
                    if (ticks < START_DELAYS[i]) break;

                    if (ticks % 2 == 0) {
                        playSpellSound(i, ticks);
                        handleBlockBreaking(i, ticks, world);

                        if ((ticks - START_DELAYS[i]) % 12 == ANIMATION_DELAYS[i]) {
                            displays[i].teleport(calculateSpawnLocation());
                        }
                    }
                    updateDisplaySprite(i, ticks);
                }

                ticks++;

                if (ticks >= ANIMATION_CYCLE_LENGTH * 20) {
                    this.cancel();
                    remove();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private Location calculateSpawnLocation() {
        return player.getEyeLocation()
                .add(player.getLocation().getDirection().multiply(2.3f));
    }

    private void updateDisplaySprite(int displayIndex, int ticks) {
        int spriteTick = (ticks - START_DELAYS[displayIndex]) % ANIMATION_CYCLE_LENGTH;
        ItemStack displayItem = createDisplayItem(displayIndex, spriteTick);
        displays[displayIndex].setItemStack(displayItem);
    }

    private ItemStack createDisplayItem(int displayIndex, int spriteTick) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        String modelName = (displayIndex % 2 == 0)
                ? String.format("crescent_frontside%02d", spriteTick)
                : String.format("crescent_backside%02d", spriteTick);

        meta.setItemModel(NamespacedKey.minecraft(modelName));
        item.setItemMeta(meta);
        return item;
    }

    private void playSpellSound(int displayIndex, int ticks) {
        if (ticks % ANIMATION_CYCLE_LENGTH == ANIMATION_DELAYS[displayIndex]) {
            player.playSound(net.kyori.adventure.sound.Sound.sound(
                    Sound.ITEM_TRIDENT_THROW,
                    net.kyori.adventure.sound.Sound.Source.PLAYER,
                    0.20f,
                    1.35f
            ));
        }
    }

    private void teleportDisplays(int ticks, Location spawnLocation) {

        int displayIndex = getDisplayIndexToTeleport(ticks);
        if (displayIndex != -1) {
            displays[displayIndex].teleport(spawnLocation);
        }
    }

    private int getDisplayIndexToTeleport(int ticks) {
        int cycle = ticks % ANIMATION_CYCLE_LENGTH;
        return switch (cycle) {
            case 0 -> 0;   // 75 degrees
            case 6 -> 1;   // -75 degrees
            case 2 -> 2;   // 45 degrees
            case 8 -> 3;   // -45 degrees
            case 4 -> 4;   // 15 degrees
            case 10 -> 5;  // -15 degrees
            default -> 0;
        };
    }

    private void handleBlockBreaking(int displayIndex, int ticks, World world) {
        if (ticks % ANIMATION_CYCLE_LENGTH == ANIMATION_DELAYS[displayIndex]) {
            breakBlocksInLineOfSight(world, ticks);
        }
    }

    private void breakBlocksInLineOfSight(World world, int ticks) {
        List<Block> blocksInLineOfSight = player.getLineOfSight(null, 5);
        BlockFace playerFacing = player.getFacing();
        double reachDistance = 4.5;

        ArrayList<Block> blocksToBreak = new ArrayList<>();

        for (Block block : blocksInLineOfSight) {
            if (getDistanceToNearestPoint(player.getEyeLocation(), block) > reachDistance) {
                continue;
            }

            blocksToBreak.add(block);
            addAdjacentBlocks(world, block, ticks, playerFacing, reachDistance, blocksToBreak);
        }

        breakBlocks(blocksToBreak);
    }

    private void addAdjacentBlocks(World world, Block block, int ticks, BlockFace playerFacing,
                                   double reachDistance, ArrayList<Block> blocksToBreak) {
        int cycle = ticks % ANIMATION_CYCLE_LENGTH;
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        switch (cycle) {
            case 0, 6 -> addVerticalBlocks(world, x, y, z, reachDistance, blocksToBreak);
            case 2 -> addDiagonalBlocks45(world, x, y, z, playerFacing, reachDistance, blocksToBreak);
            case 8 -> addDiagonalBlocksNeg45(world, x, y, z, playerFacing, reachDistance, blocksToBreak);
            case 4 -> addHorizontalBlocks15(world, x, y, z, playerFacing, reachDistance, blocksToBreak);
            case 10 -> addHorizontalBlocksNeg15(world, x, y, z, playerFacing, reachDistance, blocksToBreak);
        }
    }

    private void addVerticalBlocks(World world, int x, int y, int z, double reachDistance,
                                   ArrayList<Block> blocksToBreak) {
        addBlockIfInReach(world.getBlockAt(x, y + 1, z), reachDistance, blocksToBreak);
        addBlockIfInReach(world.getBlockAt(x, y - 1, z), reachDistance, blocksToBreak);
    }

    private void addDiagonalBlocks45(World world, int x, int y, int z, BlockFace playerFacing,
                                     double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH -> {
                addBlockIfInReach(world.getBlockAt(x - 1, y - 1, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x + 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case SOUTH -> {
                addBlockIfInReach(world.getBlockAt(x + 1, y - 1, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x - 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case EAST -> {
                addBlockIfInReach(world.getBlockAt(x, y - 1, z - 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y + 1, z + 1), reachDistance, blocksToBreak);
            }
            case WEST -> {
                addBlockIfInReach(world.getBlockAt(x, y - 1, z + 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y + 1, z - 1), reachDistance, blocksToBreak);
            }
        }
    }

    private void addDiagonalBlocksNeg45(World world, int x, int y, int z, BlockFace playerFacing,
                                        double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH -> {
                addBlockIfInReach(world.getBlockAt(x + 1, y - 1, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x - 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case SOUTH -> {
                addBlockIfInReach(world.getBlockAt(x - 1, y - 1, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x + 1, y + 1, z), reachDistance, blocksToBreak);
            }
            case EAST -> {
                addBlockIfInReach(world.getBlockAt(x, y - 1, z + 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y + 1, z - 1), reachDistance, blocksToBreak);
            }
            case WEST -> {
                addBlockIfInReach(world.getBlockAt(x, y - 1, z - 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y + 1, z + 1), reachDistance, blocksToBreak);
            }
        }
    }

    private void addHorizontalBlocks15(World world, int x, int y, int z, BlockFace playerFacing,
                                       double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH, SOUTH -> {
                addBlockIfInReach(world.getBlockAt(x + 1, y, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x - 1, y, z), reachDistance, blocksToBreak);
            }
            case EAST, WEST -> {
                addBlockIfInReach(world.getBlockAt(x, y, z + 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y, z - 1), reachDistance, blocksToBreak);
            }
        }
    }

    private void addHorizontalBlocksNeg15(World world, int x, int y, int z, BlockFace playerFacing,
                                          double reachDistance, ArrayList<Block> blocksToBreak) {
        switch (playerFacing) {
            case NORTH, SOUTH -> {
                addBlockIfInReach(world.getBlockAt(x - 1, y, z), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x + 1, y, z), reachDistance, blocksToBreak);
            }
            case EAST, WEST -> {
                addBlockIfInReach(world.getBlockAt(x, y, z - 1), reachDistance, blocksToBreak);
                addBlockIfInReach(world.getBlockAt(x, y, z + 1), reachDistance, blocksToBreak);
            }
        }
    }

    private void addBlockIfInReach(Block block, double reachDistance, ArrayList<Block> blocksToBreak) {
        if (getDistanceToNearestPoint(player.getEyeLocation(), block) <= reachDistance) {
            blocksToBreak.add(block);
        }
    }

    private void breakBlocks(ArrayList<Block> blocksToBreak) {
        for (Block block : blocksToBreak) {
            if (!block.getType().isAir()) {
                block.breakNaturally(true);
            }
        }
    }

    private double getDistanceToNearestPoint(Location eyeLocation, Block block) {
        Location blockLocation = block.getLocation();

        double nearestX = Math.max(blockLocation.getX(), Math.min(eyeLocation.getX(), blockLocation.getX() + 1));
        double nearestY = Math.max(blockLocation.getY(), Math.min(eyeLocation.getY(), blockLocation.getY() + 1));
        double nearestZ = Math.max(blockLocation.getZ(), Math.min(eyeLocation.getZ(), blockLocation.getZ() + 1));

        return eyeLocation.distance(new Location(blockLocation.getWorld(), nearestX, nearestY, nearestZ));
    }

    @Override
    protected void remove() {
        for (ItemDisplay display : displays) {
            display.remove();
        }
    }
}
