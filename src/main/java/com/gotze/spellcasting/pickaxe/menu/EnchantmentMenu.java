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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class EnchantmentMenu extends Menu {

    public EnchantmentMenu(Player player) {
        super(5, text("Enchantments"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());

        button(new Button(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player)) {
            @Override
            public void onClick(InventoryClickEvent event) {
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
            button(new Button(startingIndex++, enchantmentType.getMenuItem()) {
                @Override
                public void onClick(InventoryClickEvent event) {
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

        button(new Button(36, MenuUtils.RETURN_ITEM) {
            @Override
            public void onClick(InventoryClickEvent event) {
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
            player.sendMessage(text("You need " + requiredTokenAmount + "x [")
                    .append(clickedEnchantmentType.getUpgradeTokenName())
                    .append(text("] to enchant your pickaxe!"))
                    .color(RED));
            SoundUtils.playErrorSound(player);
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
                        .append(text(" past level " + clickedEnchantmentType.getMaxLevel() + "!"))
                        .color(RED));
                SoundUtils.playErrorSound(player);
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
    protected void onOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onClick(InventoryClickEvent event) {

    }
}