package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.feature.pickaxe.enchantment.Enchantment;
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

public class EnchantmentMenu extends Menu {

    public EnchantmentMenu(Player player) {
        super(5, Component.text("Enchantments"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());
        item(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));
        item(5, MenuUtils.FRAME);

        // TODO: make a for loop populating these buttons as they have a lot of similarities
        button(new Button(19, new ItemStackBuilder(Enchantment.EnchantmentType.EFFICIENCY.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.EFFICIENCY.toString())
                        .color(Enchantment.EnchantmentType.EFFICIENCY.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.EFFICIENCY.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.EFFICIENCY.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.EFFICIENCY);
            }
        });

        button(new Button(20, new ItemStackBuilder(Enchantment.EnchantmentType.FORTUNE.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.FORTUNE.toString())
                        .color(Enchantment.EnchantmentType.FORTUNE.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.FORTUNE.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.FORTUNE.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.FORTUNE);
            }
        });

        button(new Button(21, new ItemStackBuilder(Enchantment.EnchantmentType.UNBREAKING.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.UNBREAKING.toString())
                        .color(Enchantment.EnchantmentType.UNBREAKING.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.UNBREAKING.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.UNBREAKING.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.UNBREAKING);
            }
        });

        button(new Button(22, new ItemStackBuilder(Enchantment.EnchantmentType.UNCOVER.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.UNCOVER.toString())
                        .color(Enchantment.EnchantmentType.UNCOVER.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.UNCOVER.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.UNCOVER.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.UNCOVER);
            }
        });

        button(new Button(23, new ItemStackBuilder(Enchantment.EnchantmentType.MOMENTUM.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.MOMENTUM.toString())
                        .color(Enchantment.EnchantmentType.MOMENTUM.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.MOMENTUM.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.MOMENTUM.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.MOMENTUM);
            }
        });

        button(new Button(24, new ItemStackBuilder(Enchantment.EnchantmentType.PHANTOM_QUARRY.getUpgradeToken().getType())
                .name(Component.text(Enchantment.EnchantmentType.PHANTOM_QUARRY.toString())
                        .color(Enchantment.EnchantmentType.PHANTOM_QUARRY.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Enchantment.EnchantmentType.PHANTOM_QUARRY.getUpgradeToken().getType().toString()))
                                .color(Enchantment.EnchantmentType.PHANTOM_QUARRY.getRarity().getColor()))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeEnchantment(player, Enchantment.EnchantmentType.PHANTOM_QUARRY);
            }
        });

        button(new Button(36, MenuUtils.RETURN_BUTTON) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new PickaxeMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(43, new ItemStackBuilder(Material.BARRIER) // TODO: debug
                .name(Component.text("DEBUG: CLEAR ENCHANTMENTS")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

                PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
                pickaxeData.removeEnchantments();

                PlayerInventory playerInventory = player.getInventory();
                playerInventory.remove(playerInventory.getItemInMainHand());

                ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
                playerInventory.addItem(pickaxe);

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(pickaxe));
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(44, new ItemStackBuilder(Material.CHEST) // TODO: debug
                .name(Component.text("DEBUG: GIVE TOKENS")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
                    ItemStack upgradeToken = enchantmentType.getUpgradeToken();

                    final int amount = switch (enchantmentType.getRarity()) { // TODO: placeholder amounts
                        case COMMON -> 16;
                        case UNCOMMON -> 8;
                        case RARE -> 4;
                        case EPIC -> 2;
                        case LEGENDARY -> 1;
                    };
                    upgradeToken.setAmount(amount);
                    player.getInventory().addItem(upgradeToken);
                }
                SoundUtils.playUIClickSound(player);
            }
        });
    }

    private void upgradeEnchantment(Player player, Enchantment.EnchantmentType clickedEnchantment) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        ItemStack upgradeToken = clickedEnchantment.getUpgradeToken();

        int amount = switch (clickedEnchantment.getRarity()) { // TODO: placeholder amounts
            case COMMON -> 16;
            case UNCOMMON -> 8;
            case RARE -> 4;
            case EPIC -> 2;
            case LEGENDARY -> 1;
        };
        upgradeToken.setAmount(amount);

        if (!playerInventory.containsAtLeast(upgradeToken, amount)) {
            player.sendMessage(Component.text("You don't have the required tokens (" + amount + "x " + StringUtils.toTitleCase(upgradeToken.getType().toString()) + ") to enchant your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        Enchantment enchantment = pickaxeData.getEnchantment(clickedEnchantment);

        if (enchantment == null) {
            try {
                Enchantment newEnchantment = clickedEnchantment.getEnchantmentClass().getDeclaredConstructor().newInstance();
                pickaxeData.addEnchantment(newEnchantment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (enchantment.isMaxLevel()) {
                player.sendMessage(Component.text("Cannot upgrade " + enchantment + " past level " + enchantment.getMaxLevel() + "!")
                        .color(NamedTextColor.RED));
                SoundUtils.playErrorSound(player);
                return;
            } else {
                enchantment.increaseLevel();
            }
        }

        playerInventory.removeItem(upgradeToken);
        playerInventory.remove(heldItem);

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(pickaxe));
//        SoundUtils.playSuccessSound(player);
        player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
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