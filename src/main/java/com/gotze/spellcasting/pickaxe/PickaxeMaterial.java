package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
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


    public ItemStack getUpgradeToken() {
        Material material = switch (this) {
            case WOOD -> Material.OAK_PLANKS; // TODO: debug
            case STONE -> Material.COBBLESTONE;
            case IRON -> Material.IRON_INGOT;
            case GOLD -> Material.GOLD_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case NETHERITE -> Material.NETHERITE_INGOT;
        };

        return ItemStack.of(material);
    }

    public Component getUpgradeTokenName() {
        Material material = switch (this) {
            case WOOD -> Material.OAK_PLANKS; // TODO: debug
            case STONE -> Material.COBBLESTONE;
            case IRON -> Material.IRON_INGOT;
            case GOLD -> Material.GOLD_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case NETHERITE -> Material.NETHERITE_INGOT;
        };

        return Component.text(StringUtils.toTitleCase(material.toString()));
    }

    @Override
    public String toString() {
        return StringUtils.toTitleCase(name());
    }
}