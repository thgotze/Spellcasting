package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.data.PickaxeData;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.List;

@FunctionalInterface
public interface BlockDropItemListener {
    /**
     * Called when items are about to be dropped from a broken block
     * @param player The player who broke the block
     * @param blockState The state of the block that was broken
     * @param droppedItems The list of items that will be dropped
     * @param pickaxeData The pickaxe data associated with the player
     */
    void onBlockDropItem(Player player, BlockState blockState, List<Item> droppedItems, PickaxeData pickaxeData);
}