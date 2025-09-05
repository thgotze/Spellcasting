package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.data.PlayerPickaxeService;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class SpellsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Spells"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
        gui.setItem(21, SLICE_SPELL_BUTTON);
        gui.setItem(22, LASER_SPELL_BUTTON);
        gui.setItem(23, ROCKET_SPELL_BUTTON);
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
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;
        
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

    private final ItemStack SLICE_SPELL_BUTTON = new ItemStackBuilder(Material.IRON_SWORD)
            .displayName(Component.text("Slice")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack LASER_SPELL_BUTTON = new ItemStackBuilder(Material.LIGHTNING_ROD)
            .displayName(Component.text("Laser")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack ROCKET_SPELL_BUTTON = new ItemStackBuilder(Material.FIREWORK_ROCKET)
            .displayName(Component.text("Rocket")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .hideAdditionalTooltip()
            .hideAttributes()
            .build();

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}