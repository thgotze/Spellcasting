package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum PickaxeMaterial {
    WOOD(Material.WOODEN_PICKAXE),
    STONE(Material.STONE_PICKAXE),
    IRON(Material.IRON_PICKAXE),
    GOLD(Material.GOLDEN_PICKAXE),
    DIAMOND(Material.DIAMOND_PICKAXE),
    NETHERITE(Material.NETHERITE_PICKAXE),
    ;

    private final Material material;

    PickaxeMaterial(Material material) {
        this.material = material;
    }

    public Material material() {
        return material;
    }

    public int maxDurability() {
        return this.material.getDefaultData(DataComponentTypes.MAX_DAMAGE);
    }

    public PickaxeMaterial nextTier() {
        PickaxeMaterial[] values = values();
        int nextIndex = (ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public ItemStack upgradeToken() {
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

    public Component upgradeTokenName() {
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