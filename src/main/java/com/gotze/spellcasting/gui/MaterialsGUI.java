package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.data.PlayerPickaxeService;
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
        gui.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
        gui.setItem(20, STONE_PICKAXE_BUTTON);
        gui.setItem(21, IRON_PICKAXE_BUTTON);
        gui.setItem(22, GOLD_PICKAXE_BUTTON);
        gui.setItem(23, DIAMOND_PICKAXE_BUTTON);
        gui.setItem(24, NETHERITE_PICKAXE_BUTTON);
        gui.setItem(43, WOODEN_PICKAXE_BUTTON); // TODO: remove later
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
        if (!(event.getInventory().getHolder() instanceof MaterialsGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;

        int slot = event.getSlot();

        switch (slot) {
            case 20, 21, 22, 23, 24 -> upgradePickaxeMaterial(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            // TODO: remove case 43 and 44
            case 43 -> upgradePickaxeMaterial(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 44 -> {
                playerInventory.addItem(ItemStack.of(Material.OAK_PLANKS, 32),
                        ItemStack.of(Material.COBBLESTONE, 32),
                        ItemStack.of(Material.IRON_INGOT, 32),
                        ItemStack.of(Material.GOLD_INGOT, 32),
                        ItemStack.of(Material.DIAMOND, 32),
                        ItemStack.of(Material.NETHERITE_INGOT, 32));
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private void upgradePickaxeMaterial(Player player, PlayerInventory playerInventory, Material clickedUpgrade, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, playerInventory.getItemInMainHand())) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }
        ItemStack playerPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);

        Material nextTierPickaxe = switch (playerPickaxe.getType()) {
            case WOODEN_PICKAXE -> Material.STONE_PICKAXE;
            case STONE_PICKAXE -> Material.IRON_PICKAXE;
            case IRON_PICKAXE -> Material.GOLDEN_PICKAXE;
            case GOLDEN_PICKAXE -> Material.DIAMOND_PICKAXE;
            case DIAMOND_PICKAXE -> Material.NETHERITE_PICKAXE;
            case NETHERITE_PICKAXE -> Material.WOODEN_PICKAXE; // TODO: remove later
            default -> null;
        };

        if (clickedUpgrade != nextTierPickaxe) {
            player.sendMessage(Component.text("Cannot upgrade from " + playerPickaxe.getType() + " to " + nextTierPickaxe + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        Material requiredMaterial = switch (nextTierPickaxe) {
            case STONE_PICKAXE -> Material.COBBLESTONE;
            case IRON_PICKAXE -> Material.IRON_INGOT;
            case GOLDEN_PICKAXE -> Material.GOLD_INGOT;
            case DIAMOND_PICKAXE -> Material.DIAMOND;
            case NETHERITE_PICKAXE -> Material.NETHERITE_INGOT;
            case WOODEN_PICKAXE -> Material.OAK_PLANKS; // TODO: remove later
            default -> null;
        };

        final int REQUIRED_AMOUNT = 32;
        final ItemStack REQUIRED_MATERIALS = ItemStack.of(requiredMaterial, REQUIRED_AMOUNT);

        if (!playerInventory.containsAtLeast(REQUIRED_MATERIALS, REQUIRED_AMOUNT)) {
            player.sendMessage(Component.text("You don't have the required materials (" + REQUIRED_AMOUNT + "x " + requiredMaterial.toString().toLowerCase() + ") " + "to upgrade your pickaxe!"));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their hand
        // 2. they have enough of the required material

        playerInventory.removeItem(playerPickaxe);
        playerInventory.removeItem(REQUIRED_MATERIALS);

        PlayerPickaxeService.upgradePlayerPickaxeMaterial(player, nextTierPickaxe);
        playerInventory.addItem(PlayerPickaxeService.getPlayerPickaxe(player));

        clickedInventory.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player));
        SoundUtils.playSuccessSound(player);
    }

    private final ItemStack WOODEN_PICKAXE_BUTTON = new ItemStackBuilder(Material.WOODEN_PICKAXE) // TODO remove later
            .displayName(Component.text("DEBUG: Wooden Pickaxe")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Oak Planks")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
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