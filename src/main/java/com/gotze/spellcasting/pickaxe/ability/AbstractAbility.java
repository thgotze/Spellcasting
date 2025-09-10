package com.gotze.spellcasting.pickaxe.ability;

import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class AbstractAbility {
    public Player player;
    public World world;

    public AbstractAbility(Player player) {
        this.player = player;
        this.world = player.getWorld();
    }

    public abstract void activate();
}