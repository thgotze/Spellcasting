package com.gotze.spellcasting.util;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.World;

public class WorldUtils {

    private static final World world = Spellcasting.getPlugin().getServer().getWorld("world");

    public static World getWorld() {
        return world;
    }
}
