package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScatterEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener {

    private BlockFace blockFace;

    public ScatterEnchantment() {
        super(EnchantmentType.SCATTER);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        Material blockType = block.getType();
        if (!BlockCategories.ORE_BLOCKS.containsKey(blockType)) return;

        BlockFace oppositeFace = blockFace.getOppositeFace();

        List<Block> candidateBlocks = new ArrayList<>();
        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            candidateBlocks.addAll(BlockUtils.getBlocksInCrossPattern(block.getRelative(oppositeFace, 1), 3, 1, 3));
            candidateBlocks.addAll(BlockUtils.getBlocksInSquarePattern(block.getRelative(oppositeFace, 2), 3, 1, 3));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 3), 5, 1, 5));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 4), 5, 1, 5));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 5), 7, 1, 7));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 6), 7, 1, 7));
        } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
            candidateBlocks.addAll(BlockUtils.getBlocksInCrossPattern(block.getRelative(oppositeFace, 1), 3, 3, 1));
            candidateBlocks.addAll(BlockUtils.getBlocksInSquarePattern(block.getRelative(oppositeFace, 2), 3, 3, 1));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 3), 5, 5, 1));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 4), 5, 5, 1));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 5), 7, 7, 1));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 6), 7, 7, 1));
        } else {
            candidateBlocks.addAll(BlockUtils.getBlocksInCrossPattern(block.getRelative(oppositeFace, 1), 1, 3, 3));
            candidateBlocks.addAll(BlockUtils.getBlocksInSquarePattern(block.getRelative(oppositeFace, 2), 1, 3, 3));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 3), 1, 5, 5));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 4), 1, 5, 5));
            candidateBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(block.getRelative(oppositeFace, 5), 1, 7, 7));
            candidateBlocks.addAll(BlockUtils.getBlocksInSpherePattern(block.getRelative(oppositeFace, 6), 1, 7, 7));
        }

        int maxAmountOfBlocksToSpread = getLevel() * 5 + ThreadLocalRandom.current().nextInt(3);
        int successfulSpreadBlocks = 0;
        for (int i = 0; i < maxAmountOfBlocksToSpread; i++) {
            Block spreadableBlock = candidateBlocks.removeFirst();
            if (BlockCategories.FILLER_BLOCKS.contains(spreadableBlock.getType())) {
                spreadableBlock.setType(blockType);
                successfulSpreadBlocks++;
            }
        }
        player.sendMessage("Scattered " + successfulSpreadBlocks + " x " + StringUtils.toTitleCase(blockType.toString()) + " to nearby blocks");
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        this.blockFace = event.getBlockFace();
    }
}