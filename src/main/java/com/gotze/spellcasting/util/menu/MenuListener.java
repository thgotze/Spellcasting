package com.gotze.spellcasting.util.menu;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        asMenu(event.getInventory()).ifPresent(menu -> menu.onInventoryOpen(event));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        asMenu(event.getInventory()).ifPresent(menu -> menu.onInventoryClose(event));
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        asMenu(event.getInventory()).ifPresent(menu ->
                Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () ->
                        menu.onInventoryDrag(event), 1L));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        asMenu(event.getInventory()).ifPresent(menu -> {
            // ---------------
            // Clicked top inventory
            // ---------------
            if (clickedInventory.equals(event.getView().getTopInventory())) {
                // ===============
                // Clicked slot is a button
                // ===============
                Button button = menu.getButtons().get(event.getSlot());
                if (button != null) {
                    // Prevent item movement
                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                        button.onButtonClick(event);
                        menu.onTopInventoryClick(event);
                    }, 1L);
                }
                // ===============
                // Clicked slot is not a button
                // ===============
                else {
                    // Prevent item movement if menu is non-interactable
                    if (!menu.isInteractable()) {
                        event.setCancelled(true);
                    }
                    Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                        menu.onTopInventoryClick(event);
                    }, 1L);
                }
                return;
            }
            // ---------------
            // Clicked bottom inventory
            // ---------------
            if (clickedInventory.equals(event.getView().getBottomInventory())) {
                // Prevent item movement if menu is non-interactable
                if (!menu.isInteractable()) {
                    event.setCancelled(true);
                }

                Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                    menu.onBottomInventoryClick(event);
                }, 1L);
            }
        });
    }

    private Optional<Menu> asMenu(Inventory inventory) {
        if (inventory.getHolder() instanceof Menu menu) {
            return Optional.of(menu);
        } else {
            return Optional.empty();
        }
    }
}