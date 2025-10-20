package com.gotze.spellcasting;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestingBrewingStandListener implements Listener {

    @EventHandler
    public void openInventory(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            new TestingBrewingStandMenu(player);
        }
    }
}