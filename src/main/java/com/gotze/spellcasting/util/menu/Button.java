package com.gotze.spellcasting.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
    private int slot;
    private ItemStack item;

    public Button(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public abstract void onClick(InventoryClickEvent event);

    public int slot() {
        return slot;
    }

    public Button slot(int slot) {
        this.slot = slot;
        return this;
    }

    public ItemStack item() {
        return item;
    }

    public Button item(ItemStack item) {
        this.item = item;
        return this;
    }
}