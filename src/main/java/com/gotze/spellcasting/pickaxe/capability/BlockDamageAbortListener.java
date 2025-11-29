package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.data.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageAbortEvent;

@FunctionalInterface
public interface BlockDamageAbortListener {
    /**
     * Called when a player stops damaging a block
     * @param player The player that stopped damaging the block
     * @param event The BlockDamageAbortEvent
     * @param pickaxeData The pickaxe data associated with the player
     */
    void onBlockDamageAbort(Player player, BlockDamageAbortEvent event, PickaxeData pickaxeData);
}