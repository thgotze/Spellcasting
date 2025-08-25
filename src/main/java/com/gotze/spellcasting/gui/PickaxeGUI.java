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

public class PickaxeGUI implements InventoryHolder, Listener {
    private Inventory gui;

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(
                this,
                45,
                "Pickaxe"
        );
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPickaxe(player));
        gui.setItem(21, MaterialsButton());
        gui.setItem(22, EnchantmentsButton());
        gui.setItem(23, SpellsButton());
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

    private ItemStack MaterialsButton() {
        ItemStack itemStack = new ItemStack(Material.ANVIL);
        anvil.getItemMeta().setEnchantmentGlintOverride(true);


        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Materials") // TODO
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("5 material tiers available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack EnchantmentsButton() {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Enchantments")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 enchantments available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack SpellsButton() {
        ItemStack itemStack = new ItemStack(Material.END_CRYSTAL);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Spells")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 spells available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }
}