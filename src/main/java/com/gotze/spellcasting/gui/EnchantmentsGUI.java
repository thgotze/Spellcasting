package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.feature.pickaxe.enchantment.Enchantment;
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
        gui.setItem(19, EFFICIENCY_ENCHANTMENT_BUTTON);
        gui.setItem(20, FORTUNE_ENCHANTMENT_BUTTON);
        gui.setItem(21, UNBREAKING_ENCHANTMENT_BUTTON);
        gui.setItem(22, UNCOVER_ENCHANTMENT_BUTTON);
        gui.setItem(23, MOMENTUM_ENCHANTMENT_BUTTON);
        gui.setItem(24, OVERLOAD_ENCHANTMENT_BUTTON);
        gui.setItem(25, GUIUtils.FRAME);
        gui.setItem(43, DEBUG_CLEAR_ENCHANTMENTS_BUTTON); // TODO: debug
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
            case 19, 20, 21, 22, 23, 24 ->
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

        Enchantment.EnchantmentType chosenEnchant = null;
        for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
            if (enchantmentType.getMaterialRepresentation() == clickedUpgrade) {
                chosenEnchant = enchantmentType;
                break;
            }
        }
        if (chosenEnchant == null) return;

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        if (pickaxeData.hasEnchantment(chosenEnchant) && pickaxeData.getEnchantment(chosenEnchant).isMaxLevel()) {
            player.sendMessage(Component.text("Cannot upgrade " + chosenEnchant + " past level " + chosenEnchant.getMaxLevel() + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their main hand
        // 2. they have enough of the required material
        // 3. their pickaxe is not at the max level of the chosen enchant

        if (pickaxeData.hasEnchantment(chosenEnchant)) {
            pickaxeData.getEnchantment(chosenEnchant).increaseLevel();
        } else {
            try {
                Enchantment newEnchantment = chosenEnchant.getEnchantmentClass().getDeclaredConstructor().newInstance();
                pickaxeData.addEnchantment(newEnchantment);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create enchantment: " + chosenEnchant, e);
            }
        }

        playerInventory.remove(heldItem);
        playerInventory.removeItem(REQUIRED_MATERIALS);

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        clickedInventory.setItem(4, GUIUtils.cloneItemWithoutDamage(pickaxe));
        SoundUtils.playSuccessSound(player);
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

        clickedInventory.setItem(4, GUIUtils.cloneItemWithoutDamage(pickaxe));
        SoundUtils.playUIClickSound(player);
    }

    private void giveResources(Player player, PlayerInventory playerInventory) { // TODO: debug
        for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
            playerInventory.addItem(ItemStack.of(enchantmentType.getMaterialRepresentation(), 32));
        }
        SoundUtils.playUIClickSound(player);
    }

    private final ItemStack EFFICIENCY_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.EFFICIENCY.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.EFFICIENCY.toString())
                    .color(Enchantment.EnchantmentType.EFFICIENCY.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.EFFICIENCY.getMaterialRepresentation().toString()))
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack FORTUNE_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.FORTUNE.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.FORTUNE.toString())
                    .color(Enchantment.EnchantmentType.FORTUNE.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.FORTUNE.getMaterialRepresentation().toString()))
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack UNBREAKING_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.UNBREAKING.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.UNBREAKING.toString())
                    .color(Enchantment.EnchantmentType.UNBREAKING.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.UNBREAKING.getMaterialRepresentation().toString()))
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack UNCOVER_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.UNCOVER.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.UNCOVER.toString())
                    .color(Enchantment.EnchantmentType.UNCOVER.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.UNCOVER.getMaterialRepresentation().toString()))
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack MOMENTUM_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.MOMENTUM.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.MOMENTUM.toString())
                    .color(Enchantment.EnchantmentType.MOMENTUM.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.MOMENTUM.getMaterialRepresentation().toString()))
                            .color(NamedTextColor.GRAY))
            .build();


    private final ItemStack OVERLOAD_ENCHANTMENT_BUTTON = new ItemStackBuilder(Enchantment.EnchantmentType.OVERLOAD.getMaterialRepresentation())
            .displayName(Component.text(Enchantment.EnchantmentType.OVERLOAD.toString())
                    .color(Enchantment.EnchantmentType.OVERLOAD.getRarity().getColor()))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x " + Enchantment.EnchantmentType.OVERLOAD.getMaterialRepresentation().toString())
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack DEBUG_CLEAR_ENCHANTMENTS_BUTTON = new ItemStackBuilder(Material.BARRIER) // TODO: debug
            .displayName(Component.text("DEBUG: CLEAR ENCHANTMENTS")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
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