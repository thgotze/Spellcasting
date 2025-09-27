package com.gotze.spellcasting.util;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

public interface BlockDamageAware {
    void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData);
}