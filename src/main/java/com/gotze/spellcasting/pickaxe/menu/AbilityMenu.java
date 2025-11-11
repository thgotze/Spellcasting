package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.data.PickaxeData;
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
        super(5, text("Abilities"), false);
        populate(player);
        open(player);
    }

    @Override
    protected void populate(Player player) {
        MenuUtils.setFrames(getInventory());

        setButton(new Button(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player)) { // TODO: debug
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                if (event.getClick() != ClickType.DROP) return;
                ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                if (pickaxe == null) return;

                PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                pickaxeData.removeAbilities();

                ItemStack updatedPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);
                player.getInventory().setItem(EquipmentSlot.HAND, updatedPickaxe);

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
                SoundUtils.playUIClickSound(player);
            }
        });

        int startingIndex = 9;
        for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
            this.setButton(new Button(startingIndex++, abilityType.getMenuItem()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        ItemStack upgradeToken = abilityType.getUpgradeToken();
                        upgradeToken.setAmount(abilityType.getRequiredTokenAmount());
                        player.getInventory().addItem(upgradeToken);
                        SoundUtils.playUIClickSound(player);
                        return;
                    }
                    upgradeAbility(player, abilityType);
                }
            });
        }

        setButton(new Button(36, MenuUtils.RETURN_ITEM) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
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
        int requiredTokenAmount = clickedAbilityType.getRequiredTokenAmount();

        upgradeToken.setAmount(requiredTokenAmount);

        if (!playerInventory.containsAtLeast(upgradeToken, requiredTokenAmount)) {
            player.sendMessage(text("You need " + requiredTokenAmount + "x [")
                    .append(clickedAbilityType.getUpgradeTokenName())
                    .append(text("] to upgrade this ability!"))
                    .color(RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
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
        ItemStack updatedPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);
        playerInventory.setItem(EquipmentSlot.HAND, updatedPickaxe);

        getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
        player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }
}