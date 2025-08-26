package com.gotze.spellcasting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
}