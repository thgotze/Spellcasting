package com.gotze.spellcasting.pickaxe.enchantment;

import com.destroystokyo.paper.ParticleBuilder;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PhantomQuarryEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker {
    private static final BlockData PHANTOM_QUARRY_TINTED_GLASS = BlockType.TINTED_GLASS.createBlockData();
    private static final ParticleBuilder PHANTOM_QUARRY_PARTICLE = new ParticleBuilder(Particle.DUST)
            .count(0)
            .data(new Particle.DustOptions(Color.FUCHSIA, 1.0f))
            .offset(0, 0, 0)
            .extra(0);
    private static final long BASE_COOLDOWN = 20; // 1 second
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
            // 0.25% activation chance
//            if (ThreadLocalRandom.current().nextDouble() > 0.0025) return;

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
                blockDisplay.setGlowColorOverride(Color.FUCHSIA);

                blockDisplay.setVisibleByDefault(false);
                player.showEntity(Spellcasting.getPlugin(), blockDisplay);

                markedCornerBlocks.put(cornerBlock, blockDisplay);

                // Particle outline
                List<Location> particlePoints = BlockUtils.getBlockOutlineForParticles(cornerBlock.getLocation(), 0.10);
                for (Location point : particlePoints) {
                    PHANTOM_QUARRY_PARTICLE.clone()
                            .location(point)
                            .receivers(player)
                            .spawn();
                }

                // Particle line
                Location playerEyeLocation = player.getEyeLocation().clone();
                playerEyeLocation.add(playerEyeLocation.getDirection().multiply(1)); // offset forward a bit

                Location target = cornerBlock.getLocation().toCenterLocation();

                Vector direction = target.toVector().subtract(playerEyeLocation.toVector());
                double distance = direction.length();
                direction.normalize();

                double step = 0.1; // distance between particles
                int particles = (int) (distance / step);

                for (int i = 0; i < particles; i++) {
                    Vector point = playerEyeLocation.toVector().add(direction.clone().multiply(i * step));
                    Location particleLocation = point.toLocation(player.getWorld());
                    PHANTOM_QUARRY_PARTICLE.clone()
                            .location(particleLocation)
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
        markedCornerBlocks.entrySet().removeIf(entry -> {
            if (entry.getKey().equals(block)) {
                BlockDisplay display = entry.getValue();
                display.remove();
                return true;
            }
            return false;
        });

        if (markedCornerBlocks.isEmpty()) {
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