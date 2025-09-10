package com.gotze.spellcasting.gui;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class AbilityGUI implements InventoryHolder, Listener {
    private Inventory gui;

    public void openGUI(Player player) {
        gui = Bukkit.createInventory(this, 45, Component.text("Spells"));
        GUIUtils.setFrames(gui);
        gui.setItem(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(player));
        gui.setItem(21, SLICE_ABILITY_BUTTON);
        gui.setItem(22, LASER_ABILITY_BUTTON);
        gui.setItem(23, BAZOOKA_ABILITY_BUTTON);
        gui.setItem(36, GUIUtils.RETURN_BUTTON);
        gui.setItem(43, DEBUG_CLEAR_ABILITIES_BUTTON); // TODO: debug
        gui.setItem(44, DEBUG_GIVE_RESOURCES_BUTTON); // TODO: debug
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbilityGUI)) return;

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        if (clickedInventory.equals(playerInventory)) return;

        int slot = event.getSlot();

        switch (slot) {
            case 21, 22, 23 ->
                    upgradeAbility(player, playerInventory, clickedInventory.getItem(slot).getType(), clickedInventory);
            case 36 -> {
                new PickaxeGUI().openGUI(player);
                SoundUtils.playUIClickSound(player);
            }
            case 43 -> clearPickaxeAbilities(player, playerInventory, clickedInventory); // TODO: debug
            case 44 -> giveResources(player, playerInventory); // TODO: debug
        }
    }

    private void upgradeAbility(Player player, PlayerInventory playerInventory, Material clickedUpgrade, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        final int REQUIRED_AMOUNT = 32;
        final ItemStack REQUIRED_MATERIALS = ItemStack.of(Material.AMETHYST_SHARD, REQUIRED_AMOUNT);

        if (!playerInventory.containsAtLeast(REQUIRED_MATERIALS, REQUIRED_AMOUNT)) {
            player.sendMessage(Component.text("You don't have the required materials (" + REQUIRED_AMOUNT + "x " + REQUIRED_MATERIALS.getType().toString().toLowerCase() + ") " + "to upgrade this ability!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        Ability.AbilityType abilityType = switch (clickedUpgrade) {
            case IRON_SWORD -> Ability.AbilityType.SLICE;
            case LIGHTNING_ROD -> Ability.AbilityType.LASER;
            case FIREWORK_ROCKET -> Ability.AbilityType.BAZOOKA;
            default -> null;
        };

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        if (pickaxeData.hasAbility(abilityType) && pickaxeData.getAbility(abilityType).isMaxLevel()) {
            player.sendMessage(Component.text("Cannot upgrade " + abilityType.getName() + " past level " + abilityType.getMaxLevel() + "!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the player
        // 1. has their pickaxe in their main hand
        // 2. they have enough of the required material
        // 3. their pickaxe is not at the max level of the chosen ability

        ItemStack heldItem = playerInventory.getItemInMainHand();
        playerInventory.removeItem(heldItem);
        playerInventory.removeItem(REQUIRED_MATERIALS);

        PlayerPickaxeService.upgradePickaxeAbility(player, abilityType);
        ItemStack playerPickaxe = PlayerPickaxeService.getPickaxe(pickaxeData);
        playerInventory.addItem(playerPickaxe);

        clickedInventory.setItem(4, GUIUtils.cloneItemWithoutDamage(playerPickaxe));
        SoundUtils.playSuccessSound(player);
    }

    private void clearPickaxeAbilities(Player player, PlayerInventory playerInventory, Inventory clickedInventory) {
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
            return;
        }
        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        pickaxeData.removeAbilities();

        playerInventory.remove(playerInventory.getItemInMainHand());

        ItemStack pickaxe = PlayerPickaxeService.getPickaxe(pickaxeData);
        playerInventory.addItem(pickaxe);

        clickedInventory.setItem(4, PlayerPickaxeService.getPickaxeCloneWithoutDurability(pickaxe));
        SoundUtils.playUIClickSound(player);
    }

    private void giveResources(Player player, PlayerInventory playerInventory) {
        playerInventory.addItem(ItemStack.of(Material.AMETHYST_SHARD, 32));
        SoundUtils.playUIClickSound(player);
    }

    private final ItemStack SLICE_ABILITY_BUTTON = new ItemStackBuilder(Material.IRON_SWORD)
            .displayName(Component.text("Slice")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .hideAttributes()
            .build();

    private final ItemStack LASER_ABILITY_BUTTON = new ItemStackBuilder(Material.LIGHTNING_ROD)
            .displayName(Component.text("Laser")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .build();

    private final ItemStack BAZOOKA_ABILITY_BUTTON = new ItemStackBuilder(Material.FIREWORK_ROCKET)
            .displayName(Component.text("Bazooka")
                    .color(NamedTextColor.LIGHT_PURPLE))
            .lore(Component.text(""),
                    Component.text(StringUtils.convertToSmallFont("requirements")),
                    Component.text("32x Amethyst Shard")
                            .color(NamedTextColor.GRAY))
            .hideAdditionalTooltip()
            .hideAttributes()
            .build();

    private final ItemStack DEBUG_CLEAR_ABILITIES_BUTTON = new ItemStackBuilder(Material.BARRIER) // TODO: debug
            .displayName(Component.text("DEBUG: CLEAR ABILITIES").decorate(TextDecoration.BOLD)
                    .color(NamedTextColor.RED))
            .build();

    private final ItemStack DEBUG_GIVE_RESOURCES_BUTTON = new ItemStackBuilder(Material.CHEST) // TODO: debug
            .displayName(Component.text("DEBUG: GIVE RESOURCES")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD))
            .build();

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}