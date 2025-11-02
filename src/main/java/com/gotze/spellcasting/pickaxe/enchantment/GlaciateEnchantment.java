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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

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

        // 0.25% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.0025) return;
        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));

        this.isActive = true;
        List<Block> blocksToFreeze = BlockUtils.getBlocksInSpherePattern(block, 5, 5, 5);
        blocksToFreeze.remove(block);
        blocksToFreeze.removeIf(candidate -> !BlockCategories.FILLER_BLOCKS.contains(candidate.getType()) &&
                !BlockCategories.ORE_BLOCKS.containsKey(candidate.getType()));
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
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);

        // Frozen block return to their previous block type after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blocksToFreezeClone.isEmpty()) {
                    cancel();
                    return;
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
        }.runTaskTimer(Spellcasting.getPlugin(), 10 * 20L, 5L);
    }
}