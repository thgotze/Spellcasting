package com.gotze.spellcasting.util.block;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

@FunctionalInterface
public interface BlockDamageListener {
    void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData);
}