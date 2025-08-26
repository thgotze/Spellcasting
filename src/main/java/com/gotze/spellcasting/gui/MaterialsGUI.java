package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.PlayerPickaxeManager;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackCreator;
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
        gui = Bukkit.createInventory(this, 45, Component.text("Materials"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPlayerPickaxe(player));
        gui.setItem(20, STONE_PICKAXE_BUTTON);
        gui.setItem(21, IRON_PICKAXE_BUTTON);
        gui.setItem(22, GOLD_PICKAXE_BUTTON);
        gui.setItem(23, DIAMOND_PICKAXE_BUTTON);
        gui.setItem(24, NETHERITE_PICKAXE_BUTTON);
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
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private final ItemStack STONE_PICKAXE_BUTTON = ItemStackCreator.createItemStack(
            Material.STONE_PICKAXE,
            Component.text("Stone Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Cobblestone")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack IRON_PICKAXE_BUTTON = ItemStackCreator.createItemStack(
            Material.IRON_PICKAXE,
            Component.text("Iron Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Iron Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack GOLD_PICKAXE_BUTTON = ItemStackCreator.createItemStack(
            Material.GOLDEN_PICKAXE,
            Component.text("Gold Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Gold Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack DIAMOND_PICKAXE_BUTTON = ItemStackCreator.createItemStack(
            Material.DIAMOND_PICKAXE,
            Component.text("Diamond Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Diamond")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack NETHERITE_PICKAXE_BUTTON = ItemStackCreator.createItemStack(
            Material.NETHERITE_PICKAXE,
            Component.text("Netherite Pickaxe")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Netherite Ingot")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );
}