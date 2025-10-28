package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.util.SoundUtils;
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

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

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
                ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
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
            buttons(new Button(startingIndex++, abilityType.getMenuItem()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        ItemStack upgradeToken = abilityType.getUpgradeToken();
                        upgradeToken.setAmount(abilityType.getTokenAmount());
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
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
        if (pickaxe == null) return;
        PlayerInventory playerInventory = player.getInventory();

        ItemStack upgradeToken = clickedAbilityType.getUpgradeToken();
        int tokenAmount = clickedAbilityType.getTokenAmount();

        upgradeToken.setAmount(tokenAmount);

        if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
            player.sendMessage(text("You need " + tokenAmount + "x [")
                    .append(clickedAbilityType.getUpgradeTokenName())
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