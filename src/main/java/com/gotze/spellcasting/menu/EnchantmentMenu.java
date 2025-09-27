package com.gotze.spellcasting.menu;

import com.gotze.spellcasting.enchantment.Enchantment;
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

        int startingIndex = 19;
        for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
            ItemStack upgradeToken = enchantmentType.getUpgradeToken();

            int tokenAmount = switch (enchantmentType.getRarity()) { // TODO: placeholder amounts
                case COMMON -> 16;
                case UNCOMMON -> 8;
                case RARE -> 4;
                case EPIC -> 2;
                case LEGENDARY -> 1;
            };

            button(new Button(startingIndex++, new ItemStackBuilder(upgradeToken)
                    .name(enchantmentType.getColoredName())
                    .lore(Component.text(""),
                            Component.text(StringUtils.convertToSmallFont("requirements")),
                            Component.text(tokenAmount + "x [")
                                    .append(enchantmentType.getUpgradeTokenName())
                                    .append(Component.text("]")))
                    .hideAttributes()
                    .hideAdditionalTooltip()
                    .build()) {
                @Override
                public void onClick (InventoryClickEvent event){
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

                    int tokenAmount = switch (enchantmentType.getRarity()) { // TODO: placeholder amounts
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

    private void upgradeEnchantment(Player player, Enchantment.EnchantmentType clickedEnchantmentType) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack heldItem = playerInventory.getItemInMainHand();

        ItemStack upgradeToken = clickedEnchantmentType.getUpgradeToken();
        int tokenAmount = switch (clickedEnchantmentType.getRarity()) { // TODO: placeholder amounts
            case COMMON -> 16;
            case UNCOMMON -> 8;
            case RARE -> 4;
            case EPIC -> 2;
            case LEGENDARY -> 1;
        };
        upgradeToken.setAmount(tokenAmount);

        if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
            player.sendMessage(Component.text("You don't have the required tokens (" + tokenAmount + "x [")
                    .append(clickedEnchantmentType.getUpgradeTokenName())
                    .append(Component.text("]) to enchant your pickaxe!"))
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
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
                player.sendMessage(Component.text("Cannot upgrade ")
                        .append(clickedEnchantmentType.getColoredName())
                        .append(Component.text(" past level " + clickedEnchantmentType.getMaxLevel() + "!"))
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