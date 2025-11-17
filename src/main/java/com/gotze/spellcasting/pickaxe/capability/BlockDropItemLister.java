package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.data.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;

@FunctionalInterface
public interface BlockDropItemLister {
    /**
     * Called when items are about to be dropped from a broken block
     * @param player The player who broke the block
     * @param event The BlockDropItemEvent
     * @param pickaxeData The pickaxe data associated with the player
     */
    void onBlockDropItem(Player player, BlockDropItemEvent event, PickaxeData pickaxeData);
}