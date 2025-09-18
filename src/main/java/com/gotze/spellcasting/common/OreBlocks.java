package com.gotze.spellcasting.common;

import org.bukkit.Material;

import java.util.Map;

public class OreBlocks {
    public static final Map<Material, Drop> ORE_BLOCKS = Map.ofEntries(
            Map.entry(Material.IRON_ORE, new Drop(Material.RAW_IRON, 1)),
            Map.entry(Material.DEEPSLATE_IRON_ORE, new Drop(Material.RAW_IRON, 2)),
            Map.entry(Material.RAW_IRON_BLOCK, new Drop(Material.RAW_IRON, 3)),

            Map.entry(Material.NETHER_GOLD_ORE, new Drop(Material.GOLD_NUGGET, 2, 6)),
            Map.entry(Material.GOLD_ORE, new Drop(Material.RAW_GOLD, 1)),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, new Drop(Material.RAW_GOLD, 2)),
            Map.entry(Material.RAW_GOLD_BLOCK, new Drop(Material.RAW_GOLD, 3)),

            Map.entry(Material.COPPER_ORE, new Drop(Material.RAW_COPPER, 2, 5)),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, new Drop(Material.RAW_COPPER, 4, 10)),
            Map.entry(Material.RAW_COPPER_BLOCK, new Drop(Material.RAW_COPPER, 6, 15)),

            Map.entry(Material.COAL_ORE, new Drop(Material.COAL, 1)),
            Map.entry(Material.DEEPSLATE_COAL_ORE, new Drop(Material.COAL, 2)),
            Map.entry(Material.COAL_BLOCK, new Drop(Material.COAL, 3)),

            Map.entry(Material.LAPIS_ORE, new Drop(Material.LAPIS_LAZULI, 4, 9)),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, new Drop(Material.LAPIS_LAZULI, 8, 18)),
            Map.entry(Material.LAPIS_BLOCK, new Drop(Material.LAPIS_LAZULI, 16, 27)),

            Map.entry(Material.REDSTONE_ORE, new Drop(Material.REDSTONE, 4, 5)),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new Drop(Material.REDSTONE, 8, 10)),
            Map.entry(Material.REDSTONE_BLOCK, new Drop(Material.REDSTONE, 12, 15)),

            Map.entry(Material.DIAMOND_ORE, new Drop(Material.DIAMOND, 1)),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new Drop(Material.DIAMOND, 2)),
            Map.entry(Material.DIAMOND_BLOCK, new Drop(Material.DIAMOND, 3)),

            Map.entry(Material.EMERALD_ORE, new Drop(Material.EMERALD, 1)),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, new Drop(Material.EMERALD, 2)),
            Map.entry(Material.EMERALD_BLOCK, new Drop(Material.EMERALD, 3)),

            Map.entry(Material.NETHER_QUARTZ_ORE, new Drop(Material.QUARTZ, 1)),
            Map.entry(Material.QUARTZ_BLOCK, new Drop(Material.QUARTZ, 2)),

            Map.entry(Material.SMALL_AMETHYST_BUD, new Drop(Material.AMETHYST_SHARD, 1)),
            Map.entry(Material.MEDIUM_AMETHYST_BUD, new Drop(Material.AMETHYST_SHARD, 2)),
            Map.entry(Material.LARGE_AMETHYST_BUD, new Drop(Material.AMETHYST_SHARD, 3)),
            Map.entry(Material.AMETHYST_CLUSTER, new Drop(Material.AMETHYST_SHARD, 4, 6)),
            Map.entry(Material.AMETHYST_BLOCK, new Drop(Material.AMETHYST_SHARD, 4))
    );
}
