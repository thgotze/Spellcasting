package com.gotze.spellcasting.util;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreakAware {
    void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData);
}