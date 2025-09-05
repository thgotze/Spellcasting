package com.gotze.spellcasting.listener;

import com.destroystokyo.paper.MaterialSetTag;
import com.gotze.spellcasting.ability.LaserAbility;
import com.gotze.spellcasting.ability.SliceAbility;
import com.gotze.spellcasting.data.PlayerPickaxeService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SwapHandItemsListener implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Material materialOfHeldItem = player.getInventory().getItemInMainHand().getType();

        if (MaterialSetTag.ITEMS_PICKAXES.isTagged(materialOfHeldItem)) {
            event.setCancelled(true);
            new SliceAbility(player).cast();
            return;
        }

        if (materialOfHeldItem == Material.COBBLESTONE) {
            player.give(PlayerPickaxeService.getPlayerPickaxe(player));
            event.setCancelled(true);
            return;
        }

        if (materialOfHeldItem == Material.BLAZE_ROD) {
            event.setCancelled(true);
            new LaserAbility(player).cast();
            return;
        }

        if (materialOfHeldItem == Material.DIAMOND) {
            event.setCancelled(true);
            player.sendMessage("");
            player.sendMessage("\uE333");
            for (int i = 0; i < 17; i++) {
                player.sendMessage("");
            }
        }
    }
}
