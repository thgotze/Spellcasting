package com.gotze.spellcasting.util.menu;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        asMenu(event.getInventory()).ifPresent(menu -> menu.onOpen(event));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        asMenu(event.getInventory()).ifPresent(menu -> menu.onClose(event));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        asMenu(event.getView().getTopInventory()).ifPresent(menu -> {
            event.setCancelled(true);
            if (event.getClickedInventory() != menu.getInventory()) return;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), () -> {
                menu.onClick(event);
                Optional.ofNullable(menu.buttons().get(event.getSlot())).ifPresent(button -> button.onClick(event));
            }, 1L);
        });
    }

    private Optional<Menu> asMenu(Inventory inventory) {
        if (inventory.getHolder() == null) return Optional.empty();
        if (inventory.getHolder() instanceof Menu menu) return Optional.of(menu);
        return Optional.empty();
    }
}