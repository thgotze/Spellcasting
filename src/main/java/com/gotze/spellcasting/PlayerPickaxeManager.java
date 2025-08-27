package com.gotze.spellcasting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerPickaxeManager {
    private static Map<Player, ItemStack> PLAYER_PICKAXE_MAP = new HashMap<>();

    public static ItemStack getPlayerPickaxe(Player player) {
        return PLAYER_PICKAXE_MAP.getOrDefault(player,
                new ItemStack(Material.WOODEN_PICKAXE)
        );
    }

    public static void setPlayerPickaxe(Player player, ItemStack pickaxe) {
        PLAYER_PICKAXE_MAP.put(player, pickaxe);
    }

    public static void upgradePlayerPickaxeMaterial(Player player) {
        ItemStack playerPickaxe = getPlayerPickaxe(player);
        Inventory playerInvetory = player.getInventory();

        switch (playerPickaxe.getType()) {
            case WOODEN_PICKAXE: if (
            case STONE_PICKAXE: //
            case IRON_PICKAXE: //
            case GOLDEN_PICKAXE: //
            case DIAMOND_PICKAXE: //
            case NETHERITE_PICKAXE: //
        }
    }
}