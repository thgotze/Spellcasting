package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.util.menu.MenuUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

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

        if (pickaxeData == null) { // TODO: debug remove later
            player.sendMessage("No pickaxe data available!");
            return null;
        }

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getPickaxeMaterial().getType())
                .persistentDataContainer("owner", player.getUniqueId().toString())
                .lore(getPickaxeLore(pickaxeData))
                .hideAttributes()
                .hideEnchantTooltip()
                .durabilityDamage(pickaxeData.getDurabilityDamage())
                .maxDurability(pickaxeData.getPickaxeMaterial().getMaxDurability());

        Enchantment efficiency = pickaxeData.getEnchantment(Enchantment.EnchantmentType.EFFICIENCY);
        Enchantment fortune = pickaxeData.getEnchantment(Enchantment.EnchantmentType.FORTUNE);
        Enchantment unbreaking = pickaxeData.getEnchantment(Enchantment.EnchantmentType.UNBREAKING);

        if (efficiency != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.EFFICIENCY, efficiency.getLevel());
        }
        if (fortune != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.FORTUNE, fortune.getLevel());
        }
        if (unbreaking != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.UNBREAKING, unbreaking.getLevel());
        }

        return builder.build();
    }

    public static List<Component> getPickaxeLore(PickaxeData pickaxeData) {
        List<Component> lore = new ArrayList<>();

        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
        if (!enchantments.isEmpty()) {
            // Sort enchantments by rarity in descending order (Legendary -> Common)
            List<Enchantment> sortedEnchantments = enchantments.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getEnchantmentType().getRarity().ordinal(),
                            e1.getEnchantmentType().getRarity().ordinal()
                    ))
                    .toList();

            for (Enchantment enchantment : sortedEnchantments) {
                Enchantment.EnchantmentType enchantmentType = enchantment.getEnchantmentType();
                lore.add(enchantmentType.getColoredName()
                        .append(Component.text(" " + StringUtils.toRomanNumeral(enchantment.getLevel()))
                                .color(enchantmentType.getRarity().getColor())));
            }
            lore.add(Component.text(""));
        }

        Set<Ability> abilities = pickaxeData.getAbilities();
        if (!abilities.isEmpty()) {
            // Sort abilities by rarity in descending order (Legendary -> Common)
            List<Ability> sortedAbilities = abilities.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getRarity().ordinal(),
                            e1.getRarity().ordinal()
                    ))
                    .toList();

            for (Ability ability : sortedAbilities) {
                Ability.AbilityType abilityType = ability.getAbilityType();
                lore.add(abilityType.getColoredName()
                        .append(Component.text(" " + StringUtils.toRomanNumeral(ability.getLevel()))
                                .color(abilityType.getRarity().getColor())));
            }
            lore.add(Component.text(""));
        }

        int blocksBroken = pickaxeData.getBlocksBroken();
        lore.add(Component.text("Blocks Broken: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(blocksBroken))
                        .color(NamedTextColor.GOLD)));

        lore.add(Component.text(""));

        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();
        int damage = pickaxeData.getDurabilityDamage();
        int remaining = pickaxeMaterial.getMaxDurability() - damage;
        double percentage = (double) remaining / pickaxeMaterial.getMaxDurability();

        NamedTextColor durabilityColor;
        if (percentage >= 0.60) {
            durabilityColor = NamedTextColor.GREEN;
        } else if (percentage >= 0.30) {
            durabilityColor = NamedTextColor.YELLOW;
        } else if (percentage >= 0.20) {
            durabilityColor = NamedTextColor.GOLD;
        } else if (percentage >= 0.15) {
            durabilityColor = NamedTextColor.RED;
        } else {
            durabilityColor = NamedTextColor.DARK_RED;
        }

        lore.add(Component.text("Durability: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(remaining)
                        .color(durabilityColor))
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

    public static boolean isPlayerHoldingOwnPickaxe(Player player, boolean notifyOnError) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        NamespacedKey ownerKey = new NamespacedKey(JavaPlugin.getPlugin(Spellcasting.class), "owner");
        String owner = heldItem.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);

        boolean result = player.getUniqueId().toString().equals(owner);
        if (!result && notifyOnError) {
            player.sendMessage(Component.text("You are not holding your pickaxe!")
                    .color(NamedTextColor.RED));
            SoundUtils.playErrorSound(player);
        }
        return result;
    }

    public static ItemStack getPickaxeCloneWithoutDurability(Player player) {
        return MenuUtils.cloneItemWithoutDamage(getPickaxe(player));
    }
}