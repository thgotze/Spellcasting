package com.gotze.spellcasting.machine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Machine implements InventoryHolder {
    private final @NotNull Location location;
    private final @NotNull UUID placedBy;
    private final @NotNull Inventory inventory;
    protected int progress;

    public Machine(@NotNull Location location, @NotNull Player player) {
        this.location = location;
        this.placedBy = player.getUniqueId();
        this.inventory = populate();
        this.progress = 0;
    }

    protected abstract Inventory populate();
    public abstract void tick();
    public abstract ItemStack toItemStack();

    public @NotNull Location getLocation() {
        return location.clone();
    }

    public @NotNull UUID getWhoPlaced() {
        return placedBy;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}