package com.gotze.spellcasting.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button implements Cloneable {
    private final int slot;
    private final ItemStack item;

    public Button(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public abstract void onButtonClick(InventoryClickEvent event);
}