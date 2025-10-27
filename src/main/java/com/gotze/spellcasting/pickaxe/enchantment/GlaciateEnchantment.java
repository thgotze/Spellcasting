package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GlaciateEnchantment extends Enchantment implements BlockBreakListener {

    private final Map<Block, BlockData> frozenBlocks = new HashMap<>();
    private boolean isActive;

    public GlaciateEnchantment() {
        super(EnchantmentType.GLACIATE);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        BlockData originalBlockData = frozenBlocks.remove(block);
        if (originalBlockData != null) {
            block.setBlockData(originalBlockData);
        }

        if (!isNaturalBreak) return;
        if (this.isActive) return;

        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.99) return;

        this.isActive = true;
        List<Block> blocksToFreeze = BlockUtils.getBlocksInSpherePattern(block, 5, 5, 5);
        blocksToFreeze.removeIf(b -> b.getType().isAir() || b.equals(block) ||
                (!BlockCategories.FILLER_BLOCKS.contains(b.getType()) && !BlockCategories.ORE_BLOCKS.containsKey(b.getType())));
        Collections.shuffle(blocksToFreeze);

        List<Block> blocksToFreezeClone = new ArrayList<>(blocksToFreeze);

        // Freeze up to 9 blocks every tick
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blocksToFreeze.isEmpty()) {
                    isActive = false;
                    cancel();
                }

                for (int i = 0; i < 9 && !blocksToFreeze.isEmpty(); i++) {
                    Block blockToFreeze = blocksToFreeze.removeFirst();
                    frozenBlocks.put(blockToFreeze, blockToFreeze.getBlockData());
                    blockToFreeze.setType(Material.PACKED_ICE);
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0L, 1L);

        // Frozen block return to their previous block type after 15 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blocksToFreezeClone.isEmpty()) {
                    cancel();
                }

//                for (int i = 0; i < 3 && !blocksToFreezeClone.isEmpty(); i++) {
                    Block blockToFreeze = blocksToFreezeClone.removeFirst();
                    if (blockToFreeze.getType() != Material.PACKED_ICE) return;

                    BlockData originalBlockData = frozenBlocks.remove(blockToFreeze);
                    if (originalBlockData != null) {
                        blockToFreeze.setBlockData(originalBlockData);
                    }
//                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 15 * 20L, 5L);
    }
}