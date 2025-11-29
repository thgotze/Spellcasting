package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
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

public class EnchantmentMenu extends Menu {

    private final Player player;

    public EnchantmentMenu(Player player) {
        super(5, text("Enchantments"), false);
        this.player = player;
        populate();
        open(player);
    }

    @Override
    protected void populate() {
        MenuUtils.setFrames(getInventory());

        setButton(new Button(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player)) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                if (pickaxe == null) return;

                PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                pickaxeData.removeEnchantments();

                ItemStack updatedPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);
                player.getInventory().setItem(EquipmentSlot.HAND, updatedPickaxe);

                getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
                SoundUtils.playUIClickSound(player);
            }
        });

        int startingIndex = 9;
        for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
            setButton(new Button(startingIndex++, enchantmentType.getMenuItem()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        ItemStack upgradeToken = enchantmentType.getUpgradeToken();
                        upgradeToken.setAmount(enchantmentType.getRequiredTokenAmount());
                        player.getInventory().addItem(upgradeToken);
                        SoundUtils.playUIClickSound(player);
                        return;
                    }
                    upgradeEnchantment(player, enchantmentType);
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

    private void upgradeEnchantment(Player player, Enchantment.EnchantmentType clickedEnchantmentType) {
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
        if (pickaxe == null) return;

        PlayerInventory playerInventory = player.getInventory();

        ItemStack upgradeToken = clickedEnchantmentType.getUpgradeToken();
        int requiredTokenAmount = clickedEnchantmentType.getRequiredTokenAmount();

        upgradeToken.setAmount(requiredTokenAmount);

        if (!playerInventory.containsAtLeast(upgradeToken, requiredTokenAmount)) {
            player.sendMessage(text("You need " + requiredTokenAmount + "x [", RED)
                    .append(clickedEnchantmentType.getUpgradeTokenName())
                    .append(text("] to enchant your pickaxe!", RED)));
            SoundUtils.playBassNoteBlockErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        Enchantment enchantment = pickaxeData.getEnchantment(clickedEnchantmentType);

        if (enchantment == null) {
            try {
                Enchantment newEnchantment = clickedEnchantmentType.getEnchantmentClass().getDeclaredConstructor().newInstance();
                pickaxeData.addEnchantment(newEnchantment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            if (enchantment.isMaxLevel()) {
                player.sendMessage(text("Cannot upgrade ")
                        .append(clickedEnchantmentType.getFormattedName())
                        .append(text(" past level " + clickedEnchantmentType.getMaxLevel() + "!", RED)));
                SoundUtils.playBassNoteBlockErrorSound(player);
                return;
            } else {
                enchantment.increaseLevel();
            }
        }

        playerInventory.removeItem(upgradeToken);
        ItemStack updatedPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);
        player.getInventory().setItem(EquipmentSlot.HAND, updatedPickaxe);

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
    protected void onInventoryDrag(InventoryDragEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }
}