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

public class EnchantmentsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(
                this,
                45,
                "Enchantments"
        );
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxe.getPickaxe(player));
        gui.setItem(21, EfficiencyBookButton());
        gui.setItem(22, UnbreakingBookButton());
        gui.setItem(23, FortuneBookButton());
        gui.setItem(36, GUIUtils.RETURN_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantmentsGUI)) return;

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

    private ItemStack EfficiencyBookButton() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Efficiency")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Redstone Dust")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack UnbreakingBookButton() {
        ItemStack itemStack = new ItemStack(Material.OBSIDIAN);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Unbreaking")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Obsidian")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }

    private ItemStack FortuneBookButton() {
        ItemStack itemStack = new ItemStack(Material.LAPIS_LAZULI);
        itemStack.editMeta(meta -> {
            meta.displayName(Component.text("Fortune")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
            );

            meta.lore(Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Lapis Lazuli")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return itemStack;
    }
}