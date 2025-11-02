package com.gotze.spellcasting.util.menu;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

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
            // Cancel event if player clicks their own inventory in a non-interactable menu
            if (event.getClickedInventory() != menu.getInventory()) {
                if (!menu.isInteractable()) {
                    event.setCancelled(true);
                }
                return;
            }

            // Cancel event if button is not interactable
            Button button = menu.buttons().get(event.getSlot());
            if (button != null) {
                if (!button.isInteractable()) {
                    event.setCancelled(true);
                }
            } else {
                // Cancel event if clicked slot is not a button but a regular item and the menu is not interactable
                if (!menu.isInteractable()) {
                    event.setCancelled(true);
                }
            }

            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                if (button != null) {
                    button.onClick(event);
                }
                menu.onClick(event);
            }, 1L);
        });
    }

    private Optional<Menu> asMenu(Inventory inventory) {
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (inventoryHolder == null) return Optional.empty();
        if (inventoryHolder instanceof Menu menu) return Optional.of(menu);
        return Optional.empty();
    }
}