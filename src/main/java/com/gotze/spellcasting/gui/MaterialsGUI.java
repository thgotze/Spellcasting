package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.PlayerPickaxe;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

import java.util.Arrays;

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
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxe.getPickaxe(player));
        gui.setItem(20, StonePickaxeButton());
        gui.setItem(21, IronPickaxeButton());
        gui.setItem(22, GoldPickaxeButton());
        gui.setItem(23, DiamondPickaxeButton());
        gui.setItem(24, NetheritePickaxeButton());
        gui.setItem(36, GUIUtils.RETURN_BUTTON);
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
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }

        }
    }

    private ItemStack StonePickaxeButton() {
        ItemStack itemStack = new ItemStack(Material.STONE_PICKAXE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Stone Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Cobblestone")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack IronPickaxeButton() {
        ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Iron Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Iron Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack GoldPickaxeButton() {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_PICKAXE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Gold Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Gold Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack DiamondPickaxeButton() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Diamond Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Diamond")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack NetheritePickaxeButton() {
        ItemStack itemStack = new ItemStack(Material.NETHERITE_PICKAXE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Netherite Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Netherite Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private void upgradePickaxe(Player player) {
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

        Inventory playerInventory = player.getInventory();
        ItemStack requiredMaterials = new ItemStack(upgradeMaterial, 32);
        if (!playerInventory.containsAtLeast(requiredMaterials, 32)) return;
        playerInventory.removeItem(requiredMaterials);

        playerInventory.removeItemAnySlot(PlayerPickaxe.getPickaxe(player));

        PlayerPickaxe.setPickaxe(player, new ItemStack(newPickaxeMaterial));

        openGUI(player);
        player.give(PlayerPickaxe.getPickaxe(player));
    }
}