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

public class EnchantmentsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Enchantments"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPlayerPickaxe(player));
        gui.setItem(21, EFFICIENCY_BOOK_BUTTON);
        gui.setItem(22, UNBREAKING_BOOK_BUTTON);
        gui.setItem(23, FORTUNE_BOOK_BUTTON);
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
//            case 21 ->
//            case 22 ->
//            case 23 ->
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private final ItemStack EFFICIENCY_BOOK_BUTTON = ItemStackCreator.createItemStack(
            Material.REDSTONE,
            Component.text("Efficiency")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Redstone Dust")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack UNBREAKING_BOOK_BUTTON = ItemStackCreator.createItemStack(
            Material.OBSIDIAN,
            Component.text("Unbreaking")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Obsidian")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack FORTUNE_BOOK_BUTTON = ItemStackCreator.createItemStack(
            Material.LAPIS_LAZULI,
            Component.text("Fortune")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements"))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("32x Lapis Lazuli")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}