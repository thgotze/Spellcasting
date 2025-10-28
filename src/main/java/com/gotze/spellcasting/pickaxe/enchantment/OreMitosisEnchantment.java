package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OreMitosisEnchantment extends Enchantment implements BlockBreakListener {

    public OreMitosisEnchantment() {
        super(EnchantmentType.ORE_MITOSIS);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (!BlockCategories.ORE_BLOCKS.containsKey(block.getType())) return;

        List<Block> candidateBlocks = BlockUtils.getBlocksInSpherePattern(block, 3, 3, 3);
        candidateBlocks.removeIf(candidate -> !BlockCategories.FILLER_BLOCKS.contains(candidate.getType()));

        if (candidateBlocks.isEmpty()) return;

        Block chosenBlock1 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
        Block chosenBlock2 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));

        chosenBlock1.setType(block.getType());
        chosenBlock2.setType(block.getType());
    }
}