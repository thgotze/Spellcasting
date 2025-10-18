package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class AbilityMenu extends Menu {

    public AbilityMenu(Player player) {
        super(5, text("Abilities"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());

        button(new Button(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player)) { // TODO: debug
            @Override
            public void onClick(InventoryClickEvent event) {
                if (event.getClick() != ClickType.DROP) return;
                ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true).orElse(null);
                if (pickaxe == null) return;

                PlayerPickaxeService.removeAbilities(player);

                ItemStack updatedPickaxe = PlayerPickaxeService.playerPickaxe(player);
                player.getInventory().setItem(EquipmentSlot.HAND, updatedPickaxe);

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
                SoundUtils.playUIClickSound(player);
            }
        });

        int startingIndex = 19;
        for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
            int tokenAmount = switch (abilityType.getRarity()) { // TODO: placeholder amounts
                case COMMON -> 16;
                case UNCOMMON -> 8;
                case RARE -> 4;
                case EPIC -> 2;
                case LEGENDARY -> 1;
            };

            buttons(new Button(startingIndex++, new ItemStackBuilder(abilityType.getUpgradeToken())
                    .name(abilityType.getFormattedName())
                    .lore(empty(),
                            text(StringUtils.convertToSmallFont("requirements")),
                            text(tokenAmount + "x [")
                                    .append(abilityType.upgradeTokenName())
                                    .append(text("]")))
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        int tokenAmount = switch (abilityType.getRarity()) { // TODO: placeholder amounts
                            case COMMON -> 16;
                            case UNCOMMON -> 8;
                            case RARE -> 4;
                            case EPIC -> 2;
                            case LEGENDARY -> 1;
                        };
                        ItemStack upgradeToken = abilityType.getUpgradeToken();
                        upgradeToken.setAmount(tokenAmount);
                        player.getInventory().addItem(upgradeToken);
                        SoundUtils.playUIClickSound(player);
                        return;
                    }
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
    }

    private void upgradeAbility(Player player, Ability.AbilityType clickedAbilityType) {
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true).orElse(null);
        if (pickaxe == null) return;
        PlayerInventory playerInventory = player.getInventory();

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
            player.sendMessage(text("You need " + tokenAmount + "x [")
                    .append(clickedAbilityType.upgradeTokenName())
                    .append(text("] to upgrade this ability!"))
                    .color(RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);
        Ability ability = pickaxeData.getAbility(clickedAbilityType);

        if (ability == null) {
            try {
                Ability newAbility = clickedAbilityType.getAbilityClass().getDeclaredConstructor().newInstance();
                PlayerPickaxeService.addAbility(player, newAbility);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (ability.isMaxLevel()) {
                player.sendMessage(text("Cannot upgrade ")
                        .append(clickedAbilityType.getFormattedName())
                        .append(text(" past level " + clickedAbilityType.getMaxLevel() + "!"))
                        .color(RED));
                SoundUtils.playErrorSound(player);
                return;
            } else {
                ability.increaseLevel();
            }
        }

        playerInventory.removeItem(upgradeToken);
        ItemStack updatedPickaxe = PlayerPickaxeService.playerPickaxe(player);
        playerInventory.setItem(EquipmentSlot.HAND, updatedPickaxe);

        getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
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