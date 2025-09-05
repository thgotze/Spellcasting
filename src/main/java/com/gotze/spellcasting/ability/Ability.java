package com.gotze.spellcasting.ability;

import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Ability {
    public Player player;
    public World world;

    public Ability(Player player) {
        this.player = player;
        this.world = player.getWorld();
    }

    public abstract void cast();
}