package com.gotze.spellcasting.menu;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
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

        item(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player));

        int[] slotIndexes = {31, 20, 21, 22, 23, 24};
        int startingIndex = 0;
        for (PickaxeMaterial pickaxeMaterial : PickaxeMaterial.values()) {
            ItemStack upgradeToken = pickaxeMaterial.upgradeToken();

            int tokenAmount = switch (pickaxeMaterial) { // TODO: placeholder amounts
                case WOOD -> 32;
                case STONE -> 32;
                case IRON -> 32;
                case GOLD -> 32;
                case DIAMOND -> 32;
                case NETHERITE -> 32;
            };
            upgradeToken.setAmount(tokenAmount);

            button(new Button(slotIndexes[startingIndex++], new ItemStackBuilder(pickaxeMaterial.material())
                    .name(Component.text(pickaxeMaterial + " Pickaxe")
                            .color(NamedTextColor.AQUA))
                    .lore(Component.text(""),
                            Component.text(StringUtils.convertToSmallFont("requirements")),
                            Component.text(tokenAmount + "x [")
                                    .append(pickaxeMaterial.upgradeTokenName())
                                    .append(Component.text("]")))
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true).orElse(null);
                    if (pickaxe == null) return;
                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        ItemStack upgradeToken = pickaxeMaterial.upgradeToken();

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

                        SoundUtils.playUIClickSound(player);
                        return;
                    }

                    PlayerInventory playerInventory = player.getInventory();

                    PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);
                    PickaxeMaterial currentPickaxeMaterial = pickaxeData.pickaxeMaterial();
                    PickaxeMaterial nextTierPickaxe = currentPickaxeMaterial.nextTier();

                    if (pickaxeMaterial != nextTierPickaxe) {
                        player.sendMessage(Component.text("Cannot upgrade from " + currentPickaxeMaterial + " to " + pickaxeMaterial + "!")
                                .color(NamedTextColor.RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
                        player.sendMessage(Component.text("You don't have the required materials (" + tokenAmount + "x [")
                                .append(pickaxeMaterial.upgradeTokenName())
                                .append(Component.text("]) to upgrade your pickaxe!"))
                                .color(NamedTextColor.RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    playerInventory.removeItem(upgradeToken);

                    pickaxeData.pickaxeMaterial(nextTierPickaxe);
                    ItemStack updatedPickaxe = PlayerPickaxeService.playerPickaxe(player);
                    playerInventory.setItem(EquipmentSlot.HAND, updatedPickaxe);

                    getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
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