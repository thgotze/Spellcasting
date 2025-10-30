package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;

public class MitosisEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener {

    private BlockFace blockFace;

    public MitosisEnchantment() {
        super(EnchantmentType.MITOSIS);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        this.blockFace = event.getBlockFace();
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (!BlockCategories.ORE_BLOCKS.containsKey(block.getType())) return;
        // 5% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;

        List<Block> candidateBlocks = switch (blockFace) {
            case NORTH, SOUTH -> BlockUtils.getBlocksInSquarePattern(block, 3, 3, 1);
            case EAST, WEST -> BlockUtils.getBlocksInSquarePattern(block, 1, 3, 3);
            case UP, DOWN -> BlockUtils.getBlocksInSquarePattern(block, 3, 1, 3);
            default -> null;
        };
        if (candidateBlocks == null) return;
        candidateBlocks.removeIf(candidate -> !BlockCategories.FILLER_BLOCKS.contains(candidate.getType()));
        if (candidateBlocks.isEmpty()) return;

        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));

        Block chosenBlock1 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
        chosenBlock1.setType(block.getType());

        if (candidateBlocks.isEmpty()) return;

        Block chosenBlock2 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
        chosenBlock2.setType(block.getType());
    }
}