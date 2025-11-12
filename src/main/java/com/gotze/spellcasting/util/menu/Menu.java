package com.gotze.spellcasting.util.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    private final Inventory inventory;
    private final boolean interactable;
    private final Map<Integer, Button> buttons = new HashMap<>();

    public Menu(int rows, Component title, boolean interactable) {
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.interactable = interactable;
    }

    protected abstract void populate(Player player);

    protected abstract void onInventoryOpen(InventoryOpenEvent event);

    protected abstract void onInventoryClose(InventoryCloseEvent event);

    protected abstract void onInventoryDrag(InventoryDragEvent event);

    protected abstract void onTopInventoryClick(InventoryClickEvent event);

    protected abstract void onBottomInventoryClick(InventoryClickEvent event);

    public boolean isInteractable() {
        return interactable;
    }

    public void open(Player player) {
        player.setItemOnCursor(null);
        player.openInventory(inventory);
    }

    public void setItems(ItemStack... items) {
        inventory.addItem(items);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void removeItem(int slot) {
        inventory.setItem(slot, null);
    }

    public void setButtons(Button... buttons) {
        for (Button button : buttons) {
            this.buttons.put(button.getSlot(), button);
            inventory.setItem(button.getSlot(), button.getItem());
        }
    }

    public void setButton(Button button) {
        this.buttons.put(button.getSlot(), button);
        inventory.setItem(button.getSlot(), button.getItem());
    }

    public void setButton(Button button, int slot) {
        this.buttons.put(slot, button);
        inventory.setItem(slot, button.getItem());
    }

    public Map<Integer, Button> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public void clear() {
        inventory.clear();
        buttons.clear();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}