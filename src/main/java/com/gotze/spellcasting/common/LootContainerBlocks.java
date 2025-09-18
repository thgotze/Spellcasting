package com.gotze.spellcasting.common;

import org.bukkit.Material;

import java.util.Map;

public class LootContainerBlocks {
    public static final Map<Material, Rarity> LOOT_CONTAINERS = Map.of(
            Material.WHITE_SHULKER_BOX, Rarity.COMMON,
            Material.LIME_SHULKER_BOX, Rarity.UNCOMMON,
            Material.LIGHT_BLUE_SHULKER_BOX, Rarity.RARE,
            Material.MAGENTA_SHULKER_BOX, Rarity.EPIC,
            Material.ORANGE_SHULKER_BOX, Rarity.LEGENDARY,

            Material.DECORATED_POT, Rarity.COMMON
    );
}
