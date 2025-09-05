package com.gotze.spellcasting.data;

import com.gotze.spellcasting.enchantment.Enchantment;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerPickaxeService {
    private static final Map<Player, PlayerPickaxeData> PLAYER_PICKAXE_DATA_MAP = new HashMap<>();

    public static PlayerPickaxeData getPlayerPickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.get(player);
    }

    public static ItemStack getPlayerPickaxe(Player player) {
        PlayerPickaxeData playerPickaxeData = getPlayerPickaxeData(player);
        return playerPickaxeData.getPickaxe();
    }

    public static void setPlayerPickaxeData(Player player, PlayerPickaxeData playerPickaxeData) {
        PLAYER_PICKAXE_DATA_MAP.put(player, playerPickaxeData);
    }

    public static void setPlayerPickaxeData(Player player, ItemStack playerPickaxe) {
        PLAYER_PICKAXE_DATA_MAP.put(player, new PlayerPickaxeData(player, playerPickaxe));
    }

    public static boolean hasPlayerPickaxedata(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.containsKey(player);
    }

    public static ItemStack getPlayerPickaxeCloneWithoutDurability(Player player) {
        ItemStack playerPickaxeClone = getPlayerPickaxe(player).clone();
        playerPickaxeClone.resetData(DataComponentTypes.DAMAGE);
        return playerPickaxeClone;
    }

    public static void upgradePlayerPickaxeMaterial(Player player, Material nextTierPickaxe) {
        getPlayerPickaxeData(player).setType(nextTierPickaxe);
    }

    public static void upgradePlayerPickaxeEnchantment(Player player, Enchantment.EnchantmentType enchantmentType) {
        PlayerPickaxeData playerPickaxeData = getPlayerPickaxeData(player);

        if (playerPickaxeData.hasEnchantment(enchantmentType)) {
            playerPickaxeData.getEnchantment(enchantmentType).increaseLevel();
        } else {
            playerPickaxeData.addEnchantment(new Enchantment(enchantmentType));
        }
    }

    public static ItemStack createStarterPickaxe(Player player) {
        ItemStack starterPick = ItemStack.of(Material.WOODEN_PICKAXE);

        // Hiding attributes
        ItemAttributeModifiers data = starterPick.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        data = data.showInTooltip(false);
        starterPick.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, data);

//        // PDC owner tag
//        ItemMeta meta = starterPick.getItemMeta();
//        meta.getPersistentDataContainer().set(
//                new NamespacedKey(Spellcasting.INSTANCE, "owner"),
//                PersistentDataType.STRING,
//                player.getUniqueId().toString()
//        );
//        starterPick.setItemMeta(meta);

        // Save player pickaxe data in memory
        PlayerPickaxeService.setPlayerPickaxeData(player, starterPick);

        return starterPick;
    }

    public static boolean isPlayerHoldingOwnPickaxe(Player player, ItemStack heldItem) {
        return heldItem.equals(getPlayerPickaxe(player));
    }

//    public static boolean isPlayerHoldingOwnPickaxe(Player player, ItemStack heldItem) {
//        if (heldItem == null || !heldItem.hasItemMeta()) return false;
//        ItemMeta meta = heldItem.getItemMeta();
//        String ownerId = meta.getPersistentDataContainer().get(
//                new NamespacedKey(Spellcasting.INSTANCE, "owner"),
//                PersistentDataType.STRING
//        );
//        return player.getUniqueId().toString().equals(ownerId);

//    }
}