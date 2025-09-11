package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
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
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class EnchantmentsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Enchantments"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));
        gui.setItem(20, HASTE_AND_SPEED_ENCHANT_BUTTON); // TODO: testing
        gui.setItem(21, EFFICIENCY_ENCHANT_BUTTON);
        gui.setItem(22, UNBREAKING_ENCHANT_BUTTON);
        gui.setItem(23, FORTUNE_ENCHANT_BUTTON);
        gui.setItem(24, MINE_BLOCK_ABOVE_ENCHANT_BUTTON); // TODO: testing
        gui.setItem(43, DEBUG_CLEAR_ENCHANTS_BUTTON); // TODO: debug
        gui.setItem(44, DEBUG_GIVE_RESOURCES_BUTTON); // TODO: debug
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
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;

        int slot = event.getSlot();

        switch (slot) {
            case 20, 21, 22, 23, 24 ->
                    upgradeEnchantment(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 43 -> clearPickaxeEnchants(player, playerInventory, clickedInventory); // TODO: debug
            case 44 -> giveResources(player, playerInventory); // TODO: debug
        }
    }

    private void upgradeEnchantment(Player player, PlayerInventory playerInventory, Material clickedUpgrade, Inventory clickedInventory) {
        ItemStack heldItem = playerInventory.getItemInMainHand();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        final int REQUIRED_AMOUNT = 32;
        final ItemStack REQUIRED_MATERIALS = ItemStack.of(clickedUpgrade, REQUIRED_AMOUNT);

        if (!playerInventory.containsAtLeast(REQUIRED_MATERIALS, REQUIRED_AMOUNT)) {
            player.sendMessage(Component.text("You don't have the required materials (" + REQUIRED_AMOUNT + "x " + clickedUpgrade.toString().toLowerCase() + ") " + "to enchant your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        Enchantment.EnchantmentType enchantmentType = switch (clickedUpgrade) {
            case REDSTONE -> Enchantment.EnchantmentType.EFFICIENCY;
            case OBSIDIAN -> Enchantment.EnchantmentType.UNBREAKING;
            case LAPIS_LAZULI -> Enchantment.EnchantmentType.FORTUNE;
            case DIAMOND -> Enchantment.EnchantmentType.HASTE_AND_SPEED; // TODO: testing
            case EMERALD -> Enchantment.EnchantmentType.MINE_BLOCK_ABOVE; // TODO: testing
            default -> null;
        };

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        if (pickaxeData.hasEnchantment(enchantmentType) && pickaxeData.getEnchantment(enchantmentType).isMaxLevel()) {
            player.sendMessage(Component.text("Cannot upgrade " + enchantmentType.getName() + " past level " + enchantmentType.getMaxLevel() + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their main hand
        // 2. they have enough of the required material
        // 3. their pickaxe is not at the max level of the chosen enchant

        playerInventory.removeItem(heldItem);
        PlayerPickaxeService.upgradePickaxeEnchantment(pickaxeData, enchantmentType);

        playerInventory.removeItem(REQUIRED_MATERIALS);
        SoundUtils.playSuccessSound(player);

        ItemStack playerPickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(playerPickaxe);
        clickedInventory.setItem(4, GUIUtils.cloneItemWithoutDamage(playerPickaxe));
    }

    private void clearPickaxeEnchants(Player player, PlayerInventory playerInventory, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }
        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        pickaxeData.removeEnchantments();

        playerInventory.remove(playerInventory.getItemInMainHand());
        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        clickedInventory.setItem(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(pickaxe));
        SoundUtils.playUIClickSound(player);
    }

    private void giveResources(Player player, PlayerInventory playerInventory) {
        playerInventory.addItem(ItemStack.of(Material.DIAMOND, 32),
                ItemStack.of(Material.REDSTONE, 32),
                ItemStack.of(Material.OBSIDIAN, 32),
                ItemStack.of(Material.LAPIS_LAZULI, 32),
                ItemStack.of(Material.EMERALD, 32));
        SoundUtils.playUIClickSound(player);
    }

    private final ItemStack EFFICIENCY_ENCHANT_BUTTON = new ItemStackBuilder(Material.REDSTONE)
            .displayName(Component.text("Efficiency")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Redstone Dust")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack UNBREAKING_ENCHANT_BUTTON = new ItemStackBuilder(Material.OBSIDIAN)
            .displayName(Component.text("Unbreaking")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Obsidian")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack FORTUNE_ENCHANT_BUTTON = new ItemStackBuilder(Material.LAPIS_LAZULI)
            .displayName(Component.text("Fortune")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Lapis Lazuli")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack HASTE_AND_SPEED_ENCHANT_BUTTON = new ItemStackBuilder(Material.DIAMOND) // TODO: testing
            .displayName(Component.text("Haste And Speed")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Diamond")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack MINE_BLOCK_ABOVE_ENCHANT_BUTTON = new ItemStackBuilder(Material.EMERALD) // TODO: testing
            .displayName(Component.text("Mine Block Above")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Emerald")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack DEBUG_CLEAR_ENCHANTS_BUTTON = new ItemStackBuilder(Material.BARRIER) // TODO: debug
            .displayName(Component.text("DEBUG: CLEAR ENCHANTS").decorate(TextDecoration.BOLD)
                    .color(NamedTextColor.RED))
            .build();

    private final ItemStack DEBUG_GIVE_RESOURCES_BUTTON = new ItemStackBuilder(Material.CHEST) // TODO: debug
            .displayName(Component.text("DEBUG: GIVE RESOURCES")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
            .build();

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}