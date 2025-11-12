package com.gotze.spellcasting.machine.sifter;

import com.gotze.spellcasting.machine.Machine;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class Sifter extends Machine {

    private static final Component SIFTER_TITLE = text("Sifter").color((color(64, 64, 64)));
    private static final int INPUT_SLOT = 11;
    private static final int OUTPUT_SLOT = 15;
    private static final int DEFAULT_PROCESSING_TIME_IN_TICKS = 100;

    public Sifter(Location location, Player player) {
        super(3, SIFTER_TITLE, location, player);
    }

    @Override
    protected void populate(Player player) {

    }

    @Override
    public void tick() {

    }

    @Override
    public ItemStack toItemStack() {
        return null;
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }

    private enum SiftingRecipe {

    }
}
