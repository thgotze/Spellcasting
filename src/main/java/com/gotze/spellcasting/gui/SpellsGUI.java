package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.PlayerPickaxeManager;
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
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPickaxe(player));
        gui.setItem(21, SliceSpellButton());
        gui.setItem(22, LaserSpellButton());
        gui.setItem(23, RocketSpellButton());
        gui.setItem(36, GUIUtils.RETURN_BUTTON);
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
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private ItemStack SliceSpellButton() {
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Slice")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack LaserSpellButton() {
        ItemStack itemStack = new ItemStack(Material.LIGHTNING_ROD);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Laser")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack RocketSpellButton() {
        ItemStack itemStack = new ItemStack(Material.FIREWORK_ROCKET);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Rocket")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }
}