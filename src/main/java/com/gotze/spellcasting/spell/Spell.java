package com.gotze.spellcasting.spell;

import org.bukkit.entity.Player;

public abstract class Spell {
    public String spellName;
    public Player player;

    public abstract void cast();
}