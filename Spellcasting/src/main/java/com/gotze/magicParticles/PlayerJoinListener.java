package com.gotze.magicParticles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        String resourcePackUrl = "https://download.mc-packs.net/pack/7e609ad3d7d2abf0668002fa98c9d198ab544fe7.zip"; // 33
//        String resourcePackUrl = "https://download.mc-packs.net/pack/c3bb6e89168793994f323d6f426535e139381c5c.zip"; // 27

        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);

        player.setResourcePack(resourcePackUrl); // TODO: Use paper API
    }
}