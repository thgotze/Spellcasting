package com.gotze.spellcasting.feature.pickaxe;

import com.destroystokyo.paper.MaterialSetTag;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.common.PickaxeMaterial;
import com.gotze.spellcasting.feature.pickaxe.ability.Ability;
import com.gotze.spellcasting.feature.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.GUIUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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
        PickaxeData pickaxeData = getPickaxeData(player);

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getPickaxeMaterial().getType())
                .lore(getPickaxeLore(pickaxeData))
                .hideAttributes()
                .hideEnchantTooltip()
                .addPersistentDataContainer("owner", player.getUniqueId().toString())
                .setDurabilityDamage(pickaxeData.getDamage())
                .setMaxDurability(pickaxeData.getPickaxeMaterial().getMaxDurability());

        Enchantment efficiency = pickaxeData.getEnchantment(Enchantment.EnchantmentType.EFFICIENCY);
        Enchantment fortune = pickaxeData.getEnchantment(Enchantment.EnchantmentType.FORTUNE);
        Enchantment unbreaking = pickaxeData.getEnchantment(Enchantment.EnchantmentType.UNBREAKING);

        if (efficiency != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.EFFICIENCY, efficiency.getLevel());
        }
        if (fortune != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.FORTUNE, fortune.getLevel());
        }
        if (unbreaking != null) {
            builder.addEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, unbreaking.getLevel());
        }

        return builder.build();
    }

    public static List<Component> getPickaxeLore(PickaxeData pickaxeData) {
        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();
        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
        Set<Ability> abilities = pickaxeData.getAbilities();
        int blocksBroken = pickaxeData.getBlocksBroken();
        int damage = pickaxeData.getDamage();

        List<Component> lore = new ArrayList<>();

        if (!enchantments.isEmpty()) {
            // Sort enchantments by rarity in descending order (Common -> Legendary)
            List<Enchantment> sortedEnchantments = enchantments.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getEnchantmentType().getRarity().ordinal(),
                            e1.getEnchantmentType().getRarity().ordinal()
                    ))
                    .toList();

            for (Enchantment enchantment : sortedEnchantments) {
                lore.add(Component.text(enchantment.getEnchantmentType() + " ")
                        .color(enchantment.getEnchantmentType().getRarity().getColor())
                        .append(Component.text(StringUtils.toRomanNumeral(enchantment.getLevel())))
                );
            }
            lore.add(Component.text(""));
        }

        if (!abilities.isEmpty()) {
            // Sort abilities by rarity in descending order (Common -> Legendary)
            List<Ability> sortedAbilities = abilities.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getRarity().ordinal(),
                            e1.getRarity().ordinal()
                    ))
                    .toList();

            for (Ability ability : sortedAbilities) {
                lore.add(Component.text("⚡ ")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .append(Component.text(ability.getAbilityType() + " ")
                                .color(ability.getRarity().getColor())
                                .decoration(TextDecoration.BOLD, false))
                        .append(Component.text(StringUtils.toRomanNumeral(ability.getLevel()))
                                .color(ability.getRarity().getColor())
                                .decoration(TextDecoration.BOLD, false)));
            }
            lore.add(Component.text(""));
        }

        lore.add(Component.text("Blocks Broken: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(blocksBroken))
                        .color(NamedTextColor.GOLD)));

        lore.add(Component.text(""));

        int remaining = pickaxeMaterial.getMaxDurability() - damage;
        double pct = (double) remaining / pickaxeMaterial.getMaxDurability();
        NamedTextColor color;
        if (pct >= 0.60) {
            color = NamedTextColor.GREEN;
        } else if (pct >= 0.30) {
            color = NamedTextColor.YELLOW;
        } else if (pct >= 0.20) {
            color = NamedTextColor.GOLD;
        } else if (pct >= 0.15) {
            color = NamedTextColor.RED;
        } else {
            color = NamedTextColor.DARK_RED;
        }

        Component durabilityComponent = Component.text(remaining).color(color);

        lore.add(Component.text("Durability: ")
                .color(NamedTextColor.GRAY)
                .append(durabilityComponent)
                .append(Component.text(" | ")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text(pickaxeMaterial.getMaxDurability())
                        .color(NamedTextColor.GRAY)));

        List<Component> fixedLore = lore.stream()
                .map(component -> component
                        .colorIfAbsent(NamedTextColor.WHITE)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                ).toList();

        return fixedLore;
    }

    public static boolean isPlayerHoldingOwnPickaxe(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!MaterialSetTag.ITEMS_PICKAXES.isTagged(heldItem.getType())) return false;

        NamespacedKey ownerKey = new NamespacedKey(Spellcasting.INSTANCE, "owner");
        String owner = heldItem.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        return player.getUniqueId().toString().equals(owner);
    }

    public static ItemStack getPickaxeCloneWithoutDurability(Player player) {
        return GUIUtils.cloneItemWithoutDamage(getPickaxe(player));
    }
}