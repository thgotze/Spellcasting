package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.data.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

@FunctionalInterface
public interface BlockDamageListener {
    /**
     * Called when a player damages a block
     * @param player The player breaking the block
     * @param event The BlockDamageEvent
     * @param pickaxeData The pickaxe data
     */
    void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData);
}