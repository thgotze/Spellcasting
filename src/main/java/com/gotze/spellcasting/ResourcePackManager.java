package com.gotze.spellcasting;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Set resource pack TODO: currently using clientside resourcepack instead of serverside
        Player player = event.getPlayer();
        String resourcePackUrl = "https://download.mc-packs.net/pack/1596d8f15753ebb16f6870979ad427df8deb880a.zip";
        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);
        player.setResourcePack(resourcePackUrl);
    }
}