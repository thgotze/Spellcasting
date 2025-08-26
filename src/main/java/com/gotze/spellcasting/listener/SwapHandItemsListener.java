package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.spell.LaserSpell;
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
            return;
        }

        if (material == Material.BLAZE_ROD) {
            event.setCancelled(true);
            new LaserSpell(player).cast();
            return;
        }

        if (material == Material.DIAMOND) {
            event.setCancelled(true);
            player.sendMessage("\uE333");
            player.sendMessage("1");
            player.sendMessage("2");
            player.sendMessage("3");
            player.sendMessage("4");
            player.sendMessage("5");
            player.sendMessage("6");
            player.sendMessage("7");
            player.sendMessage("8");
            player.sendMessage("9");
            player.sendMessage("10");
            player.sendMessage("11");
            player.sendMessage("12");
            player.sendMessage("13");
            player.sendMessage("14");
            player.sendMessage("15");
            player.sendMessage("16");
            player.sendMessage("17");
            return;
        }
    }
}
