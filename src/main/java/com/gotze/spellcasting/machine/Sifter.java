package com.gotze.spellcasting.machine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Sifter extends Machine {

    public Sifter(Location location, Player player) {
        super(MachineType.SIFTER, location, player);
        populate();
    }

    @Override
    protected void populate() {

    }

    @Override
    public void tick() {

    }

    @Override
    public @Nullable ItemStack getInputItem() {
        return null;
    }

    @Override
    public void setInputItem(@Nullable ItemStack inputItem) {

    }

    @Override
    public @Nullable ItemStack getOutputItem() {
        return null;
    }

    @Override
    public void setOutputItem(@Nullable ItemStack outputItem) {

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