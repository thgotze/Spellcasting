package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.feature.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MaterialsMenu extends Menu {

    public MaterialsMenu(Player player) {
        super(5, Component.text("Materials"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());
        item(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));

        button(new Button(20, new ItemStackBuilder(Material.STONE_PICKAXE)
                .name(Component.text("Stone Pickaxe")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Cobblestone")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.STONE);
            }
        });

        button(new Button(21, new ItemStackBuilder(Material.IRON_PICKAXE)
                .name(Component.text("Iron Pickaxe")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Iron Ingot")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.IRON);
            }
        });

        button(new Button(22, new ItemStackBuilder(Material.GOLDEN_PICKAXE)
                .name(Component.text("Gold Pickaxe")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Gold Ingot")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.GOLD);
            }
        });

        button(new Button(23, new ItemStackBuilder(Material.DIAMOND_PICKAXE)
                .name(Component.text("Diamond Pickaxe")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Diamond")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.DIAMOND);
            }
        });

        button(new Button(24, new ItemStackBuilder(Material.NETHERITE_PICKAXE)
                .name(Component.text("Netherite Pickaxe")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Netherite Ingot")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.NETHERITE);
            }
        });

        button(new Button(36, MenuUtils.RETURN_BUTTON) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new PickaxeMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(43, new ItemStackBuilder(Material.WOODEN_PICKAXE) // TODO: debug
                .name(Component.text("DEBUG: Wooden Pickaxe")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x Oak Planks")
                                .color(NamedTextColor.GRAY))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradePickaxeMaterial(player, PickaxeMaterial.WOOD);
            }
        });

        button(new Button(44, new ItemStackBuilder(Material.CHEST) // TODO: debug
                .name(Component.text("DEBUG: GIVE RESOURCES")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                for (PickaxeMaterial pickaxeMaterial : PickaxeMaterial.values()) {
                    ItemStack upgradeMaterial = pickaxeMaterial.getUpgradeMaterial();
                    upgradeMaterial.setAmount(32); // TODO: placeholder amount
                    player.getInventory().addItem(upgradeMaterial);
                }
                SoundUtils.playUIClickSound(player);
            }
        });
    }

    private void upgradePickaxeMaterial(Player player, PickaxeMaterial clickedPickaxeMaterial) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();
        PickaxeMaterial nextTierPickaxe = pickaxeMaterial.getNextTier();

        if (clickedPickaxeMaterial != nextTierPickaxe) {
            player.sendMessage(Component.text("Cannot upgrade from " + pickaxeMaterial + " to " + clickedPickaxeMaterial + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        ItemStack upgradeMaterial = nextTierPickaxe.getUpgradeMaterial();
        int amount = 32; // TODO: placeholder amount
        upgradeMaterial.setAmount(amount);

        if (!playerInventory.containsAtLeast(upgradeMaterial, amount)) {
            player.sendMessage(Component.text("You don't have the required materials (" + amount + "x " + StringUtils.toTitleCase(upgradeMaterial.getType().toString()) + ") to upgrade your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        pickaxeData.setPickaxeMaterial(nextTierPickaxe);

        playerInventory.removeItem(heldItem);
        playerInventory.removeItem(upgradeMaterial);

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(pickaxe));
//        SoundUtils.playSuccessSound(player);
        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onClick(InventoryClickEvent event) {

    }
}