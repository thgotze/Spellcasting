package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.menu.Button;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Washer extends Machine {

    public Washer(Location location, Player player) {
        super(MachineType.WASHER, location, player);
        populate();
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
    protected void populate() {
        for (int i = 0; i <= 10; i++) {
            getInventory().setItem(i, new ItemStackBuilder(Material.PAPER)
                    .itemModel(NamespacedKey.minecraft("air"))
                    .hideTooltipBox()
                    .build());
        }

        setButton(new Button(4, ItemStack.of(Material.CAULDRON)) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                event.getWhoClicked().sendMessage("You clicked the cauldron!");
            }
        });
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
}
