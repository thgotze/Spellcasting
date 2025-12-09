package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.gotze.spellcasting.Spellcasting.plugin;

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
            return;
        }

        if (!isNaturalBreak) return;
        if (this.isActive) return;

        if (ThreadLocalRandom.current().nextDouble() > (0.0025 + getLevel() * 0.00125)) return; // 0.25%, 0.375%, 0.5%, 0.625%, 0.75%

        this.isActive = true;
        List<Block> blocksToFreeze = BlockUtils.getBlocksInSpherePattern(block, 5, 5, 5);
        blocksToFreeze.remove(block);
        Collections.shuffle(blocksToFreeze);

        List<Block> frozenInThisCycle = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blocksToFreeze.isEmpty()) {
                    isActive = false;
                    cancel();
                }

                // Freeze up to 9 blocks every tick
                for (int i = 0; i < 9 && !blocksToFreeze.isEmpty(); i++) {
                    Block blockToFreeze = blocksToFreeze.removeFirst();

                    // Ore blocks become blue ice
                    if (BlockCategories.ORE_BLOCKS.containsKey(blockToFreeze.getType())) {
                        frozenBlocks.put(blockToFreeze, blockToFreeze.getBlockData());
                        blockToFreeze.setType(Material.BLUE_ICE);
                        frozenInThisCycle.add(blockToFreeze);

                        // Filler blocks become packed ice
                    } else if (BlockCategories.FILLER_BLOCKS.contains(blockToFreeze.getType())) {
                        frozenBlocks.put(blockToFreeze, blockToFreeze.getBlockData());
                        blockToFreeze.setType(Material.PACKED_ICE);
                        frozenInThisCycle.add(blockToFreeze);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (frozenInThisCycle.isEmpty()) {
                    cancel();
                    return;
                }

                // Thaw frozen blocks every 1/4th second after 10 seconds
                Block blockToThaw = frozenInThisCycle.removeFirst();
                BlockData originalBlockData = frozenBlocks.remove(blockToThaw);
                if (originalBlockData != null) {
                    blockToThaw.setBlockData(originalBlockData);
                }
            }
        }.runTaskTimer(plugin, 200L, 5L);
    }
}
