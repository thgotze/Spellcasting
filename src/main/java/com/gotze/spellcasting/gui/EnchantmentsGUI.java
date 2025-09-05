package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.data.PlayerPickaxeData;
import com.gotze.spellcasting.data.PlayerPickaxeService;
import com.gotze.spellcasting.enchantment.Enchantment;
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
        gui.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
        gui.setItem(21, EFFICIENCY_BOOK_BUTTON);
        gui.setItem(22, UNBREAKING_BOOK_BUTTON);
        gui.setItem(23, FORTUNE_BOOK_BUTTON);
        gui.setItem(43, new ItemStackBuilder(Material.BARRIER) // TODO temp
                .displayName(Component.text("DEBUG: CLEAR ENCHANTS").decorate(TextDecoration.BOLD)
                        .color(NamedTextColor.RED))
                .build());
        gui.setItem(44, new ItemStackBuilder(Material.CHEST) // TODO: remove later
                .displayName(Component.text("DEBUG: GIVE RESOURCES")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build());
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
            case 21, 22, 23 ->
                    upgradeEnchantment(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 43 -> { // TODO: delete later
                ItemStack playerPickaxe = playerInventory.getItemInMainHand();
                playerInventory.getItemInMainHand().removeEnchantments();
                PlayerPickaxeService.setPlayerPickaxeData(player, playerPickaxe);
                clickedInventory.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
                SoundUtils.playSuccessSound(player);
            }
            case 44 -> playerInventory.addItem(ItemStack.of(Material.REDSTONE, 32), // TODO: remove later
                    ItemStack.of(Material.OBSIDIAN, 32),
                    ItemStack.of(Material.LAPIS_LAZULI, 32));
        }
    }

    private void upgradeEnchantment(Player player, PlayerInventory playerInventory, Material clickedUpgrade, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, playerInventory.getItemInMainHand())) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }
        ItemStack playerPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);

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
            case EMERALD -> Enchantment.EnchantmentType.CUSTOM_ENCHANT; // TODO: testing
            default -> null;
        };

        PlayerPickaxeData playerPickaxeData = PlayerPickaxeService.getPlayerPickaxeData(player);
        if (playerPickaxeData.hasEnchantment(enchantmentType) && playerPickaxeData.getEnchantment(enchantmentType).isMaxLevel()) {
            player.sendMessage(Component.text("Cannot upgrade " + enchantmentType.getName() + " past level " + enchantmentType.getMaxLevel() + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their main hand
        // 2. they have enough of the required material
        // 3. their pickaxe is not at the max level of the chosen enchant

        playerInventory.removeItem(playerPickaxe);
        playerInventory.removeItem(REQUIRED_MATERIALS);

        PlayerPickaxeService.upgradePlayerPickaxeEnchantment(player, enchantmentType);
        playerInventory.addItem(PlayerPickaxeService.getPlayerPickaxe(player));

        clickedInventory.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
        SoundUtils.playSuccessSound(player);
    }

    private final ItemStack EFFICIENCY_BOOK_BUTTON = new ItemStackBuilder(Material.REDSTONE)
            .displayName(Component.text("Efficiency")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Redstone Dust")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack UNBREAKING_BOOK_BUTTON = new ItemStackBuilder(Material.OBSIDIAN)
            .displayName(Component.text("Unbreaking")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Obsidian")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack FORTUNE_BOOK_BUTTON = new ItemStackBuilder(Material.LAPIS_LAZULI)
            .displayName(Component.text("Fortune")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Lapis Lazuli")
                            .color(NamedTextColor.GRAY))
            .build();

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}