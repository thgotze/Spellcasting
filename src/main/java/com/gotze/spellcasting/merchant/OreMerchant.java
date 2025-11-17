package com.gotze.spellcasting.merchant;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class OreMerchant extends Merchant {

    private static final int[] TRADE_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    private static final int[] BOTTOM_SLOTS = {
            36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    private static final EnumMap<Material, Double> SELLABLE_ITEMS = new EnumMap<>(Material.class);

    static {
        SELLABLE_ITEMS.put(Material.COBBLESTONE, 1.00);
        SELLABLE_ITEMS.put(Material.RAW_COPPER, 2.50);
        SELLABLE_ITEMS.put(Material.COPPER_INGOT, 10.00);
        SELLABLE_ITEMS.put(Material.RAW_IRON, 12.50);
        SELLABLE_ITEMS.put(Material.IRON_INGOT, 50.00);
        SELLABLE_ITEMS.put(Material.RAW_GOLD, 20.00);
        SELLABLE_ITEMS.put(Material.GOLD_INGOT, 80.00);
        SELLABLE_ITEMS.put(Material.DIAMOND, 100.00);
    }

    public OreMerchant() {
        super(5, text("Ore Merchant"), true,
                "Ore Merchant", new Location(Bukkit.getWorld("world"), 0.5, 97, 21.5),
                Villager.Type.SNOW, Villager.Profession.CARTOGRAPHER);
        populate();
    }

    @Override
    protected void populate() {
        setBlackPanes();
    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {
        setBlackPanes();

        Player player = (Player) event.getPlayer();

        List<ItemStack> itemsToReturn = new ArrayList<>();
        for (int slot : TRADE_SLOTS) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack != null) {
                itemsToReturn.add(itemStack);
                getInventory().clear(slot);
            }
        }

        if (!itemsToReturn.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> player.give(itemsToReturn), 1L);
        }
    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event) {
        Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
            double totalSellValue = calculateTotalSellValue();
            if (totalSellValue > 0.00) {
                setYellowPanes(totalSellValue);
            } else {
                setBlackPanes();
            }
        }, 1L);
    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {
        for (int slot : BOTTOM_SLOTS) {
            if (slot == event.getSlot()) return;
        }

        double totalSellValue = calculateTotalSellValue();
        if (totalSellValue > 0.00) {
            setYellowPanes(totalSellValue);
        } else {
            setBlackPanes();
        }
    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {
        double totalSellValue = calculateTotalSellValue();
        if (totalSellValue > 0.00) {
            setYellowPanes(totalSellValue);
        } else {
            setBlackPanes();
        }
    }

    private void setBlackPanes() {
        for (int slot : BOTTOM_SLOTS) {
            setButton(new Button(slot, new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE)
                    .name(text("Add items to sell them", GRAY))
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    // Do nothing
                }
            });
        }
    }

    private void setYellowPanes(double totalSellValue) {
        for (int slot : BOTTOM_SLOTS) {
            setButton(new Button(slot, new ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                    .name(text("Total sell value: " + totalSellValue, YELLOW))
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    setLimePanes(totalSellValue);
                }
            });
        }
    }

    private void setLimePanes(double totalSellValue) {
        for (int slot : BOTTOM_SLOTS) {
            setButton(new Button(slot, new ItemStackBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .name(text("Click again to confirm sale", GREEN))
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    for (int slot : TRADE_SLOTS) {
                        ItemStack itemStack = getInventory().getItem(slot);
                        if (itemStack == null) continue;

                        if (SELLABLE_ITEMS.containsKey(itemStack.getType())) {
                            getInventory().clear(slot);
                        }
                    }

                    Player player = (Player) event.getWhoClicked();
                    player.sendMessage(text("You sold items worth " + totalSellValue + "!", GREEN));
                    PlayerProfileManager.getPlayerProfile(player).addBalance(totalSellValue);
                    setBlackPanes();
                }
            });
        }
    }

    private double calculateTotalSellValue() {
        double totalSellValue = 0.00;
        for (int slot : TRADE_SLOTS) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack == null) continue;

            if (SELLABLE_ITEMS.containsKey(itemStack.getType())) {
                totalSellValue += SELLABLE_ITEMS.get(itemStack.getType()) * itemStack.getAmount();
            }
        }
        return totalSellValue;
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }
}