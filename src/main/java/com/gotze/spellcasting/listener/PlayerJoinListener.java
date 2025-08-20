package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.PlayerPickaxe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set resource pack
        // Currently not sending resource pack because I'm using the client-side resource pack instead of server side
//        String resourcePackUrl = "https://download.mc-packs.net/pack/7e609ad3d7d2abf0668002fa98c9d198ab544fe7.zip";
//        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);
//        player.setResourcePack(resourcePackUrl);

        // Give player pickaxe their pickaxe
        PlayerPickaxe.setPickaxe(player, null);
        player.give(PlayerPickaxe.getPickaxe(player));
    }
}