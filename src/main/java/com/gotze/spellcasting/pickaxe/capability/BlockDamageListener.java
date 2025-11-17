package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.data.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

@FunctionalInterface
public interface BlockDamageListener {
    /**
     * Called when a player damages a block
     * @param player The player damaging the block
     * @param event The BlockDamageEvent
     * @param pickaxeData The pickaxe data associated with the player
     */
    void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData);
}