package com.gotze.spellcasting.pickaxe.enchantment;

import com.destroystokyo.paper.ParticleBuilder;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PhantomQuarryEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker {

    private static final BlockData PHANTOM_QUARRY_TINTED_GLASS = BlockType.TINTED_GLASS.createBlockData();
    private static final ParticleBuilder PHANTOM_QUARRY_PARTICLE = Particle.DUST_COLOR_TRANSITION.builder()
            .count(1)
            .colorTransition(Color.ORANGE, Color.WHITE);
    private static final ParticleBuilder PHANTOM_QUARRY_TRAIL_PARTICLE = Particle.TRAIL.builder()
            .offset(0.5, 0.5, 0.5)
            .count(1);
    private static final long BASE_COOLDOWN = 1000; // 1 second
    private static final long TIMEOUT_TASK_LENGTH = 300; // 15 seconds

    private boolean isActive;
    private boolean isProcessingQuarry;
    private Block centerBlock;
    private long cooldown;
    private BukkitRunnable timeoutTask;
    private BlockFace blockFace;
    private final Map<Block, BlockDisplay> markedCornerBlocks = new HashMap<>();

    public PhantomQuarryEnchantment() {
        super(EnchantmentType.PHANTOM_QUARRY);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (this.isProcessingQuarry) return;
        // ---------------
        // First time
        // ---------------
        if (!this.isActive) {
            if (!isNaturalBreak) return;
            if (System.currentTimeMillis() < cooldown) return;

            if (ThreadLocalRandom.current().nextDouble() > (0.01 + getLevel() * 0.001)) return; // 1%, 2%, 3%, 4%, 5%

            this.centerBlock = block;
            List<Block> cornerBlocks = new ArrayList<>();
            cornerBlocks.addAll(BlockUtils.getPositiveDiagonalBlocks(block, blockFace, 2));
            cornerBlocks.addAll(BlockUtils.getNegativeDiagonalBlocks(block, blockFace, 2));
            cornerBlocks.removeIf(cornerBlock -> !BlockCategories.ORE_BLOCKS.containsKey(cornerBlock.getType())
                    && !BlockCategories.FILLER_BLOCKS.contains(cornerBlock.getType()));

            // If less than 3 corners were found, then don't activate the enchantment
            if (cornerBlocks.size() < 3) {
                reset();
                return;
            }

            this.isActive = true;
            this.cooldown = System.currentTimeMillis() + BASE_COOLDOWN;
            World world = player.getWorld();

            for (Block cornerBlock : cornerBlocks) {
                Location displayLocation = cornerBlock.getLocation().add(0.001953125f, 0.001953125f, 0.001953125f);
                BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
                blockDisplay.setTransformationMatrix(new Matrix4f().scale(0.9921875f, 0.9921875f, 0.9921875f));

                blockDisplay.setBlock(PHANTOM_QUARRY_TINTED_GLASS);
                blockDisplay.setGlowing(true);
                blockDisplay.setGlowColorOverride(Color.ORANGE);

                blockDisplay.setVisibleByDefault(false);
                player.showEntity(Spellcasting.getPlugin(), blockDisplay);

                markedCornerBlocks.put(cornerBlock, blockDisplay);

                // Particle outline
                for (Location particlePoint : BlockUtils.getBlockOutlineForParticles(cornerBlock.getLocation(), 0.10)) {
                    PHANTOM_QUARRY_PARTICLE.clone()
                            .location(particlePoint)
                            .receivers(player)
                            .spawn();

                    PHANTOM_QUARRY_TRAIL_PARTICLE.clone()
                            .data(new Particle.Trail(particlePoint, Color.ORANGE, 30))
                            .location(particlePoint)
                            .receivers(player)
                            .spawn();
                }
            }

            // The player has 15 seconds to break the corner blocks
            timeoutTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (isActive) {
                        reset();
                        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" expired", RED)));
                    }
                }
            };
            timeoutTask.runTaskLater(Spellcasting.getPlugin(), TIMEOUT_TASK_LENGTH);
            return;
        }

        // ---------------
        // Subsequent times
        // ---------------
        BlockDisplay display = markedCornerBlocks.remove(block);
        if (display != null) {
            display.remove();

            player.playSound(block.getLocation(), Sound.BLOCK_SCULK_CATALYST_HIT, 1.0f, 1.0f);

            for (Location particlePoint : BlockUtils.getBlockOutlineForParticles(block.getLocation(), 0.10)) {
                PHANTOM_QUARRY_PARTICLE.clone()
                        .location(particlePoint)
                        .receivers(player)
                        .spawn();
            }
        }

        if (markedCornerBlocks.isEmpty()) {
            player.playSound(centerBlock.getLocation(), Sound.BLOCK_SCULK_PLACE, 1.0f,1.0f);
            player.playSound(centerBlock.getLocation(), Sound.ENTITY_PHANTOM_BITE, 1.0f,0.75f);
            this.isProcessingQuarry = true;
            List<Block> blocksToBreak = switch (blockFace) {
                case NORTH, SOUTH -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 5, 1);
                case EAST, WEST -> BlockUtils.getBlocksInSquarePattern(centerBlock, 1, 5, 5);
                case UP, DOWN -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 1, 5);
                default -> null;
            };
            if (blocksToBreak == null) return;
            blocksToBreak.removeIf(blockToBreak -> blockToBreak.getType().isAir());
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
                        breakBlock(player, blocksToBreak.removeFirst(), pickaxeData);
                    }
                }
            }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
        }
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (this.isActive) return;
        if (this.isProcessingQuarry) return;
        this.blockFace = event.getBlockFace();
    }

    private void reset() {
        isActive = false;
        isProcessingQuarry = false;
        centerBlock = null;
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
        for (BlockDisplay display : markedCornerBlocks.values()) {
            if (display.isValid()) {
                display.remove();
            }
        }
        markedCornerBlocks.clear();
    }
}
