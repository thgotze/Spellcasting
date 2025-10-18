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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlaciateEnchantment extends Enchantment implements BlockBreakListener {

    private boolean isActive;
    private final Map<Block, BlockData> affectedBlocks = new HashMap<>();

    public GlaciateEnchantment() {
        super(EnchantmentType.GLACIATE);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        // ---------------
        // First time
        // ---------------
        if (!this.isActive) {
            this.isActive = true;

            List<Block> blocksToGlaciate = BlockUtils.getBlocksInSpherePattern(block, 5, 5, 5);
            blocksToGlaciate.removeIf(b -> b.getType().isAir() || b.equals(block));
            Collections.shuffle(blocksToGlaciate);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (blocksToGlaciate.isEmpty()) {
                        player.sendMessage("Glaciate froze " + affectedBlocks.size() + " blocks");
                        cancel();
                    }

                    // Convert up to 3 blocks to ice every tick
                    for (int i = 0; i < 9 && !blocksToGlaciate.isEmpty(); i++) {
                        Block blockToGlaciate = blocksToGlaciate.removeFirst();
                        affectedBlocks.put(blockToGlaciate, blockToGlaciate.getBlockData());
                        blockToGlaciate.setType(BlockCategories.ORE_BLOCKS.containsKey(blockToGlaciate.getType()) ? Material.BLUE_ICE : Material.PACKED_ICE);
                    }
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0L, 1L);
        }

        // ---------------
        // Subsequent times
        // ---------------
        BlockData originalData = affectedBlocks.remove(block);
        if (originalData == null) return;
        block.setBlockData(originalData);

        if (affectedBlocks.isEmpty()) {
            this.isActive = false;
        }
    }
}