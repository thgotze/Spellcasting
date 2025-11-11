package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.util.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Machine extends Menu {
    private final Location location;
    private final UUID placedBy;
    protected int progress;

    public Machine(int rows, Component title, Location location, Player player) {
        super(rows, title, true);
        this.location = location;
        this.placedBy = player.getUniqueId();
        this.progress = 0;
    }

    public abstract void tick();
    public abstract ItemStack toItemStack();

    public Location getLocation() {
        return location.clone();
    }

    public UUID getWhoPlaced() {
        return placedBy;
    }
}