package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
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

public class MaterialsGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Materials"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));
        gui.setItem(20, STONE_PICKAXE_BUTTON);
        gui.setItem(21, IRON_PICKAXE_BUTTON);
        gui.setItem(22, GOLD_PICKAXE_BUTTON);
        gui.setItem(23, DIAMOND_PICKAXE_BUTTON);
        gui.setItem(24, NETHERITE_PICKAXE_BUTTON);
        gui.setItem(43, WOODEN_PICKAXE_BUTTON); // TODO: remove later
        gui.setItem(44, DEBUG_GIVE_RESOURCES); // TODO: remove later
        gui.setItem(36, GUIUtils.RETURN_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MaterialsGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;

        int slot = event.getSlot();

        switch (slot) {
            case 20, 21, 22, 23, 24 ->
                    upgradePickaxeMaterial(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            // TODO: remove case 43 and 44
            case 43 ->
                    upgradePickaxeMaterial(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 44 -> {
                playerInventory.addItem(ItemStack.of(Material.COBBLESTONE, 32),
                        ItemStack.of(Material.IRON_INGOT, 32),
                        ItemStack.of(Material.GOLD_INGOT, 32),
                        ItemStack.of(Material.DIAMOND, 32),
                        ItemStack.of(Material.NETHERITE_INGOT, 32),
                        ItemStack.of(Material.OAK_PLANKS, 32));
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private void upgradePickaxeMaterial(Player player, PlayerInventory playerInventory, Material clickedUpgrade, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }
        ItemStack heldItem = playerInventory.getItemInMainHand();

        PickaxeMaterial nextTierPickaxe = switch (heldItem.getType()) {
            case WOODEN_PICKAXE -> PickaxeMaterial.STONE;
            case STONE_PICKAXE -> PickaxeMaterial.IRON;
            case IRON_PICKAXE -> PickaxeMaterial.GOLD;
            case GOLDEN_PICKAXE -> PickaxeMaterial.DIAMOND;
            case DIAMOND_PICKAXE -> PickaxeMaterial.NETHERITE;
            case NETHERITE_PICKAXE -> PickaxeMaterial.WOOD; // TODO: remove later
            default -> throw new IllegalArgumentException("Unsupported pickaxe material: " + heldItem.getType());
        };

        if (clickedUpgrade != nextTierPickaxe.getType()) {
            player.sendMessage(Component.text("Cannot upgrade from " + heldItem.getType() + " to " + clickedUpgrade + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        Material requiredMaterial = switch (nextTierPickaxe) {
            case STONE -> Material.COBBLESTONE;
            case IRON -> Material.IRON_INGOT;
            case GOLD -> Material.GOLD_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case NETHERITE -> Material.NETHERITE_INGOT;
            case WOOD -> Material.OAK_PLANKS; // TODO: remove later
        };

        final int REQUIRED_AMOUNT = 32;
        final ItemStack REQUIRED_MATERIALS = ItemStack.of(requiredMaterial, REQUIRED_AMOUNT);

        if (!playerInventory.containsAtLeast(REQUIRED_MATERIALS, REQUIRED_AMOUNT)) {
            player.sendMessage(Component.text("You don't have the required materials (" + REQUIRED_AMOUNT + "x " + requiredMaterial.toString().toLowerCase() + ") " + "to upgrade your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their main hand
        // 2. they have enough of the required material

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        pickaxeData.setPickaxeMaterial(nextTierPickaxe);

        playerInventory.removeItem(heldItem);
        playerInventory.removeItem(REQUIRED_MATERIALS);

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        clickedInventory.setItem(4, GUIUtils.cloneItemWithoutDamage(pickaxe));
        SoundUtils.playSuccessSound(player);
    }

    private final ItemStack WOODEN_PICKAXE_BUTTON = new ItemStackBuilder(Material.WOODEN_PICKAXE) // TODO: remove later
            .displayName(Component.text("DEBUG: Wooden Pickaxe")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Oak Planks")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack DEBUG_GIVE_RESOURCES = new ItemStackBuilder(Material.CHEST) // TODO: remove later
            .displayName(Component.text("DEBUG: GIVE RESOURCES")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
            .build();

    private final ItemStack STONE_PICKAXE_BUTTON = new ItemStackBuilder(Material.STONE_PICKAXE)
            .displayName(Component.text("Stone Pickaxe")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Cobblestone")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack IRON_PICKAXE_BUTTON = new ItemStackBuilder(Material.IRON_PICKAXE)
            .displayName(Component.text("Iron Pickaxe")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Iron Ingot")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack GOLD_PICKAXE_BUTTON = new ItemStackBuilder(Material.GOLDEN_PICKAXE)
            .displayName(Component.text("Gold Pickaxe")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Gold Ingot")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack DIAMOND_PICKAXE_BUTTON = new ItemStackBuilder(Material.DIAMOND_PICKAXE)
            .displayName(Component.text("Diamond Pickaxe")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Diamond")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack NETHERITE_PICKAXE_BUTTON = new ItemStackBuilder(Material.NETHERITE_PICKAXE)
            .displayName(Component.text("Netherite Pickaxe")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Netherite Ingot")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();


    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}