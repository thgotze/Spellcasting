package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.spell.SliceSpell;
import com.gotze.spellcasting.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SwapHandItemsListener implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Material material = player.getInventory().getItemInMainHand().getType();

        if (ItemUtils.isPickaxe(material)) {
            event.setCancelled(true);
            new SliceSpell(player).cast();
        }

        if (material == Material.DIAMOND) {
            player.sendMessage("\uE333");
        }
    }
}
