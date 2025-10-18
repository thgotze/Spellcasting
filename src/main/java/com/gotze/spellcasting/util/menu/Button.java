package com.gotze.spellcasting.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
    private int slot;
    private ItemStack item;
    private boolean interactable = false; // Default to non-interactable

    public Button(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public Button(int slot, ItemStack item, boolean interactable) {
        this.slot = slot;
        this.item = item;
        this.interactable = interactable;
    }

    public abstract void onClick(InventoryClickEvent event);

    public int slot() {
        return slot;
    }

    public ItemStack item() {
        return item;
    }

    public Button item(ItemStack item) {
        this.item = item;
        return this;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public Button interactable(boolean interactable) {
        this.interactable = interactable;
        return this;
    }
}