package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.block.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.*;

public class PhantomQuarryEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker {
    private static final BlockData TINTED_GLASS = Material.TINTED_GLASS.createBlockData();
    private static final long BASE_COOLDOWN = 15;

    private boolean isProcessingQuarry;
    private Block centerBlock;
    private long cooldown;
    private BukkitRunnable timeoutTask;
    private BlockFace blockFace;

    // public >>> other enchants and abilities might need to know what blocks have been marked
    public boolean isActive;
    public final Map<Block, BlockDisplay> markedBlocks = new HashMap<>();

    public PhantomQuarryEnchantment() {
        super(EnchantmentType.PHANTOM_QUARRY);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (this.isProcessingQuarry) return;

        // ***
        // First time
        // ***

        if (!this.isActive) {
            if (!isNaturalBreak) return;
            if (System.currentTimeMillis() < cooldown) return;

            this.centerBlock = block;
            List<Block> cornerBlocks = new ArrayList<>();
            cornerBlocks.addAll(BlockUtils.getPositiveDiagonalBlocks(block, blockFace, 2));
            cornerBlocks.addAll(BlockUtils.getNegativeDiagonalBlocks(block, blockFace, 2));
            cornerBlocks.removeIf(b -> b.getType().isEmpty() ||
                    (!BlockCategories.ORE_BLOCKS.containsKey(b.getType()) && !BlockCategories.FILLER_BLOCKS.contains(b.getType())));

            // If less than 3 corners were found then don't activate the enchantment
            if (cornerBlocks.size() < 3) {
                reset();
                return;
            }

            this.isActive = true;
            this.cooldown = System.currentTimeMillis() + BASE_COOLDOWN;
            World world = player.getWorld();

            for (Block cornerBlock : cornerBlocks) {
                Location displayLocation = cornerBlock.getLocation().add(0.0625f, 0.0625f, 0.0625f);
                BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
                blockDisplay.setTransformationMatrix(new Matrix4f().scale(0.875f, 0.875f, 0.875f));

                blockDisplay.setBlock(TINTED_GLASS);
                blockDisplay.setGlowing(true);
                blockDisplay.setGlowColorOverride(Color.YELLOW);

                blockDisplay.setVisibleByDefault(false);
                player.showEntity(JavaPlugin.getPlugin(Spellcasting.class), blockDisplay);

                markedBlocks.put(cornerBlock, blockDisplay);
            }

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
            timeoutTask.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), BASE_COOLDOWN * 20L);
            return;
        }

        // ***
        // Subsequent times
        // ***

        markedBlocks.entrySet().removeIf(entry -> {
            if (entry.getKey().equals(block)) {
                BlockDisplay display = entry.getValue();
                display.remove();
                return true;
            }
            return false;
        });

        if (markedBlocks.isEmpty()) {
            this.isProcessingQuarry = true;
            List<Block> blocksToBreak = switch (blockFace) {
                case NORTH, SOUTH -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 5, 1);
                case EAST, WEST -> BlockUtils.getBlocksInSquarePattern(centerBlock, 1, 5, 5);
                case UP, DOWN -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 1, 5);
                default -> throw new IllegalStateException();
            };
            blocksToBreak.removeIf(b -> b.getType().isEmpty());
            Collections.shuffle(blocksToBreak);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (blocksToBreak.isEmpty()) {
                        reset();
                        cancel();
                    }

                    // Break up to 3 blocks every tick
                    for (int i = 0; i < 3 && !blocksToBreak.isEmpty(); i++) {
                        breakBlock(player, blocksToBreak.removeFirst(), pickaxeData, false);
                    }
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0L, 1L);
        }
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.blockFace = event.getBlockFace();
    }

    private void reset() {
        isActive = false;
        isProcessingQuarry = false;
        centerBlock = null;
        blockFace = null;
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
        for (BlockDisplay display : markedBlocks.values()) {
            if (display.isValid()) {
                display.remove();
            }
        }
        markedBlocks.clear();
    }
}