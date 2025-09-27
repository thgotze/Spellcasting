package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.feature.pickaxe.ability.Ability;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AbilityMenu extends Menu {

    public AbilityMenu(Player player) {
        super(5, Component.text("Abilities"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());
        item(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));

        button(new Button(36, MenuUtils.RETURN_BUTTON) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new PickaxeMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });
        // TODO: make a for loop populating these buttons as they have a lot of similarities
        button(new Button(21, new ItemStackBuilder(Ability.AbilityType.SLICE.getUpgradeToken().getType())
                .name(Component.text(Ability.AbilityType.SLICE.toString())
                        .color(Ability.AbilityType.SLICE.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Ability.AbilityType.SLICE.getUpgradeToken().getType().toString()))
                                .color(Ability.AbilityType.SLICE.getRarity().getColor()))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeAbility(player, Ability.AbilityType.SLICE);
            }
        });

        button(new Button(22, new ItemStackBuilder(Ability.AbilityType.LASER.getUpgradeToken().getType())
                .name(Component.text(Ability.AbilityType.LASER.toString())
                        .color(Ability.AbilityType.LASER.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Ability.AbilityType.LASER.getUpgradeToken().getType().toString()))
                                .color(Ability.AbilityType.LASER.getRarity().getColor()))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeAbility(player, Ability.AbilityType.LASER);
            }
        });

        button(new Button(23, new ItemStackBuilder(Ability.AbilityType.BAZOOKA.getUpgradeToken().getType())
                .name(Component.text(Ability.AbilityType.BAZOOKA.toString())
                        .color(Ability.AbilityType.BAZOOKA.getRarity().getColor()))
                .lore(Component.text(""),
                        Component.text(StringUtils.convertToSmallFont("requirements")),
                        Component.text("32x " + StringUtils.toTitleCase(Ability.AbilityType.BAZOOKA.getUpgradeToken().getType().toString()))
                                .color(Ability.AbilityType.BAZOOKA.getRarity().getColor()))
                .hideAttributes()
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                upgradeAbility(player, Ability.AbilityType.BAZOOKA);
            }
        });

        button(new Button(43, new ItemStackBuilder(Material.BARRIER) // TODO: debug
                .name(Component.text("DEBUG: CLEAR ABILITIES").decorate(TextDecoration.BOLD)
                        .color(NamedTextColor.RED))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;
                ItemStack heldItem = player.getInventory().getItemInMainHand();

                PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
                pickaxeData.removeAbilities();

                heldItem.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(heldItem));
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(44, new ItemStackBuilder(Material.CHEST) // TODO: debug
                .name(Component.text("DEBUG: GIVE RESOURCES")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
                    ItemStack upgradeToken = abilityType.getUpgradeToken();

                    final int amount = switch (abilityType.getRarity()) { // TODO: placeholder amounts
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

    private void upgradeAbility(Player player, Ability.AbilityType clickedAbility) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        ItemStack upgradeToken = clickedAbility.getUpgradeToken();

        int amount = switch (clickedAbility.getRarity()) { // TODO: placeholder amounts
            case COMMON -> 16;
            case UNCOMMON -> 8;
            case RARE -> 4;
            case EPIC -> 2;
            case LEGENDARY -> 1;
        };
        upgradeToken.setAmount(amount);

        if (!playerInventory.containsAtLeast(upgradeToken, amount)) {
            player.sendMessage(Component.text("You don't have the required tokens (" + amount + "x " + StringUtils.toTitleCase(upgradeToken.getType().toString()) + ") to upgrade this ability!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        Ability ability = pickaxeData.getAbility(clickedAbility);

        if (ability == null) {
            try {
                Ability newAbility = clickedAbility.getAbilityClass().getDeclaredConstructor().newInstance();
                pickaxeData.addAbility(newAbility);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (ability.isMaxLevel()) {
                player.sendMessage(Component.text("Cannot upgrade " + clickedAbility + " past level " + clickedAbility.getMaxLevel() + "!")
                        .color(NamedTextColor.RED));
                SoundUtils.playErrorSound(player);
                return;
            } else {
                ability.increaseLevel();
            }
        }

        playerInventory.removeItem(upgradeToken);
        playerInventory.removeItem(heldItem);

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
        playerInventory.addItem(pickaxe);

        getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(pickaxe));
        SoundUtils.playSuccessSound(player);
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