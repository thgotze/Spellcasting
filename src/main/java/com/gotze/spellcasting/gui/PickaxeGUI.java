package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.PlayerPickaxe;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
        setFrames();
        gui.setItem(13, PlayerPickaxe.getPickaxe(player));
        gui.setItem(30, AnvilButton());
        gui.setItem(31, EnchantedBookButton());
        gui.setItem(32, SpellsButton());
        player.openInventory(this.gui);
    }

    private void setFrames() {
        ItemStack FRAME = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = FRAME.getItemMeta();
        meta.setHideTooltip(true);
        FRAME.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, FRAME);
        }
        for (int i = 36; i < 45; i++) {
            gui.setItem(i, FRAME);
        }
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
            case 30 -> new MaterialsGUI().openGUI(player);
            case 31 -> new EnchantsGUI().openGUI(player);
            case 32 -> new SpellsGUI().openGUI(player);
        }
    }

    private ItemStack AnvilButton() {
        ItemStack itemStack = new ItemStack(Material.ANVIL);
        itemStack.editMeta(meta -> meta.displayName(Component.text("Pickaxe Material")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ));
        return itemStack;
    }

    private ItemStack EnchantedBookButton() {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        itemStack.editMeta(meta -> meta.displayName(Component.text("Enchantments")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ));
        return itemStack;
    }

    private ItemStack SpellsButton() {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);
        itemStack.editMeta(meta -> meta.displayName(Component.text("Spells")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
        ));
        return itemStack;
    }
}