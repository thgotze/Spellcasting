package com.gotze.spellcasting.spell;

import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Spell {
    public String spellName;
    public Player player;
    public World world;

    public abstract void cast();
}