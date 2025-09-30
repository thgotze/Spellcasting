package com.gotze.spellcasting.menu;

//import com.gotze.blockbreaksounds.gui.blockbreaksounds.BlockBreakSoundsGUI;
//import com.gotze.blockbreaksounds.model.CurrentSoundData;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.Menu;
import com.gotze.spellcasting.util.menu.MenuUtils;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class PickaxeMenu extends Menu {

    public PickaxeMenu(Player player) {
        super(5, Component.text("Pickaxe"));
        populate(player);
        open(player);
    }

    private void populate(Player player) {
        MenuUtils.setFrames(getInventory());
//        item(2, new ItemStackBuilder(Material.BOOK) // TODO fix implementation of statistics, currently is just a static view
//                .name(Component.text("Statistics")
//                        .color(NamedTextColor.GOLD))
//                .lore(Component.text(StringUtils.convertToSmallFont("Material Tier: Diamond"))
//                                .color(NamedTextColor.GRAY),
//                        Component.text(StringUtils.convertToSmallFont("Enchants unlocked: 5/8"))
//                                .color(NamedTextColor.GRAY),
//                        Component.text(StringUtils.convertToSmallFont("Spells unlocked: 2/6"))
//                                .color(NamedTextColor.GRAY),
//                        Component.text(StringUtils.convertToSmallFont("Blocks broken: 175"))
//                                .color(NamedTextColor.GRAY),
//                        Component.text(StringUtils.convertToSmallFont("Durability: 1250"))
//                                .color(NamedTextColor.GRAY))
//                .build());

        item(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));

        button(new Button(21, new ItemStackBuilder(Material.ENCHANTED_BOOK)
                .name(Component.text("Enchantments")
                        .color(NamedTextColor.YELLOW))
                .lore(Component.text(StringUtils.convertToSmallFont("Click to view pickaxe enchantments"))
                        .color(NamedTextColor.GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new EnchantmentMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(22, new ItemStackBuilder(Material.ANVIL)
                .name(Component.text("Materials")
                        .color(NamedTextColor.AQUA))
                .lore(Component.text(StringUtils.convertToSmallFont("Click to view pickaxe materials"))
                        .color(NamedTextColor.GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new MaterialsMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

        button(new Button(23, new ItemStackBuilder(Material.END_CRYSTAL)
                .name(Component.text("Abilities").color(NamedTextColor.RED))
                .lore(Component.text(StringUtils.convertToSmallFont("Click to view pickaxe abilities"))
                        .color(NamedTextColor.GRAY))
                .build()) {
            @Override
            public void onClick(InventoryClickEvent event) {
                new AbilityMenu(player);
                SoundUtils.playUIClickSound(player);
            }
        });

//        button(new Button(6, CurrentSoundData.createCurrentSoundButton(player)) { // TODO: fix poor implementation
//            @Override
//            public void onClick(InventoryClickEvent event) {
//                CurrentSoundData.currentSoundButtonHandler(event.getClickedInventory(), event.getClick(), player, 6);
//                if (event.getClickedInventory().getItem(44).getType().equals(Material.GLASS_PANE)) {
//                    new BlockBreakSoundsGUI(player);
//                    SoundUtils.playUIClickSound(player);
//                }
//            }
//        });
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