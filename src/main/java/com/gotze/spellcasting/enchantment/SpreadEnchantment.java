package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpreadEnchantment extends Enchantment implements BlockDamageAware, BlockBreakAware {

    private boolean isActive;
    private BlockFace blockFace;

    public SpreadEnchantment() {
        super(EnchantmentType.SPREAD);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        this.blockFace = event.getBlockFace();
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        Block originBlock = event.getBlock();
        Material originBlockType = originBlock.getType();
        if (!BlockCategories.ORE_BLOCKS.containsKey(originBlockType)) return;

        this.isActive = true;
        BlockFace oppositeFace = blockFace.getOppositeFace();

        List<Block> spreadableBlocks = new ArrayList<>();
        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            spreadableBlocks.addAll(BlockUtils.getBlocksInCrossPattern(originBlock.getRelative(oppositeFace, 1), 3, 1, 3));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSquarePattern(originBlock.getRelative(oppositeFace, 2), 3, 1, 3));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 3), 5, 1, 5));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 4), 5, 1, 5));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 5), 7, 1, 7));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 6), 7, 1, 7));
        } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
            spreadableBlocks.addAll(BlockUtils.getBlocksInCrossPattern(originBlock.getRelative(oppositeFace, 1), 3, 3, 1));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSquarePattern(originBlock.getRelative(oppositeFace, 2), 3, 3, 1));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 3), 5, 5, 1));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 4), 5, 5, 1));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 5), 7, 7, 1));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 6), 7, 7, 1));
        } else {
            spreadableBlocks.addAll(BlockUtils.getBlocksInCrossPattern(originBlock.getRelative(oppositeFace, 1), 1, 3, 3));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSquarePattern(originBlock.getRelative(oppositeFace, 2), 1, 3, 3));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 3), 1, 5, 5));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 4), 1, 5, 5));
            spreadableBlocks.addAll(BlockUtils.getBlocksInDiamondPattern(originBlock.getRelative(oppositeFace, 5), 1, 7, 7));
            spreadableBlocks.addAll(BlockUtils.getBlocksInSpherePattern(originBlock.getRelative(oppositeFace, 6), 1, 7, 7));
        }
        Collections.shuffle(spreadableBlocks);

        int amountToSpread = getLevel() * 5 + ThreadLocalRandom.current().nextInt(3);
        int blocksSpread = 0;
        for (int i = 0; i < amountToSpread; i++) {
            Block spreadableBlock = spreadableBlocks.removeFirst();
            if (BlockCategories.FILLER_BLOCKS.contains(spreadableBlock.getType())) {
                spreadableBlock.setType(originBlockType);
                blocksSpread++;
            }
        }
        player.sendMessage("You spread " + StringUtils.toTitleCase(originBlockType.toString()) + " to " + blocksSpread + "/" + amountToSpread + " blocks");
        reset();
    }

    private void reset() {
        blockFace = null;
        isActive = false;
    }
}