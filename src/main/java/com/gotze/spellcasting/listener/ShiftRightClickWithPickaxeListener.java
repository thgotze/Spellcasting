package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.gui.PickaxeGUI;
import com.gotze.spellcasting.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShiftRightClickWithPickaxeListener implements Listener {

    @EventHandler
    public void onShiftRightClickWithPickaxe(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().isRightClick() || !player.isSneaking()) return;

        Material material = event.getMaterial();

        if (ItemUtils.isPickaxe(material)) {
            event.setCancelled(true);
            new PickaxeGUI().openGUI(player);
        }
    }
}