package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.PlayerPickaxe;
import com.gotze.spellcasting.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpellsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(
                this,
                45,
                "Spells"
        );
        gui.setItem(13, PlayerPickaxe.getPickaxe(player));
        gui.setItem(30, new ItemStack(Material.BLAZE_ROD));
        gui.setItem(31, new ItemStack(Material.BLAZE_ROD));
        gui.setItem(32, new ItemStack(Material.BLAZE_ROD));
        gui.setItem(36, ItemUtils.RETURN_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SpellsGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        if (clickedInventory.equals(player.getInventory())) return;

        int slot = event.getSlot();

        switch (slot) {
            case 36 -> new PickaxeGUI().openGUI(player);
        }
    }
}