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

public class MaterialsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(
                this,
                45,
                "Materials"
        );
        gui.setItem(13, PlayerPickaxe.getPickaxe(player));
        gui.setItem(30, new ItemStack(Material.ANVIL));
        gui.setItem(31, new ItemStack(Material.ANVIL));
        gui.setItem(32, new ItemStack(Material.ANVIL));
        gui.setItem(36, ItemUtils.RETURN_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MaterialsGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        if (clickedInventory.equals(player.getInventory())) return;

        int slot = event.getSlot();

        switch (slot) {
            case 1 -> upgradePickaxe(player);
            case 36 -> new PickaxeGUI().openGUI(player);
        }
    }

    private void upgradePickaxe(Player player) {
        Inventory playerInventory = player.getInventory();

        ItemStack playerPickaxe = PlayerPickaxe.getPickaxe(player);

        Material upgradeMaterial = null;
        Material newPickaxeMaterial = null;

        switch (playerPickaxe.getType()) {
            case WOODEN_PICKAXE -> {
                upgradeMaterial = Material.COBBLESTONE;
                newPickaxeMaterial = Material.STONE_PICKAXE;
            }

            case STONE_PICKAXE -> {
                upgradeMaterial = Material.IRON_INGOT;
                newPickaxeMaterial = Material.IRON_PICKAXE;
            }

            case IRON_PICKAXE -> {
                upgradeMaterial = Material.GOLD_INGOT;
                newPickaxeMaterial = Material.GOLDEN_PICKAXE;
            }

            case GOLDEN_PICKAXE -> {
                upgradeMaterial = Material.DIAMOND;
                newPickaxeMaterial = Material.DIAMOND_PICKAXE;
            }

            case DIAMOND_PICKAXE -> {
                upgradeMaterial = Material.NETHERITE_INGOT;
                newPickaxeMaterial = Material.NETHERITE_PICKAXE;
            }

            case NETHERITE_PICKAXE -> {
                upgradeMaterial = Material.OAK_PLANKS;
                newPickaxeMaterial = Material.WOODEN_PICKAXE;
            }
        }
        if (upgradeMaterial == null) return;

        ItemStack requiredMaterials = new ItemStack(upgradeMaterial, 32);
        if (!playerInventory.containsAtLeast(requiredMaterials, 32)) return;
        playerInventory.removeItem(requiredMaterials);

        playerInventory.removeItemAnySlot(PlayerPickaxe.getPickaxe(player));

        PlayerPickaxe.setPickaxe(player, new ItemStack(newPickaxeMaterial));

        openGUI(player);
        player.give(PlayerPickaxe.getPickaxe(player));
    }
}