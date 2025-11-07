package com.gotze.spellcasting.merchants;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class OreMerchantMenu extends Menu {

    private static final int[] tradeSlots = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    private static final int[] acceptTradeSlots = {
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

    private static final ItemStack LIME_PANE = ItemStack.of(Material.LIME_STAINED_GLASS_PANE);
    private static final ItemStack YELLOW_PANE = ItemStack.of(Material.YELLOW_STAINED_GLASS_PANE);
    private static final ItemStack BLACK_PANE = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE)
            .name(text("Add items to sell them").color(GRAY))
            .build();

    public OreMerchantMenu(Player player) {
        super(5, text("Ore Merchant"), true);
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        for (int acceptTradeSlot : acceptTradeSlots) {
            setButton(new Button(acceptTradeSlot,BLACK_PANE) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {

                }
            });
        }
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        List<ItemStack> itemsToReturn = new ArrayList<>();

        for (int slot : tradeSlots) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack != null) {
                itemsToReturn.add(itemStack);
            }
        }

        if (!itemsToReturn.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> player.give(itemsToReturn), 1L);
        }
    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {
        double totalSellValue = calculateTotalSellValue();

        for (int slot : acceptTradeSlots) {
            if (event.getSlot() == slot) {
                Material glassType = getInventory().getItem(slot).getType();
                if (glassType == Material.YELLOW_STAINED_GLASS_PANE) {
                    for (int slot2 : acceptTradeSlots) {
                        setButton(new Button(slot2,new ItemStackBuilder(LIME_PANE)
                                .name(text("Click again to accept").color(GREEN))
                                .build()) {
                            @Override
                            public void onButtonClick(InventoryClickEvent event) {

                            }
                        });
                    }

                } else if (glassType == Material.LIME_STAINED_GLASS_PANE) {
                    for (int tradeSlot : tradeSlots) {
                        ItemStack itemStack = getInventory().getItem(tradeSlot);
                        if (itemStack == null) continue;

                        if (SELLABLE_ITEMS.containsKey(itemStack.getType())) {
                            getInventory().clear(tradeSlot);
                        }
                    }

                    for (int acceptTradeSlot : acceptTradeSlots) {
                        setButton(new Button(acceptTradeSlot, BLACK_PANE) {
                            @Override
                            public void onButtonClick(InventoryClickEvent event) {

                            }
                        });
                    }
                    Player player = (Player) event.getWhoClicked();
                    PlayerProfileManager.getPlayerProfile(player).addBalance(totalSellValue);
                    player.sendMessage("You sold ores and received " + totalSellValue + " coins!");
                }
                return;
            }
        }

        if (totalSellValue > 0.00) {
            for (int slot : acceptTradeSlots) {
                setButton(new Button(slot, new ItemStackBuilder(YELLOW_PANE)
                        .name(text("Total sell value: " + totalSellValue).color(YELLOW))
                        .build()) {
                    @Override
                    public void onButtonClick(InventoryClickEvent event) {

                    }
                });
            }

        } else {
            for (int slot : acceptTradeSlots) {
                setButton(new Button(slot, BLACK_PANE) {
                    @Override
                    public void onButtonClick(InventoryClickEvent event) {

                    }
                });
            }
        }
    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {
        double totalSellValue = calculateTotalSellValue();

        if (totalSellValue > 0.00) {
            for (int acceptTradeSlot : acceptTradeSlots) {
                setButton(new Button(acceptTradeSlot, new ItemStackBuilder(YELLOW_PANE)
                        .name(text("Total sell value: " + totalSellValue).color(YELLOW))
                        .build()) {
                    @Override
                    public void onButtonClick(InventoryClickEvent event) {

                    }
                });
            }
        }
    }

    private double calculateTotalSellValue() {
        double totalSellValue = 0.00;
        for (int slot : tradeSlots) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack == null) continue;

            if (SELLABLE_ITEMS.containsKey(itemStack.getType())) {
                totalSellValue += SELLABLE_ITEMS.get(itemStack.getType()) * itemStack.getAmount();
            }
        }
        return totalSellValue;
    }
}