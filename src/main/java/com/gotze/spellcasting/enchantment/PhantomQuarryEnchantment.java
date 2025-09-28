package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockDamageAware;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.*;

public class PhantomQuarryEnchantment extends Enchantment implements BlockDamageAware {
    private static final BlockData TINTED_GLASS = Material.TINTED_GLASS.createBlockData();
    private static final long BASE_COOLDOWN = 1_000; // 15 seconds

    private boolean isActive;
    private Block originBlock;
    private final Map<Block, BlockDisplay> markedBlocks = new HashMap<>();
    private long cooldown;
    private BukkitRunnable timeoutTask;
    private BlockFace blockFace;

    public PhantomQuarryEnchantment() {
        super(EnchantmentType.PHANTOM_QUARRY);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.blockFace = event.getBlockFace();
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        // First time
        if (!this.isActive) {
            if (System.currentTimeMillis() < cooldown) return;
            this.originBlock = event.getBlock();

            List<Block> cornerBlocks = new ArrayList<>();
            if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                // Flat on the ground (X/Z plane)
                cornerBlocks.add(originBlock.getRelative(2, 0, 2));
                cornerBlocks.add(originBlock.getRelative(2, 0, -2));
                cornerBlocks.add(originBlock.getRelative(-2, 0, 2));
                cornerBlocks.add(originBlock.getRelative(-2, 0, -2));
            } else if (blockFace == BlockFace.EAST || blockFace == BlockFace.WEST) {
                // E/W vertical wall (X/Y plane)
                cornerBlocks.add(originBlock.getRelative(0, 2, 2));
                cornerBlocks.add(originBlock.getRelative(0, -2, 2));
                cornerBlocks.add(originBlock.getRelative(0, 2, -2));
                cornerBlocks.add(originBlock.getRelative(0, -2, -2));
            } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
                // N/S vertical wall (Z/Y plane)
                cornerBlocks.add(originBlock.getRelative(2, 2, 0));
                cornerBlocks.add(originBlock.getRelative(2, -2, 0));
                cornerBlocks.add(originBlock.getRelative(-2, 2, 0));
                cornerBlocks.add(originBlock.getRelative(-2, -2, 0));
            }

            World world = originBlock.getWorld();
            for (Block cornerBlock : cornerBlocks) {
                if (cornerBlock.getType() != Material.AIR) {
                    Location blockLocation = cornerBlock.getLocation().add(0.0625f, 0.0625f, 0.0625f);
                    BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(blockLocation, EntityType.BLOCK_DISPLAY);
                    blockDisplay.setBlock(TINTED_GLASS);
                    blockDisplay.setGlowing(true);
                    blockDisplay.setGlowColorOverride(Color.YELLOW);
                    blockDisplay.setBrightness(new Display.Brightness(15, 15));
                    blockDisplay.setTransformationMatrix(new Matrix4f().scale(0.875f, 0.875f, 0.875f));
                    blockDisplay.setVisibleByDefault(false);
                    player.showEntity(JavaPlugin.getPlugin(Spellcasting.class), blockDisplay);

                    markedBlocks.put(cornerBlock, blockDisplay);
                }
            }

            // If none of the 4 corners were created then don't activate the enchantment
            if (markedBlocks.isEmpty()) {
                reset();
                return;
            }
            isActive = true;
            cooldown = System.currentTimeMillis() + BASE_COOLDOWN;

            // The player has 15 seconds to break the corner blocks
            timeoutTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (isActive) {
                        reset();
                        player.sendMessage("Your Phantom Quarry expired");
                    }
                }
            };
            timeoutTask.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 15 * 20L);
            return;
        }

        // Subsequent times
        Iterator<Map.Entry<Block, BlockDisplay>> iterator = markedBlocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Block, BlockDisplay> entry = iterator.next();

            if (event.getBlock().equals(entry.getKey())) {
                BlockDisplay display = entry.getValue();
                if (display.isValid()) {
                    display.remove();
                }
                iterator.remove();
            }
        }

        if (markedBlocks.isEmpty()) {
            if (originBlock == null) return;

            // Use the stored broken face to determine pattern
            List<Block> blocksToBreak;
            if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                // Horizontal pattern: 5x1x5
                blocksToBreak = BlockUtils.getBlocksInSquarePattern(originBlock, 5, 1, 5);
            } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
                // Vertical wall facing North/South: 5x5x1
                blocksToBreak = BlockUtils.getBlocksInSquarePattern(originBlock, 5, 5, 1);
            } else {
                // Vertical wall facing East/West: 1x5x5
                blocksToBreak = BlockUtils.getBlocksInSquarePattern(originBlock, 1, 5, 5);
            }
            blocksToBreak.removeIf(block -> block.getType() == Material.AIR);
            Collections.shuffle(blocksToBreak);
            pickaxeData.addBlocksBroken(blocksToBreak.size() - 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (blocksToBreak.isEmpty()) {
                        reset();
                        cancel();
                    }

                    // Break up to 3 blocks every tick
                    for (int i = 0; i < 3 && !blocksToBreak.isEmpty(); i++) {
                        Block blockToBreak = blocksToBreak.removeFirst();
                        blockToBreak.breakNaturally(true);
                    }
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0L, 1L);
        }
    }

    private void reset() {
        for (BlockDisplay display : markedBlocks.values()) {
            if (display.isValid()) {
                display.remove();
            }
        }
        isActive = false;
        originBlock = null;
        blockFace = null;
        markedBlocks.clear();
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
    }
}