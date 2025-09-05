package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.data.PlayerPickaxeService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!PlayerPickaxeService.hasPlayerPickaxedata(player)) {
            ItemStack starterPick = PlayerPickaxeService.createStarterPickaxe(player);
            player.give(starterPick);
            player.sendMessage(Component.text("You have been given a starter pickaxe!")
                    .color(NamedTextColor.GREEN));
        }

        // Set resource pack TODO: currently using clientside resourcepack instead of serverside
//        String resourcePackUrl = "https://download.mc-packs.net/pack/7e609ad3d7d2abf0668002fa98c9d198ab544fe7.zip";
//        player.sendMessage("Setting resource pack from file: " + resourcePackUrl);
//        player.setResourcePack(resourcePackUrl);
    }
}