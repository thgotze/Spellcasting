package com.gotze.spellcasting.data;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerPickaxeService {
    private static final Map<Player, PickaxeData> PLAYER_PICKAXE_DATA_MAP = new HashMap<>();

    public static void createPlayerPickaxeData(Player player) {
        PLAYER_PICKAXE_DATA_MAP.put(player, new PickaxeData());
    }

    public static PickaxeData getPlayerPickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.get(player);
    }

    public static boolean hasPlayerPickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.containsKey(player);
    }

    public static ItemStack getPlayerPickaxe(Player player) {
        PickaxeData pickaxeData = getPlayerPickaxeData(player);

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getType())
                .lore(getPickaxeLore(player))
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

    public static List<Component> getPickaxeLore(Player player) {
        return getPickaxeLore(getPlayerPickaxeData(player));
    }

    public static List<Component> getPickaxeLore(PickaxeData pickaxeData) {
//        Material material = pickaxeData.getType();
//        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
//        Set<Ability> abilities = pickaxeData.getAbilities();
//        int blocksBroken = pickaxeData.getBlocksBroken();
//        int damage = pickaxeData.getDamage();

        List<Component> lore = List.of(Component.text(pickaxeData.toString()));

        List<Component> fixedLore = lore.stream()
                .map(component -> component
                        .colorIfAbsent(NamedTextColor.WHITE)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                ).toList();

        return fixedLore;
    }

    public static void upgradePlayerPickaxeMaterial(Player player, Material nextTierPickaxe) {
        getPlayerPickaxeData(player).setType(nextTierPickaxe);
    }

    public static void upgradePlayerPickaxeEnchantment(Player player, Enchantment.EnchantmentType enchantmentType) {
        PickaxeData pickaxeData = getPlayerPickaxeData(player);

        if (pickaxeData.hasEnchantment(enchantmentType)) {
            pickaxeData.getEnchantment(enchantmentType).increaseLevel();
        } else {
            pickaxeData.addEnchantment(new Enchantment(enchantmentType));
        }
    }

    public static void upgradePlayerPickaxeAbility(Player player, Ability.AbilityType abilityType) {
        PickaxeData pickaxeData = getPlayerPickaxeData(player);

        if (pickaxeData.hasAbility(abilityType)) {
            pickaxeData.getAbility(abilityType).increaseLevel();
        } else {
            pickaxeData.addAbility(new Ability(abilityType));
        }
    }

    public static void incrementBlocksBrokenCounter(Player player, int amount) {
        getPlayerPickaxeData(player).addBlocksBroken(amount);
    }

    public static void removeAllPlayerPickaxeEnchantments(Player player) {
        getPlayerPickaxeData(player).removeEnchantments();
    }

    public static boolean isPlayerHoldingOwnPickaxe(Player player) {
        return player.getInventory().getItemInMainHand()
                .matchesWithoutData(getPlayerPickaxe(player), Set.of(DataComponentTypes.DAMAGE));
    }

    public static ItemStack getPlayerPickaxeCloneWithoutDurability(Player player) {
        ItemStack playerPickaxeClone = getPlayerPickaxe(player).clone();
        playerPickaxeClone.resetData(DataComponentTypes.DAMAGE);
        return playerPickaxeClone;
    }
}