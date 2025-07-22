package com.gotze.magicParticles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public abstract class AbstractSpell {
    protected final JavaPlugin plugin;
    protected final Location location;
    protected final Player player;

    public AbstractSpell(JavaPlugin plugin, Location location, @Nullable Player player) {
        this.plugin = plugin;
        this.location = location;
        this.player = player;
    }

    protected abstract void spawn();

    protected abstract void remove();
}