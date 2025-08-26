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

public class PickaxeGUI implements InventoryHolder, Listener {
    private Inventory gui;

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Pickaxe"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPlayerPickaxe(player));
        gui.setItem(21, MATERIALS_BUTTON);
        gui.setItem(22, ENCHANTMENTS_BUTTON);
        gui.setItem(23, SPELLS_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PickaxeGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        if (clickedInventory.equals(player.getInventory())) return;

        int slot = event.getSlot();

        switch (slot) {
            case 21 -> {
                new MaterialsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 22 -> {
                new EnchantmentsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 23 -> {
                new SpellsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private final ItemStack MATERIALS_BUTTON = ItemStackCreator.createItemStack(
            Material.ANVIL,
            Component.text("Materials")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("5 material tiers available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack ENCHANTMENTS_BUTTON = ItemStackCreator.createItemStack(
            Material.ENCHANTED_BOOK,
            Component.text("Enchantments")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 enchantments available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack SPELLS_BUTTON = ItemStackCreator.createItemStack(
            Material.END_CRYSTAL,
            Component.text("Spells")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 spells available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );
}