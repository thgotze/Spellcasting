package com.gotze.spellcasting.util.block;

import com.gotze.spellcasting.util.Loot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class BlockCategories {
    public static final Set<Material> FILLER_BLOCKS = Set.of(
            Material.STONE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,
            Material.SANDSTONE,
            Material.DEEPSLATE,
            Material.BASALT,
            Material.SMOOTH_BASALT,
            Material.CALCITE,
            Material.TUFF,
            Material.NETHERRACK,
            Material.END_STONE
    );

    public static final Map<Material, Loot> ORE_BLOCKS = Map.ofEntries(
            Map.entry(Material.COAL_ORE, new Loot(ItemStack.of(Material.COAL), 1)),
            Map.entry(Material.DEEPSLATE_COAL_ORE, new Loot(ItemStack.of(Material.COAL), 2)),
            Map.entry(Material.COAL_BLOCK, new Loot(ItemStack.of(Material.COAL), 3)),

            Map.entry(Material.COPPER_ORE, new Loot(ItemStack.of(Material.RAW_COPPER), 2, 5)),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, new Loot(ItemStack.of(Material.RAW_COPPER), 4, 10)),
            Map.entry(Material.RAW_COPPER_BLOCK, new Loot(ItemStack.of(Material.RAW_COPPER), 6, 15)),

            Map.entry(Material.IRON_ORE, new Loot(ItemStack.of(Material.RAW_IRON), 1)),
            Map.entry(Material.DEEPSLATE_IRON_ORE, new Loot(ItemStack.of(Material.RAW_IRON), 2)),
            Map.entry(Material.RAW_IRON_BLOCK, new Loot(ItemStack.of(Material.RAW_IRON), 3)),

            Map.entry(Material.GOLD_ORE, new Loot(ItemStack.of(Material.RAW_GOLD), 1)),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, new Loot(ItemStack.of(Material.RAW_GOLD), 2)),
            Map.entry(Material.RAW_GOLD_BLOCK, new Loot(ItemStack.of(Material.RAW_GOLD), 3)),

            Map.entry(Material.NETHER_GOLD_ORE, new Loot(ItemStack.of(Material.GOLD_NUGGET), 2, 6)),

            Map.entry(Material.LAPIS_ORE, new Loot(ItemStack.of(Material.LAPIS_LAZULI), 4, 9)),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, new Loot(ItemStack.of(Material.LAPIS_LAZULI), 8, 18)),
            Map.entry(Material.LAPIS_BLOCK, new Loot(ItemStack.of(Material.LAPIS_LAZULI), 16, 27)),

            Map.entry(Material.REDSTONE_ORE, new Loot(ItemStack.of(Material.REDSTONE), 4, 5)),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new Loot(ItemStack.of(Material.REDSTONE), 8, 10)),
            Map.entry(Material.REDSTONE_BLOCK, new Loot(ItemStack.of(Material.REDSTONE), 12, 15)),

            Map.entry(Material.DIAMOND_ORE, new Loot(ItemStack.of(Material.DIAMOND), 1)),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new Loot(ItemStack.of(Material.DIAMOND), 2)),
            Map.entry(Material.DIAMOND_BLOCK, new Loot(ItemStack.of(Material.DIAMOND), 3)),

            Map.entry(Material.EMERALD_ORE, new Loot(ItemStack.of(Material.EMERALD), 1)),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, new Loot(ItemStack.of(Material.EMERALD), 2)),
            Map.entry(Material.EMERALD_BLOCK, new Loot(ItemStack.of(Material.EMERALD), 3)),

            Map.entry(Material.NETHER_QUARTZ_ORE, new Loot(ItemStack.of(Material.QUARTZ), 1)),
            Map.entry(Material.QUARTZ_BLOCK, new Loot(ItemStack.of(Material.QUARTZ), 2)),

            Map.entry(Material.SMALL_AMETHYST_BUD, new Loot(ItemStack.of(Material.AMETHYST_SHARD), 1)),
            Map.entry(Material.MEDIUM_AMETHYST_BUD, new Loot(ItemStack.of(Material.AMETHYST_SHARD), 2)),
            Map.entry(Material.LARGE_AMETHYST_BUD, new Loot(ItemStack.of(Material.AMETHYST_SHARD), 3)),
            Map.entry(Material.AMETHYST_CLUSTER, new Loot(ItemStack.of(Material.AMETHYST_SHARD), 4, 6)),
            Map.entry(Material.AMETHYST_BLOCK, new Loot(ItemStack.of(Material.AMETHYST_SHARD), 4))
    );

    private BlockCategories() {}
}
