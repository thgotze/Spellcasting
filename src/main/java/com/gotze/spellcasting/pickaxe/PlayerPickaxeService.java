package com.gotze.spellcasting.pickaxe;

import com.destroystokyo.paper.MaterialSetTag;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerPickaxeService {
    private static final Map<Player, PickaxeData> PLAYER_PICKAXE_DATA_MAP = new HashMap<>();

    public static void createPickaxeData(Player player) {
        PLAYER_PICKAXE_DATA_MAP.put(player, new PickaxeData());
    }

    public static PickaxeData getPickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.get(player);
    }

    public static ItemStack getPickaxe(Player player) {
        return getPickaxe(getPickaxeData(player));
    }

    public static ItemStack getPickaxe(PickaxeData pickaxeData) {
        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getType())
                .lore(getPickaxeLore(pickaxeData))
                .hideAttributes()
                .hideEnchantTooltip();

        Enchantment efficiency = pickaxeData.getEnchantment(Enchantment.EnchantmentType.EFFICIENCY);
        if (efficiency != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.EFFICIENCY, efficiency.getLevel());
        }

        Enchantment unbreaking = pickaxeData.getEnchantment(Enchantment.EnchantmentType.UNBREAKING);
        if (unbreaking != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, unbreaking.getLevel());
        }

        Enchantment fortune = pickaxeData.getEnchantment(Enchantment.EnchantmentType.FORTUNE);
        if (fortune != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.FORTUNE, fortune.getLevel());
        }

        return builder.build();
    }

    public static List<Component> getPickaxeLore(PickaxeData pickaxeData) {
//        Material material = pickaxeData.getType();
        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
        Set<Ability> abilities = pickaxeData.getAbilities();
        int blocksBroken = pickaxeData.getBlocksBroken();
        int damage = pickaxeData.getDamage();

        List<Component> lore = new ArrayList<>();

        // Sort enchantments by rarity (descending order)
        List<Enchantment> sortedEnchantments = enchantments.stream()
                .sorted((e1, e2) -> Integer.compare(
                        e2.getEnchantmentType().getRarity().ordinal(),
                        e1.getEnchantmentType().getRarity().ordinal()
                ))
                .toList();


//        for (Enchantment enchantment : sortedEnchantments) { TODO: decided if this or one below looks better
//            lore.add(Component.text("⛏ ")
//                    .color(NamedTextColor.YELLOW)
//                    .append(Component.text(enchantment.getEnchantmentType().getName() + " ")
//                            .color(enchantment.getEnchantmentType().getRarity().getColor()))
//                    .append(Component.text(StringUtils.toRomanNumeral(enchantment.getLevel()))
//                            .color(TextColor.color(250, 250, 250))
//                            .decorate(TextDecoration.BOLD)));
//        }

        for (Enchantment enchantment : sortedEnchantments) {
            lore.add(Component.text(enchantment.getEnchantmentType().getName() + " ")
                            .color(enchantment.getEnchantmentType().getRarity().getColor())
                    .append(Component.text(StringUtils.toRomanNumeral(enchantment.getLevel()))
                            .color(TextColor.color(250, 250, 250))
                            .decorate(TextDecoration.BOLD)));
        }

        lore.add(Component.text(""));

        for (Ability ability : abilities) {
            lore.add(Component.text("⚡ ")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
                    .append(Component.text(ability.getAbilityType().getName() + " ")
                            .color(ability.getAbilityType().getRarity().getColor())
                            .decoration(TextDecoration.BOLD, false))
                    .append(Component.text(StringUtils.toRomanNumeral(ability.getLevel()))
                            .color(TextColor.color(250, 250, 250))
                            .decorate(TextDecoration.BOLD)));
        }
        lore.add(Component.text(""));
        lore.add(Component.text("Blocks Broken: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(blocksBroken))
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)));

        lore.add(Component.text("Durability: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(damage))
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)));

        List<Component> fixedLore = lore.stream()
                .map(component -> component
                        .colorIfAbsent(NamedTextColor.WHITE)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                ).toList();

        return fixedLore;
    }

    public static void upgradePickaxeEnchantment(Player player, Enchantment.EnchantmentType enchantmentType) {
        upgradePickaxeEnchantment(getPickaxeData(player), enchantmentType);
    }

    public static void upgradePickaxeEnchantment(PickaxeData pickaxeData, Enchantment.EnchantmentType enchantmentType) {
        if (pickaxeData.hasEnchantment(enchantmentType)) {
            pickaxeData.getEnchantment(enchantmentType).increaseLevel();
        } else {
            pickaxeData.addEnchantment(new Enchantment(enchantmentType));
        }
    }

    public static void upgradePickaxeAbility(Player player, Ability.AbilityType abilityType) {
        upgradePickaxeAbility(getPickaxeData(player), abilityType);
    }

    public static void upgradePickaxeAbility(PickaxeData pickaxeData, Ability.AbilityType abilityType) {
        if (pickaxeData.hasAbility(abilityType)) {
            pickaxeData.getAbility(abilityType).increaseLevel();
        } else {
            pickaxeData.addAbility(new Ability(abilityType));
        }
    }

    public static boolean isPlayerHoldingOwnPickaxe(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!MaterialSetTag.ITEMS_PICKAXES.isTagged(heldItem.getType())) return false;

        return heldItem.matchesWithoutData(getPickaxe(getPickaxeData(player)), Set.of(DataComponentTypes.DAMAGE));
    }

    public static ItemStack getPickaxeCloneWithoutDurability(Player player) {
        return getPickaxeCloneWithoutDurability(getPickaxeData(player));
    }

    public static ItemStack getPickaxeCloneWithoutDurability(PickaxeData pickaxeData) {
        return getPickaxeCloneWithoutDurability(getPickaxe(pickaxeData));
    }

    public static ItemStack getPickaxeCloneWithoutDurability(ItemStack pickaxe) {
        return GUIUtils.cloneItemWithoutDamage(pickaxe);
    }
}