package com.gotze.spellcasting.feature;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Set resource pack TODO: currently using clientside resourcepack instead of serverside
//        String resourcePackUrl = "https://download.mc-packs.net/pack/7e609ad3d7d2abf0668002fa98c9d198ab544fe7.zip";
//        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);
//        player.setResourcePack(resourcePackUrl);
    }
}