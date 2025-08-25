package com.gotze.spellcasting;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPickaxeService {
    private final Map<UUID, PlayerPickaxeManager> playerManagers = new HashMap<>();

    public PlayerPickaxeManager getManager(Player player) {
        return playerManagers.computeIfAbsent(
                player.getUniqueId(),
                k -> new PlayerPickaxeManager(player)
        );
    }

    public void removePlayer(Player player) {
        playerManagers.remove(player.getUniqueId());
    }
}