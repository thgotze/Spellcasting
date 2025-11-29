package com.gotze.spellcasting.util;

import org.bukkit.entity.Player;

public class PermissionUtils {

    public static final String ADMIN = "spellcasting.admin";

    public static final String PICKAXE_GET = "spellcasting.pickaxe.get";
    public static final String PICKAXE_REPAIR = "spellcasting.pickaxe.repair";
    public static final String PICKAXE_RESET = "spellcasting.pickaxe.reset";
    public static final String PICKAXE_DEBUG = "spellcasting.pickaxe.debug";

    public static boolean isAdmin(Player player) {
        return player.hasPermission(ADMIN);
    }
}