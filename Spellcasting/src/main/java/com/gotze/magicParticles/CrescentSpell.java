package com.gotze.magicParticles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class CrescentSpell extends AbstractSpell {

    //    private static final String[] CLOCKWISE_SPRITES = {
    private final String[] CLOCKWISE_SPRITES = {"\uE100", "", "\uE042", "\uE043", "\uE044", "\uE045", "", "", "", "", "", ""};
//     private final String[] CLOCKWISE_SPRITES = {
//                "\uE040", "\uE041", "\uE042", "\uE043", "\uE044", "\uE045",
//                "\uE046", "\uE047", "\uE048", "\uE049", "\uE04A", "\uE04B"
//        };

    //    private static final String[] COUNTER_CLOCKWISE_SPRITES = {
    private final String[] COUNTER_CLOCKWISE_SPRITES = {"", "", "\uE032", "\uE033", "\uE034", "\uE035", "", "", "", "", "", ""};
//    private final String[] COUNTER_CLOCKWISE_SPRITES = {
//            "\uE030", "\uE031", "\uE032", "\uE033", "\uE034", "\uE035",
//            "\uE036", "\uE037", "\uE038", "\uE039", "\uE03A", "\uE03B"
//    };

    //    private static final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};
    private final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};
    //    private static final int[] ANIMATION_DELAYS = {0, 6, 2, 8, 4, 10};
    private final int[] ANIMATION_DELAYS = {0, 6, 2, 8, 4, 10};
    //    private static final int[] START_DELAYS = {
    private final int[] START_DELAYS = {0, 42, 74, 116, 148, 190};
    // 0 * 12 + 0, 3 * 12 + 6,
    // 6 * 12 + 2, 9 * 12 + 8,
    // 12 * 12 + 4, 15 * 12 + 10

    // 0 -> 42
    // 74 -> 116
    // 148 -> 190

    private final TextDisplay[] displays = new TextDisplay[6];
    //    private final TextDisplay[] displays = new TextDisplay[1];
    private final TextDisplay[] displayBacksides = new TextDisplay[6];
//    private final TextDisplay[] displayBacksides = new TextDisplay[1];

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
                            if (ticks % 12 == ANIMATION_DELAYS[i]) { // 75
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
                        TextDisplayBuilder baseDisplay = new TextDisplayBuilder(spawnLocation)
                                .scale(17, 27, 0f)
                                .rotateX(90f);

//                    for (int i = 0; i < 1; i++) {
                        for (int i = 0; i < 6; i++) {
                            // Create frontside crescent

                            displays[i] = baseDisplay.copy()
                                    .rotateZ(DISPLAY_ROTATIONS[i])
                                    .rotateX(-0.5f)
                                    .build();
                            displayBacksides[i] = baseDisplay.copy()
                                    .rotateZ(DISPLAY_ROTATIONS[i])
                                    .rotateZ(180f)
                                    .rotateX(-0.5f)
                                    .build();
                        }
                    }
                }


                // Teleport only one of the 6 pairs every tick
                if (ticks % 2 == 0) {
                    if (ticks % 12 == 0) { // 75
                        displays[0].teleport(spawnLocation);
                        displayBacksides[0].teleport(spawnLocation);

                    } else if (ticks % 12 == 6) { // -75 degrees
                        displays[1].teleport(spawnLocation);
                        displayBacksides[1].teleport(spawnLocation);

                    } else if (ticks % 12 == 2) { // 45 degrees
                        displays[2].teleport(spawnLocation);
                        displayBacksides[2].teleport(spawnLocation);

                    } else if (ticks % 12 == 8) { // -45 degrees
                        displays[3].teleport(spawnLocation);
                        displayBacksides[3].teleport(spawnLocation);

                    } else if (ticks % 12 == 4) { // 15 degrees
                        displays[4].teleport(spawnLocation);
                        displayBacksides[4].teleport(spawnLocation);

                    } else if (ticks % 12 == 10) { // -15 degrees
                        displays[5].teleport(spawnLocation);
                        displayBacksides[5].teleport(spawnLocation);
                    }
                }

                // Change sprite
                for (int i = 0; i < 6; i++) {
                    if (ticks >= START_DELAYS[i]) {
                        int spriteTick = (ticks - START_DELAYS[i]) % 12;
                        displays[i].text(Component.text(i % 2 == 0 ? CLOCKWISE_SPRITES[spriteTick] : COUNTER_CLOCKWISE_SPRITES[spriteTick],
                                TextColor.color(255, 255, 255)
                        ));
                        displayBacksides[i].text(Component.text(i % 2 == 0 ? COUNTER_CLOCKWISE_SPRITES[spriteTick] : CLOCKWISE_SPRITES[spriteTick],
                                TextColor.color(255, 255, 255)
                        ));
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
//        for (int i = 0; i < 1; i++) {
        for (int i = 0; i < 6; i++) {
            displays[i].remove();
            displayBacksides[i].remove();
        }
    }
}