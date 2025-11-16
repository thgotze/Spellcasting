package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
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

public class MaterialMenu extends Menu {

    private final Player player;

    public MaterialMenu(Player player) {
        super(5, text("Materials"), false);
        this.player = player;
        populate();
        open(player);
    }

    @Override
    protected void populate() {
        MenuUtils.setFrames(getInventory());

        setItem(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player));

        int[] slotIndexes = {19, 20, 21, 22, 23, 24, 25};
        int startingIndex = 0;
        for (PickaxeMaterial pickaxeMaterial : PickaxeMaterial.values()) {

            int tokenAmount = switch (pickaxeMaterial) { // TODO: placeholder amounts
                case WOOD -> 32;
                case STONE -> 32;
                case COPPER -> 32;
                case IRON -> 32;
                case DIAMOND -> 32;
                case NETHERITE -> 32;
            };

            setButton(new Button(slotIndexes[startingIndex++], new ItemStackBuilder(pickaxeMaterial.getPickaxeType())
                    .name(pickaxeMaterial.getFormattedPickaxeTypeName().color(pickaxeMaterial.getRarity().getColor()))
                    .lore(text(""),
                            text(StringUtils.convertToSmallFont("requirements")),
                            text(tokenAmount + "x [")
                                    .append(pickaxeMaterial.getFormattedUpgradeTokenName())
                                    .append(text("]")))
                    .hideAttributes()
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                    if (pickaxe == null) return;

                    ItemStack upgradeToken = ItemStack.of(pickaxeMaterial.getUpgradeTokenType());

                    upgradeToken.setAmount(tokenAmount);

                    if (event.getClick() == ClickType.DROP) { // TODO: debug
                        player.getInventory().addItem(upgradeToken);
                        SoundUtils.playUIClickSound(player);
                        return;
                    }

                    PlayerInventory playerInventory = player.getInventory();

                    PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                    PickaxeMaterial currentPickaxeMaterial = pickaxeData.getPickaxeMaterial();
                    PickaxeMaterial nextTierPickaxe = currentPickaxeMaterial.getNextTier();

                    if (pickaxeMaterial != nextTierPickaxe) {
                        player.sendMessage(text("Cannot upgrade from " + currentPickaxeMaterial + " to " + pickaxeMaterial + "!", RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    if (!playerInventory.containsAtLeast(upgradeToken, tokenAmount)) {
                        player.sendMessage(text("You need " + tokenAmount + "x [")
                                .append(pickaxeMaterial.getFormattedUpgradeTokenName())
                                .append(text("] to upgrade your pickaxe!"))
                                .color(RED));
                        SoundUtils.playErrorSound(player);
                        return;
                    }

                    playerInventory.removeItem(upgradeToken);
                    pickaxeData.setPickaxeMaterial(nextTierPickaxe);

                    ItemStack updatedPickaxe = PlayerPickaxeService.getPlayerPickaxe(player);
                    playerInventory.setItem(EquipmentSlot.HAND, updatedPickaxe);

                    getInventory().setItem(4, MenuUtils.cloneItemWithoutDamage(updatedPickaxe));
                    player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
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