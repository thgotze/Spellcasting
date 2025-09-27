package com.gotze.spellcasting.feature.pickaxe;

import com.gotze.spellcasting.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum PickaxeMaterial {
    WOOD(Material.WOODEN_PICKAXE, 256),
    STONE(Material.STONE_PICKAXE, 512),
    IRON(Material.IRON_PICKAXE, 1024),
    GOLD(Material.GOLDEN_PICKAXE, 2048),
    DIAMOND(Material.DIAMOND_PICKAXE, 4096),
    NETHERITE(Material.NETHERITE_PICKAXE, 8192),
    ;

    private final Material material;
    private final int maxDurability;

    PickaxeMaterial(Material material, int maxDurability) {
        this.material = material;
        this.maxDurability = maxDurability;
    }

    public Material getType() {
        return material;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public PickaxeMaterial getNextTier() {
        PickaxeMaterial[] values = values();
        int nextIndex = (ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public ItemStack getUpgradeMaterial() {
        return switch (this) {
            case WOOD -> new ItemStackBuilder(Material.OAK_PLANKS).build(); // TODO: debug
            case STONE -> new ItemStackBuilder(Material.COBBLESTONE).build();
            case IRON -> new ItemStackBuilder(Material.IRON_INGOT).build();
            case GOLD -> new ItemStackBuilder(Material.GOLD_INGOT).build();
            case DIAMOND -> new ItemStackBuilder(Material.DIAMOND).build();
            case NETHERITE -> new ItemStackBuilder(Material.NETHERITE_INGOT).build();
        };
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}