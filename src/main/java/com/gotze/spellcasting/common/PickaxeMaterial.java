package com.gotze.spellcasting.common;

import org.bukkit.Material;

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

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}