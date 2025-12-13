package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.util.SoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gotze.spellcasting.Spellcasting.plugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class AbilityManager implements Listener {

    public static final String NEG_SPACE = "\uF001";

    private static final Component progressBar0 = text("||||||||||", DARK_GRAY, BOLD);
    private static final Component progressBar10 = textOfChildren(text("|", GREEN, BOLD), text("|||||||||", DARK_GRAY, BOLD));
    private static final Component progressBar20 = textOfChildren(text("||", GREEN, BOLD), text("||||||||", DARK_GRAY, BOLD));
    private static final Component progressBar30 = textOfChildren(text("|||", GREEN, BOLD), text("|||||||", DARK_GRAY, BOLD));
    private static final Component progressBar40 = textOfChildren(text("||||", YELLOW, BOLD), text("||||||", DARK_GRAY, BOLD));
    private static final Component progressBar50 = textOfChildren(text("|||||", YELLOW, BOLD), text("|||||", DARK_GRAY, BOLD));
    private static final Component progressBar60 = textOfChildren(text("||||||", YELLOW, BOLD), text("||||", DARK_GRAY, BOLD));
    private static final Component progressBar70 = textOfChildren(text("|||||||", GOLD, BOLD), text("|||", DARK_GRAY, BOLD));
    private static final Component progressBar80 = textOfChildren(text("||||||||", GOLD, BOLD), text("||", DARK_GRAY, BOLD));
    private static final Component progressBar90 = textOfChildren(text("|||||||||", GOLD, BOLD), text("|", DARK_GRAY, BOLD));
    private static final Component progressBar100 = text("||||||||||", RED, BOLD);
    private static final Component spaceBetweenProgressBars = text(" ".repeat(5));
    private static final Component startingSpace = (text(" ".repeat(3)).append(text(NEG_SPACE.repeat(2))));

    private static final Map<Player, Integer> selectedAbilityIndex = new HashMap<>();
    private static final Map<Player, Component> playerActionBar = new HashMap<>();

    public AbilityManager() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : playerActionBar.keySet()) {
                player.sendActionBar(playerActionBar.get(player));
            }
        }, 40, 40);
    }

    private Component getProgressBarFor(Ability ability) {
        int energy = ability.getEnergy();
        int requiredEnergy = ability.getAbilityType().getRequiredEnergy();

        // 0..10 filled bars (floor), then map to 0..100 in steps of 10
        int filledBars = (int) Math.floor((energy * 10.0) / requiredEnergy);
        if (filledBars < 0) filledBars = 0;
        if (filledBars > 10) filledBars = 10;

        return switch (filledBars) {
            case 0 -> progressBar0;
            case 1 -> progressBar10;
            case 2 -> progressBar20;
            case 3 -> progressBar30;
            case 4 -> progressBar40;
            case 5 -> progressBar50;
            case 6 -> progressBar60;
            case 7 -> progressBar70;
            case 8 -> progressBar80;
            case 9 -> progressBar90;
            case 10 -> progressBar100;
            default -> progressBar0;
        };
    }

    private Component getSpriteOfSelectedAbilityType(Ability.AbilityType abilityType) {
        return switch (abilityType) {
            case BAZOOKA -> text("\uF004");
            case DRILL_DASH -> text("\uF006");
            case HAMMER -> text("\uF008");
            case TRIDENT_THROW -> text("\uF010");
            case WIND_BURST -> text("\uF012");
        };
    }

    private Component getSpriteOfUnselectedAbilityType(Ability.AbilityType abilityType) {
        return switch (abilityType) {
            case BAZOOKA -> text("\uF005");
            case DRILL_DASH -> text("\uF007");
            case HAMMER -> text("\uF009");
            case TRIDENT_THROW -> text("\uF011");
            case WIND_BURST -> text("\uF013");
        };
    }

    @EventHandler
    public void onHandSwapCycleAbility(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        int current = selectedAbilityIndex.getOrDefault(player, 0);
        int delta = player.isSneaking() ? -1 : 1; // sneak = left, not sneaking = right
        int next = Math.floorMod(current + delta, abilities.size());

        selectedAbilityIndex.put(player, next);

        Ability selectedAbility = abilities.get(next);

        List<Component> actionBarComponents = new ArrayList<>();
        actionBarComponents.add(startingSpace);

        for (Ability ability : abilities) {
            actionBarComponents.add(getProgressBarFor(ability));

            actionBarComponents.add(text(NEG_SPACE.repeat(39)));

            if (ability == selectedAbility) {
                actionBarComponents.add(getSpriteOfSelectedAbilityType(ability.getAbilityType()));
            } else {
                actionBarComponents.add(getSpriteOfUnselectedAbilityType(ability.getAbilityType()));
            }

            if (ability != abilities.getLast()) {
                actionBarComponents.add(spaceBetweenProgressBars);
            }
        }

        TextComponent actionBar = textOfChildren(actionBarComponents.toArray(Component[]::new));
        playerActionBar.put(player, actionBar);
        player.sendActionBar(actionBar);

        player.playSound(player, Sound.UI_BUTTON_CLICK, 0.05f, 2f);
    }

    @EventHandler
    public void onShiftRightClickActivateAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().isRightClick()) return;
        if (!player.isSneaking()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        if (pickaxeData.getDurabilityDamage() + 20 >= pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            player.sendMessage(text("Pickaxe durability too low to activate ability", RED));
            SoundUtils.playBassNoteBlockErrorSound(player);
            SoundUtils.playVillagerErrorSound(player);
            return;
        }

        int index = selectedAbilityIndex.getOrDefault(player, 0);

        Ability selectedAbility = abilities.get(index);
        if (selectedAbility != null) {
            selectedAbility.activateAbility(player, pickaxeData);
        }
    }
}
