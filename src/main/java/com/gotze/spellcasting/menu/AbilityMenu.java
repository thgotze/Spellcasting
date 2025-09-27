package com.gotze.spellcasting.menu;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
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

        int startingIndex = 19;
        for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
            ItemStack upgradeToken = abilityType.getUpgradeToken();

            int tokenAmount = switch (abilityType.getRarity()) { // TODO: placeholder amounts
                case COMMON -> 16;
                case UNCOMMON -> 8;
                case RARE -> 4;
                case EPIC -> 2;
                case LEGENDARY -> 1;
            };

            buttons(new Button(startingIndex++, new ItemStackBuilder(upgradeToken)
                    .name(abilityType.getColoredName())
                    .lore(Component.text(""),
                            Component.text(StringUtils.convertToSmallFont("requirements")),
                            Component.text(tokenAmount + "x [")
                                    .append(abilityType.getUpgradeTokenName())
                                    .append(Component.text("]")))
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    upgradeAbility(player, abilityType);
                }
            });
        }

        button(new Button(36, MenuUtils.RETURN_ITEM) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new PickaxeMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(43, new ItemStackBuilder(Material.BARRIER) // TODO: debug
                .name(Component.text("DEBUG: CLEAR ABILITIES")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

                PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
                pickaxeData.removeAbilities();

                ItemStack heldItem = player.getInventory().getItemInMainHand();
                heldItem.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(heldItem));
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
                for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
                    ItemStack upgradeToken = abilityType.getUpgradeToken();

                    int tokenAmount = switch (abilityType.getRarity()) { // TODO: placeholder amounts
                        case COMMON -> 16;
                        case UNCOMMON -> 8;
                        case RARE -> 4;
                        case EPIC -> 2;
                        case LEGENDARY -> 1;
                    };
                    upgradeToken.setAmount(tokenAmount);
                    player.getInventory().addItem(upgradeToken);
                }
                SoundUtils.playUIClickSound(player);
            }
        });

    }

    private void upgradeAbility(Player player, Ability.AbilityType clickedAbilityType) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        ItemStack upgradeToken = clickedAbilityType.getUpgradeToken();
        int tokenAmount = switch (clickedAbilityType.getRarity()) { // TODO: placeholder amounts
            case COMMON -> 16;
            case UNCOMMON -> 8;
            case RARE -> 4;
            case EPIC -> 2;
            case LEGENDARY -> 1;
        };
        upgradeToken.setAmount(tokenAmount);

        if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
            player.sendMessage(Component.text("You don't have the required tokens (" + tokenAmount + "x [")
                    .append(clickedAbilityType.getUpgradeTokenName())
                    .append(Component.text("]) to upgrade this ability!"))
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        Ability ability = pickaxeData.getAbility(clickedAbilityType);

        if (ability == null) {
            try {
                Ability newAbility = clickedAbilityType.getAbilityClass().getDeclaredConstructor().newInstance();
                pickaxeData.addAbility(newAbility);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (ability.isMaxLevel()) {
                player.sendMessage(Component.text("Cannot upgrade ")
                        .append(clickedAbilityType.getColoredName())
                        .append(Component.text(" past level " + clickedAbilityType.getMaxLevel() + "!"))
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