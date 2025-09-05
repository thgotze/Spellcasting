package com.gotze.spellcasting.gui;

import com.gotze.blockbreaksounds.gui.blockbreaksounds.BlockBreakSoundsGUI;
import com.gotze.blockbreaksounds.model.CurrentSoundData;
import com.gotze.spellcasting.data.PlayerPickaxeService;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import static com.gotze.spellcasting.util.StringUtils.convertToSmallFont;

public class PickaxeGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Pickaxe"));
        GUIUtils.setFrames(gui);
        gui.setItem(2, STATISTICS_BUTTON); // TODO fix implementation of statistics, currently is just a static view
        gui.setItem(4, PlayerPickaxeService.getPlayerPickaxeCloneWithoutDurability(player)); // TODO find better naming?
        gui.setItem(6, CurrentSoundData.createCurrentSoundButton(player)); // TODO fix implementation of current sound
        gui.setItem(21, ENCHANTMENTS_BUTTON);
        gui.setItem(22, MATERIAL_BUTTON);
        gui.setItem(23, SPELLS_BUTTON);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PickaxeGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;

        int slot = event.getSlot();

        switch (slot) {
            case 21 -> {
                new EnchantmentsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 22 -> {
                new MaterialsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 23 -> {
                new SpellsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 6 -> { // TODO fix implementation of current sound
                CurrentSoundData.currentSoundButtonHandler(clickedInventory, event.getClick(), player, slot);
                if (clickedInventory.getItem(44).getType().equals(Material.GLASS_PANE)) {
                    new BlockBreakSoundsGUI(player);
                    SoundUtils.playUIClickSound(player);
                }
            }
        }
    }

    private final ItemStack STATISTICS_BUTTON = new ItemStackBuilder(Material.BOOK)
            .displayName(Component.text("Statistics")
                    .color(NamedTextColor.GOLD))
            .lore(Component.text(convertToSmallFont("Blocks broken: 175"))
                            .color(NamedTextColor.GRAY),
                    Component.text(convertToSmallFont("Material Tier: Diamond"))
                            .color(NamedTextColor.GRAY),
                    Component.text(convertToSmallFont("Enchants unlocked: 5/8"))
                            .color(NamedTextColor.GRAY),
                    Component.text(convertToSmallFont("Spells unlocked: 2/6"))
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack MATERIAL_BUTTON = new ItemStackBuilder(Material.ANVIL)
            .displayName(Component.text("Materials")
                    .color(NamedTextColor.AQUA))
            .lore(Component.text(convertToSmallFont("Click to view pickaxe materials"))
                    .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack ENCHANTMENTS_BUTTON = new ItemStackBuilder(Material.ENCHANTED_BOOK)
            .displayName(Component.text("Enchantments")
                    .color(NamedTextColor.YELLOW))
            .lore(Component.text(convertToSmallFont("Click to view pickaxe enchantments"))
                    .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack SPELLS_BUTTON = new ItemStackBuilder(Material.END_CRYSTAL)
            .displayName(Component.text("Abilities")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(convertToSmallFont("Click to view pickaxe abilities"))
                    .color(NamedTextColor.GRAY))
            .build();

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}