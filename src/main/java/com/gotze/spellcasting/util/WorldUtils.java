package com.gotze.spellcasting.util;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WorldUtils {

    private static final World world = Spellcasting.getPlugin().getServer().getWorld("world");

    public static @NotNull World getWorld() {
        return Objects.requireNonNull(world);
    }
}
