package com.gotze.magicParticles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CrescentSpell extends AbstractSpell {

    private final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};
    private final int[] ANIMATION_DELAYS = {0, 6, 2, 8, 4, 10};
    private final int[] START_DELAYS = {0, 42, 74, 116, 148, 190};

    private final ItemDisplay[] displays = new ItemDisplay[6];
    private final ItemDisplay[] displayBacksides = new ItemDisplay[6];

    public CrescentSpell(JavaPlugin plugin, Location location, Player player) {
        super(plugin, location, player);
        spawn();
    }

    @Override
    protected void spawn() {

        new BukkitRunnable() {
            int ticks = 0;

            Location spawnLocation = null;
            final World world = location.getWorld();

            @Override
            public void run() {

                // Play sound
                if (ticks % 2 == 0) {
                    for (int i = 0; i < 6; i++) {
                        if (ticks >= START_DELAYS[i]) {
                            if (ticks % 12 == ANIMATION_DELAYS[i]) {
                                player.playSound(net.kyori.adventure.sound.Sound.sound(
                                        Sound.ITEM_TRIDENT_THROW,
                                        net.kyori.adventure.sound.Sound.Source.PLAYER,
                                        0.20f,
                                        1.35f
                                ));
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

                // Only update location every other tick
                if (ticks == 0 || ticks % 2 == 0) {
                    spawnLocation = player.getEyeLocation()
                            .add(player.getLocation().getDirection().multiply(3));

                    // Only create the displays on 0th tick
                    if (displays[displays.length - 1] == null) {
                        for (int i = 0; i < 6; i++) {
                            // Create frontside crescent
                            displays[i] = world.spawn(spawnLocation, ItemDisplay.class);
                            displays[i].setBrightness(new Display.Brightness(15, 15));
                            displays[i].setTransformationMatrix(new Matrix4f()
                                    .rotateX((float) Math.toRadians(90f - 0.5f))
                                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i]))
                                    .scale(17, 27, 0.1f)
                            );

                            // Create backside crescent
                            displayBacksides[i] = world.spawn(spawnLocation, ItemDisplay.class);
                            displayBacksides[i].setBrightness(new Display.Brightness(15, 15));
                            displayBacksides[i].setTransformationMatrix(new Matrix4f()
                                    .rotateX((float) Math.toRadians(90f - 0.5f))
                                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i] + 180f))
                                    .scale(17, 27, 0.1f)
                            );
                        }
                    }
                }

                // Teleport only one of the 6 pairs every tick
                if (ticks % 2 == 0) {
                    if (ticks % 12 == 0) {
                        displays[0].teleport(spawnLocation);
                        displayBacksides[0].teleport(spawnLocation);
                    } else if (ticks % 12 == 6) {
                        displays[1].teleport(spawnLocation);
                        displayBacksides[1].teleport(spawnLocation);
                    } else if (ticks % 12 == 2) {
                        displays[2].teleport(spawnLocation);
                        displayBacksides[2].teleport(spawnLocation);
                    } else if (ticks % 12 == 8) {
                        displays[3].teleport(spawnLocation);
                        displayBacksides[3].teleport(spawnLocation);
                    } else if (ticks % 12 == 4) {
                        displays[4].teleport(spawnLocation);
                        displayBacksides[4].teleport(spawnLocation);
                    } else if (ticks % 12 == 10) {
                        displays[5].teleport(spawnLocation);
                        displayBacksides[5].teleport(spawnLocation);
                    }
                }

                // Change sprite
                for (int i = 0; i < 6; i++) {
                    if (ticks >= START_DELAYS[i]) {
                        int spriteTick = (ticks - START_DELAYS[i]) % 12;

                        // Create items for frontside and backside
                        ItemStack frontsideItem = new ItemStack(Material.PAPER);
                        ItemMeta frontsideMeta = frontsideItem.getItemMeta();

                        ItemStack backsideItem = new ItemStack(Material.PAPER);
                        ItemMeta backsideMeta = backsideItem.getItemMeta();

                        if (i % 2 == 0) { // Clockwise displays
                            frontsideMeta.setItemModel(NamespacedKey.minecraft(String.format("crescent_frontside%02d", spriteTick)));
                            backsideMeta.setItemModel(NamespacedKey.minecraft(String.format("crescent_backside%02d", spriteTick)));
                        } else { // Counter-clockwise displays
                            frontsideMeta.setItemModel(NamespacedKey.minecraft(String.format("crescent_backside%02d", spriteTick)));
                            backsideMeta.setItemModel(NamespacedKey.minecraft(String.format("crescent_frontside%02d", spriteTick)));
                        }

                        frontsideItem.setItemMeta(frontsideMeta);
                        backsideItem.setItemMeta(backsideMeta);

                        displays[i].setItemStack(frontsideItem);
                        displayBacksides[i].setItemStack(backsideItem);
                    } else {
                        break;
                    }
                }


                // Break blocks
                if (ticks % 2 == 0) {
                    for (int i = 0; i < 6; i++) {
                        if (ticks >= START_DELAYS[i]) {
                            if (ticks % 12 == ANIMATION_DELAYS[i]) {
                                List<Block> blocksInLineOfSight = player.getLineOfSight(null, 5);
                                BlockFace playerFacing = player.getFacing();

                                ArrayList<Block> blocksToBreak = new ArrayList<>();

                                for (Block block : blocksInLineOfSight) {
                                    Location blockLocation = block.getLocation();
                                    int x = blockLocation.getBlockX();
                                    int y = blockLocation.getBlockY();
                                    int z = blockLocation.getBlockZ();
                                    blocksToBreak.add(world.getBlockAt(x,y,z));

                                    if (ticks % 12 == 0) { // 75

                                        blocksToBreak.add(world.getBlockAt(x, y + 1, z));
                                        blocksToBreak.add(world.getBlockAt(x, y - 1, z));
//                            blocksToBreak.add(world.getBlockAt(x, y + 2, z));
//
//                            if (playerFacing == BlockFace.NORTH) {
//                                blocksToBreak.add(world.getBlockAt(x + 1, y + 2, z));
//
//                            } else if (playerFacing == BlockFace.SOUTH) {
//                                blocksToBreak.add(world.getBlockAt(x - 1, y + 2, z));
//
//                            } else if (playerFacing == BlockFace.EAST) {
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z + 1));
//
//                            } else if (playerFacing == BlockFace.WEST) {
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z - 1));
//                            }

                                    } else if (ticks % 12 == 6 && ticks >= START_DELAYS[1]) { // -75 degrees
                                        blocksToBreak.add(world.getBlockAt(x, y + 1, z));
                                        blocksToBreak.add(world.getBlockAt(x, y - 1, z));
//                            blocksToBreak.add(world.getBlockAt(x, y + 2, z));
//
//                            if (playerFacing == BlockFace.NORTH) {
//                                blocksToBreak.add(world.getBlockAt(x - 1, y + 2, z));
//
//                            } else if (playerFacing == BlockFace.SOUTH) {
//                                blocksToBreak.add(world.getBlockAt(x + 1, y + 2, z));
//
//                            } else if (playerFacing == BlockFace.EAST) {
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z - 1));
//
//                            } else if (playerFacing == BlockFace.WEST) {
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z + 1));
//                            }

                                    } else if (ticks % 12 == 2 && ticks >= START_DELAYS[2]) { // 45 degrees
                                        if (playerFacing == BlockFace.NORTH) {
                                            blocksToBreak.add(world.getBlockAt(x - 1, y - 1, z));
                                            blocksToBreak.add(world.getBlockAt(x + 1, y + 1, z));
//                                blocksToBreak.add(world.getBlockAt(x + 2, y + 2, z));

                                        } else if (playerFacing == BlockFace.SOUTH) {
                                            blocksToBreak.add(world.getBlockAt(x + 1, y - 1, z));
                                            blocksToBreak.add(world.getBlockAt(x - 1, y + 1, z));
//                                blocksToBreak.add(world.getBlockAt(x - 2, y + 2, z));

                                        } else if (playerFacing == BlockFace.EAST) {
                                            blocksToBreak.add(world.getBlockAt(x, y - 1, z - 1));
                                            blocksToBreak.add(world.getBlockAt(x, y + 1, z + 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z + 2));

                                        } else if (playerFacing == BlockFace.WEST) {
                                            blocksToBreak.add(world.getBlockAt(x, y - 1, z + 1));
                                            blocksToBreak.add(world.getBlockAt(x, y + 1, z - 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z - 2));
                                        }

                                    } else if (ticks % 12 == 8 && ticks >= START_DELAYS[3]) { // -45 degrees
                                        if (playerFacing == BlockFace.NORTH) {
                                            blocksToBreak.add(world.getBlockAt(x + 1, y - 1, z));
                                            blocksToBreak.add(world.getBlockAt(x - 1, y + 1, z));
//                                blocksToBreak.add(world.getBlockAt(x - 2, y + 2, z));

                                        } else if (playerFacing == BlockFace.SOUTH) {
                                            blocksToBreak.add(world.getBlockAt(x - 1, y - 1, z));
                                            blocksToBreak.add(world.getBlockAt(x + 1, y + 1, z));
//                                blocksToBreak.add(world.getBlockAt(x + 2, y + 2, z));

                                        } else if (playerFacing == BlockFace.EAST) {
                                            blocksToBreak.add(world.getBlockAt(x, y - 1, z + 1));
                                            blocksToBreak.add(world.getBlockAt(x, y + 1, z - 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z - 2));

                                        } else if (playerFacing == BlockFace.WEST) {
                                            blocksToBreak.add(world.getBlockAt(x, y - 1, z - 1));
                                            blocksToBreak.add(world.getBlockAt(x, y + 1, z + 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 2, z + 2));
                                        }

                                    } else if (ticks % 12 == 4 && ticks >= START_DELAYS[4]) { // 15 degrees
                                        if (playerFacing == BlockFace.NORTH) {
                                            blocksToBreak.add(world.getBlockAt(x + 1, y, z));
                                            blocksToBreak.add(world.getBlockAt(x - 1, y, z));
//                                blocksToBreak.add(world.getBlockAt(x - 2, y + 1, z));

                                        } else if (playerFacing == BlockFace.SOUTH) {
                                            blocksToBreak.add(world.getBlockAt(x + 1, y, z));
                                            blocksToBreak.add(world.getBlockAt(x - 1, y, z));
//                                blocksToBreak.add(world.getBlockAt(x + 2, y + 1, z));

                                        } else if (playerFacing == BlockFace.EAST) {
                                            blocksToBreak.add(world.getBlockAt(x, y, z + 1));
                                            blocksToBreak.add(world.getBlockAt(x, y, z - 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 1, z - 2));

                                        } else if (playerFacing == BlockFace.WEST) {
                                            blocksToBreak.add(world.getBlockAt(x, y, z + 1));
                                            blocksToBreak.add(world.getBlockAt(x, y, z - 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 1, z + 2));
                                        }

                                    } else if (ticks % 12 == 10 && ticks >= START_DELAYS[5]) { // -15 degrees
                                        if (playerFacing == BlockFace.NORTH) {
                                            blocksToBreak.add(world.getBlockAt(x - 1, y, z));
                                            blocksToBreak.add(world.getBlockAt(x + 1, y, z));
//                                blocksToBreak.add(world.getBlockAt(x + 2, y + 1, z));

                                        } else if (playerFacing == BlockFace.SOUTH) {
                                            blocksToBreak.add(world.getBlockAt(x - 1, y, z));
                                            blocksToBreak.add(world.getBlockAt(x + 1, y, z));
//                                blocksToBreak.add(world.getBlockAt(x - 2, y + 1, z));

                                        } else if (playerFacing == BlockFace.EAST) {
                                            blocksToBreak.add(world.getBlockAt(x, y, z - 1));
                                            blocksToBreak.add(world.getBlockAt(x, y, z + 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 1, z + 2));

                                        } else if (playerFacing == BlockFace.WEST) {
                                            blocksToBreak.add(world.getBlockAt(x, y, z - 1));
                                            blocksToBreak.add(world.getBlockAt(x, y, z + 1));
//                                blocksToBreak.add(world.getBlockAt(x, y + 1, z - 2));
                                        }
                                    }
                                }

                                for (Block block : blocksToBreak) {
                                    if (!block.getType().isAir()) {
//                                        block.setType(Material.AIR);
                                        block.breakNaturally(true);
                                    }
                                }
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

                ticks++;
                if (ticks == 12 * 20) { // 20 cycles
                    this.cancel();
                    remove();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    protected void remove() {
        for (int i = 0; i < 6; i++) {
            displays[i].remove();
            displayBacksides[i].remove();
        }
    }
}