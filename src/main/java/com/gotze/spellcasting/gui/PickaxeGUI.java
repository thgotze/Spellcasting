package com.gotze.spellcasting.gui;

import com.google.common.collect.HashMultimap;
import com.gotze.spellcasting.PlayerPickaxeManager;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackCreator;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PickaxeGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Pickaxe"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeManager.getPlayerPickaxe(player));
        gui.setItem(21, MATERIALS_BUTTON);
        gui.setItem(22, ENCHANTMENTS_BUTTON);
        gui.setItem(23, SPELLS_BUTTON);

        ItemStack target = ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE, 99);
        ItemStack tester = ItemStack.of(Material.COBBLESTONE);
        ItemMeta testerMeta = tester.getItemMeta();
        FoodComponent foodComponent = testerMeta.getFood();
        UseCooldownComponent useCooldown = testerMeta.getUseCooldown();
        foodComponent.setCanAlwaysEat(false);

        target.editMeta(itemMeta -> {
            itemMeta.setAttributeModifiers(HashMultimap.create());
//            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            itemMeta.setFood(foodComponent);
            itemMeta.setRarity(ItemRarity.COMMON);
            itemMeta.setUseCooldown(useCooldown);
        });
        target.unsetData(DataComponentTypes.CONSUMABLE);
        target.unsetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        target.setData(DataComponentTypes.MAX_STACK_SIZE, 99);
        target.setData(DataComponentTypes.ITEM_MODEL, tester.getData(DataComponentTypes.ITEM_MODEL));
//        public <T> @Nullable T getData(final io.papermc.paper.datacomponent.DataComponentType.@NotNull Valued<T> type)
//        public <T> void setData(final io.papermc.paper.datacomponent.DataComponentType.@NotNull Valued<T> type, final @NotNull T value) {

//        player.give(target);
        gui.setItem(9, target);



        ItemStack ironpick = ItemStack.of(Material.IRON_PICKAXE);
//        ItemMeta ironPickMeta = ironpick.getItemMeta();
//        ironPickMeta.setAttributeModifiers(HashMultimap.create());
//        ironpick.setItemMeta(ironPickMeta);
        ironpick.setData(DataComponentTypes.TOOL, Tool.tool().defaultMiningSpeed(10.0f));
        ironpick.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        ironpick.unsetData(DataComponentTypes.ENCHANTMENTS);
        ironpick.unsetData(DataComponentTypes.LORE);
        ironpick.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        ironpick.setData(DataComponentTypes.MAX_DAMAGE, 100);
        ironpick.unsetData(DataComponentTypes.ENCHANTABLE);
        ironpick.unsetData(DataComponentTypes.RARITY);
//        ironpick.unsetData(DataComponentTypes.ITEM_NAME);
        ironpick.unsetData(DataComponentTypes.REPAIR_COST);
//        ironpick.unsetData(DataComponentTypes.ITEM_MODEL);
        ironpick.setData(DataComponentTypes.MAX_STACK_SIZE, 67);
        ironpick.unsetData(DataComponentTypes.REPAIRABLE);
        ironpick.unsetData(DataComponentTypes.DAMAGE);
        gui.setItem(10, ironpick);
        player.give(ironpick);

//        Set<DataComponentType> dataComponentTypes = ironpick.getDataTypes();
//        for (DataComponentType dct : dataComponentTypes) {
//            player.sendMessage(dct.key().toString());
//        }

//        if (ironpick.hasData(DataComponentTypes.TOOL)) {
//            player.sendMessage("has tool");
//            player.sendMessage(ironpick.getData(DataComponentTypes.TOOL).toString());
//        }
//        player.sendMessage(ironpick.getData(DataComponentTypes.ENCHANTMENTS).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.LORE).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.MAX_DAMAGE).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.ENCHANTABLE).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.RARITY).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.ITEM_NAME).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.REPAIR_COST).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.ITEM_MODEL).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.MAX_STACK_SIZE).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.REPAIRABLE).toString());
//        player.sendMessage(ironpick.getData(DataComponentTypes.DAMAGE).toString());

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PickaxeGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        if (clickedInventory.equals(player.getInventory())) return;

        int slot = event.getSlot();

        switch (slot) {
            case 21 -> {
                new MaterialsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 22 -> {
                new EnchantmentsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 23 -> {
                new SpellsGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
        }
    }

    private final ItemStack MATERIALS_BUTTON = ItemStackCreator.createItemStack(
            Material.ANVIL,
            Component.text("Materials")
                    .color(NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("5 material tiers available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack ENCHANTMENTS_BUTTON = ItemStackCreator.createItemStack(
            Material.ENCHANTED_BOOK,
            Component.text("Enchantments")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 enchantments available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    private final ItemStack SPELLS_BUTTON = ItemStackCreator.createItemStack(
            Material.END_CRYSTAL,
            Component.text("Spells")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false),
            Arrays.asList(
                    Component.text(StringUtils.convertToSmallFont("3 spells available"))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            )
    );

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}