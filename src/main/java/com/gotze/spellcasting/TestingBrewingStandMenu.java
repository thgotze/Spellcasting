package com.gotze.spellcasting;

import com.gotze.spellcasting.util.menu.Menu;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class TestingBrewingStandMenu extends Menu {

    public TestingBrewingStandMenu(Player player) {
        super(InventoryType.CHEST, text("\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uE337").color(WHITE)
                .append(text("\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001" +
                        "\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001\uF001Crusher").color(TextColor.color(64, 64, 64))), true);
        populate(player);
        open(player);
    }

    private void populate(Player player) {
//        ItemStack itemStack = ItemStack.of(Material.PAPER);
//        itemStack.editMeta(itemMeta -> itemMeta.setItemModel(NamespacedKey.minecraft("shredder_blade_left")));
//        itemStack.editMeta(itemMeta -> itemMeta.setItemModel(NamespacedKey.minecraft("blank")));
//        item(0, itemStack);
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