package com.gotze.spellcasting.util.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    private final Inventory inventory;
    private final Map<Integer, Button> buttons = new HashMap<>();

    public Menu(int rows, Component title) {
        inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public Menu(InventoryType type, Component title) {
        inventory = Bukkit.createInventory(this, type, title);
    }

    protected abstract void onOpen(InventoryOpenEvent event);

    protected abstract void onClose(InventoryCloseEvent event);

    protected abstract void onClick(InventoryClickEvent event);

    public void open(Player player) {
        player.setItemOnCursor(null);
        player.openInventory(inventory);
    }

    public void items(@NotNull ItemStack... items) {
        inventory.addItem(items);
    }

    public void item(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void remove(int slot) {
        inventory.setItem(slot, null);
    }

    public void buttons(Button... buttons) {
        for (Button button : buttons) {
            this.buttons.put(button.slot(), button);
            inventory.setItem(button.slot(), button.item());
        }
    }

    public void button(Button button) {
        this.buttons.put(button.slot(), button);
        inventory.setItem(button.slot(), button.item());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Map<Integer, Button> buttons() {
        return Collections.unmodifiableMap(buttons);
    }

    public void clear() {
        inventory.clear();
        buttons.clear();
    }
}