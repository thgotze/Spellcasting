package com.gotze.spellcasting.pickaxe.menu;

import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class PickaxeMenu extends Menu {

    public PickaxeMenu(Player player) {
        super(5, text("Pickaxe"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());

        item(4, PlayerPickaxeService.pickaxeCloneWithoutDurability(player));

        button(new Button(21, new ItemStackBuilder(Material.ENCHANTED_BOOK)
                .name(text("Enchantments", LIGHT_PURPLE))
                .lore(text(StringUtils.convertToSmallFont("View pickaxe enchantments"), GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new EnchantmentMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(22, new ItemStackBuilder(Material.ANVIL)
                .name(text("Materials", AQUA))
                .lore(text(StringUtils.convertToSmallFont("View pickaxe materials"), GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new MaterialMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(23, new ItemStackBuilder(Material.END_CRYSTAL)
                .name(text("Abilities",RED))
                .lore(text(StringUtils.convertToSmallFont("View pickaxe abilities"), GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new AbilityMenu(player);
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