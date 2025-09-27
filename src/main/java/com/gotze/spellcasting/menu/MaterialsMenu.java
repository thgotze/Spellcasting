package com.gotze.spellcasting.menu;

import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
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

public class MaterialsMenu extends Menu {

    public MaterialsMenu(Player player) {
        super(5, Component.text("Materials"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());
        item(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));

        int[] slotIndexes = {40, 20, 21, 22, 23, 24};
        int startingIndex = 0;
        for (PickaxeMaterial pickaxeMaterial : PickaxeMaterial.values()) {
            ItemStack upgradeToken = pickaxeMaterial.getUpgradeToken();

            int tokenAmount = switch (pickaxeMaterial) { // TODO: placeholder amounts
                case WOOD -> 32;
                case STONE -> 32;
                case IRON -> 32;
                case GOLD -> 32;
                case DIAMOND -> 32;
                case NETHERITE -> 32;
            };
            upgradeToken.setAmount(tokenAmount);

            button(new Button(slotIndexes[startingIndex++], new ItemStackBuilder(pickaxeMaterial.getType())
                    .name(Component.text(pickaxeMaterial + " Pickaxe")
                            .color(NamedTextColor.AQUA))
                    .lore(Component.text(""),
                            Component.text(StringUtils.convertToSmallFont("requirements")),
                            Component.text(tokenAmount + "x [")
                                    .append(pickaxeMaterial.getUpgradeTokenName())
                                    .append(Component.text("]")))
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, true)) return;

                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack heldItem = playerInventory.getItemInMainHand();

                    PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
                    PickaxeMaterial currentPickaxeMaterial = pickaxeData.getPickaxeMaterial();
                    PickaxeMaterial nextTierPickaxe = currentPickaxeMaterial.getNextTier();

                    if (pickaxeMaterial != nextTierPickaxe) {
                        player.sendMessage(Component.text("Cannot upgrade from " + currentPickaxeMaterial + " to " + pickaxeMaterial + "!")
                                .color(NamedTextColor.RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
                        player.sendMessage(Component.text("You don't have the required materials (" + tokenAmount + "x [")
                                        .append(pickaxeMaterial.getUpgradeTokenName())
                                        .append(Component.text("]) to upgrade your pickaxe!"))
                                .color(NamedTextColor.RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    pickaxeData.setPickaxeMaterial(nextTierPickaxe);

                    playerInventory.removeItem(heldItem);
                    playerInventory.removeItem(upgradeToken);

                    ItemStack pickaxe = PlayerPickaxeService.getPickaxe(player);
                    playerInventory.addItem(pickaxe);

                    getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(pickaxe));

                    player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
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

        button(new Button(44, new ItemStackBuilder(Material.CHEST) // TODO: debug
                .name(Component.text("DEBUG: GIVE TOKENS")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                for (PickaxeMaterial pickaxeMaterial : PickaxeMaterial.values()) {
                    ItemStack upgradeToken = pickaxeMaterial.getUpgradeToken();

                    int tokenAmount = switch (pickaxeMaterial) { // TODO: placeholder amounts
                        case WOOD -> 32;
                        case STONE -> 32;
                        case IRON -> 32;
                        case GOLD -> 32;
                        case DIAMOND -> 32;
                        case NETHERITE -> 32;
                    };
                    upgradeToken.setAmount(tokenAmount);
                    player.getInventory().addItem(upgradeToken);
                }
                SoundUtils.playUIClickSound(player);
            }
        });
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