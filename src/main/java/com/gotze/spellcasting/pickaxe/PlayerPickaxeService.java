package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.MenuUtils;
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

    public static PickaxeData pickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.get(player);
    }

    public static ItemStack playerPickaxe(Player player) {
        PickaxeData pickaxeData = pickaxeData(player);

        if (pickaxeData == null) { // TODO: debug remove later when database is setup
            player.sendMessage("No pickaxe data available!");
            return null;
        }

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.pickaxeMaterial().material())
                .persistentDataContainer("owner", player.getUniqueId().toString())
                .lore(pickaxeLore(pickaxeData))
                .hideAttributes()
                .hideEnchantTooltip()
                .durabilityDamage(pickaxeData.durabilityDamage());

        Enchantment efficiency = pickaxeData.getEnchantment(Enchantment.EnchantmentType.EFFICIENCY);
        Enchantment fortune = pickaxeData.getEnchantment(Enchantment.EnchantmentType.FORTUNE);
        Enchantment unbreaking = pickaxeData.getEnchantment(Enchantment.EnchantmentType.UNBREAKING);

        if (efficiency != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.EFFICIENCY, efficiency.level());
        }
        if (fortune != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.FORTUNE, fortune.level());
        }
        if (unbreaking != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.UNBREAKING, unbreaking.level());
        }

        return builder.build();
    }

    public static List<Component> pickaxeLore(PickaxeData pickaxeData) {
        List<Component> lore = new ArrayList<>();

        Set<Enchantment> enchantments = pickaxeData.enchantments();
        if (!enchantments.isEmpty()) {
            // Sort enchantments by rarity in descending order (Legendary -> Common)
            List<Enchantment> sortedEnchantments = enchantments.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.enchantmentType().rarity().ordinal(),
                            e1.enchantmentType().rarity().ordinal()
                    ))
                    .toList();

            for (Enchantment enchantment : sortedEnchantments) {
                Enchantment.EnchantmentType enchantmentType = enchantment.enchantmentType();
                lore.add(enchantmentType.formattedName()
                        .append(Component.text(" " + StringUtils.toRomanNumeral(enchantment.level()))
                                .color(enchantmentType.rarity().color())));
            }
            lore.add(Component.text(""));
        }

        Set<Ability> abilities = pickaxeData.abilities();
        if (!abilities.isEmpty()) {
            // Sort abilities by rarity in descending order (Legendary -> Common)
            List<Ability> sortedAbilities = abilities.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.rarity().ordinal(),
                            e1.rarity().ordinal()
                    ))
                    .toList();

            for (Ability ability : sortedAbilities) {
                Ability.AbilityType abilityType = ability.abilityType();
                lore.add(abilityType.formattedName()
                        .append(Component.text(" " + StringUtils.toRomanNumeral(ability.level()))
                                .color(abilityType.rarity().color())));
            }
            lore.add(Component.text(""));
        }

        int blocksBroken = pickaxeData.blocksBroken();
        lore.add(Component.text("Blocks Broken: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(blocksBroken))
                        .color(NamedTextColor.GOLD)));

        lore.add(Component.text(""));

        PickaxeMaterial pickaxeMaterial = pickaxeData.pickaxeMaterial();
        int damage = pickaxeData.durabilityDamage();
        int remaining = pickaxeMaterial.maxDurability() - damage;
        double percentage = (double) remaining / pickaxeMaterial.maxDurability();

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
                .append(Component.text(pickaxeMaterial.maxDurability())
                        .color(NamedTextColor.GRAY)));

        return lore.stream()
                .map(component -> component
                        .colorIfAbsent(NamedTextColor.WHITE)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                ).toList();
    }

    public static Optional<ItemStack> getPlayerPickaxeFromMainHand(Player player, boolean notifyOnError) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!isItemStackPlayerOwnPickaxe(heldItem, player)) {
            if (notifyOnError) {
                player.sendMessage(Component.text("You are not holding your pickaxe!")
                        .color(NamedTextColor.RED));
                SoundUtils.playErrorSound(player);
            }
            return Optional.empty();
        }
        return Optional.of(heldItem);
    }

    public static boolean isItemStackPlayerOwnPickaxe(ItemStack itemStack, Player player) {
        NamespacedKey ownerKey = new NamespacedKey(JavaPlugin.getPlugin(Spellcasting.class), "owner");
        String owner = itemStack.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        return player.getUniqueId().toString().equals(owner);
    }

    public static ItemStack pickaxeCloneWithoutDurability(Player player) {
        return MenuUtils.cloneItemWithoutDamage(playerPickaxe(player));
    }
}