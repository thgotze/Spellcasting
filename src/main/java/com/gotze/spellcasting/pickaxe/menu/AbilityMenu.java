package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.ItemModelManager;
import com.gotze.spellcasting.util.PermissionUtils;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class AbilityMenu extends Menu {

    private final Player player;

    public AbilityMenu(Player player) {
        super(5, text("Pickaxe Abilities"), false);
        this.player = player;
        populate();
        open(player);
    }

    @Override
    protected void populate() {
        MenuUtils.setFrames(getInventory());

        setButton(new Button(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player)) { // TODO: debug
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                if (event.getClick() != ClickType.DROP) return;
                if (!PermissionUtils.isAdmin(player)) return;
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
                    ItemStack upgradeToken = abilityType.getUpgradeToken();
                    int requiredTokenAmount = abilityType.getRequiredTokenAmount();
                    upgradeToken.setAmount(requiredTokenAmount);

                    if (event.getClick() == ClickType.DROP) {
                        if (!PermissionUtils.isAdmin(player)) return;
                        player.getInventory().addItem(upgradeToken);
                        SoundUtils.playUIClickSound(player);
                        return;
                    }

                    ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                    if (pickaxe == null) return;

                    if (ItemModelManager.hasActiveModification(player)) {
                        player.sendMessage(text("You currently have an ability active!", RED));
                        SoundUtils.playBassNoteBlockErrorSound(player);
                        return;
                    }

                    PlayerInventory playerInventory = player.getInventory();

                    if (!playerInventory.containsAtLeast(upgradeToken, requiredTokenAmount)) {
                        player.sendMessage(text("You need " + requiredTokenAmount + "x [", RED)
                                .append(abilityType.getUpgradeTokenName())
                                .append(text("] to upgrade this ability!", RED)));
                        SoundUtils.playBassNoteBlockErrorSound(player);
                        return;
                    }

                    PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                    Ability ability = pickaxeData.getAbility(abilityType);

                    if (ability == null) {
                        try {
                            Ability newAbility = abilityType.getAbilityClass().getDeclaredConstructor().newInstance();
                            pickaxeData.addAbility(newAbility);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (ability.isMaxLevel()) {
                            player.sendMessage(text("Cannot upgrade ")
                                    .append(abilityType.getFormattedName())
                                    .append(text(" past level " + abilityType.getMaxLevel() + "!", RED)));
                            SoundUtils.playBassNoteBlockErrorSound(player);
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

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }
}