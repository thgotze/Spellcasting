package com.gotze.spellcasting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerPickaxe {
    private static final Map<UUID, ItemStack> PLAYER_PICKAXE = new HashMap<>();
    private static final ItemStack STARTER_PICKAXE = new ItemStack(Material.WOODEN_PICKAXE);

    public static ItemStack getPickaxe(Player player) {
        return PLAYER_PICKAXE.get(player.getUniqueId());
    }

    public static void setPickaxe(Player player, @Nullable ItemStack pickaxe) {
        UUID playerUniqueId = player.getUniqueId();

        PLAYER_PICKAXE.put(playerUniqueId, Objects.requireNonNullElse(pickaxe, STARTER_PICKAXE));
    }
}
