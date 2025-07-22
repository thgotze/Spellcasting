package com.gotze.magicParticles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

//        String resourcePackUrl = "https://download.mc-packs.net/pack/d7322fd454289b5ff6156b445ff738b3213aed57.zip"; // 32
        String resourcePackUrl = "https://download.mc-packs.net/pack/c3bb6e89168793994f323d6f426535e139381c5c.zip"; // 27

        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);

        player.setResourcePack(resourcePackUrl); // TODO: Use paper API
    }
}