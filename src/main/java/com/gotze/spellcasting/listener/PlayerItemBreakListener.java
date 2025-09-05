package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.data.PlayerPickaxeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemBreakListener implements Listener {

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();

        ItemStack brokenItem = event.getBrokenItem();
        if (brokenItem.getType() == PlayerPickaxeService.getPlayerPickaxeData(player).getType()) {
            player.give(PlayerPickaxeService.getPlayerPickaxe(player));
        }
    }
}
