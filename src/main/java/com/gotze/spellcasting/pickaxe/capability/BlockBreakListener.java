package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface BlockBreakListener {
    /**
     * Called when a block is about to be broken (programmatically or naturally)
     * @param player The player breaking the block
     * @param block The block being broken
     * @param pickaxeData The pickaxe data
     * @param isNaturalBreak True if from BlockBreakEvent, false if programmatic
     */
    void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak);
}