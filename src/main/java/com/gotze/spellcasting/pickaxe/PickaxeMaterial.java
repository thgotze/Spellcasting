package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public enum PickaxeMaterial {
    WOOD(Material.WOODEN_PICKAXE, Material.OAK_PLANKS),
    STONE(Material.STONE_PICKAXE, Material.COBBLESTONE),
    COPPER(Material.COPPER_PICKAXE, Material.COPPER_INGOT),
    IRON(Material.IRON_PICKAXE, Material.IRON_INGOT),
    DIAMOND(Material.DIAMOND_PICKAXE, Material.DIAMOND),
    NETHERITE(Material.NETHERITE_PICKAXE, Material.NETHERITE_INGOT),
    ;

    private final Material pickaxeType;
    private final Material upgradeTokenType;

    PickaxeMaterial(Material pickaxeType, Material upgradeTokenType) {
        this.pickaxeType = pickaxeType;
        this.upgradeTokenType = upgradeTokenType;
    }

    public Material getPickaxeType() {
        return pickaxeType;
    }

    public Material getUpgradeTokenType() {
        return upgradeTokenType;
    }

    public int getMaxDurability() {
        return this.pickaxeType.getDefaultData(DataComponentTypes.MAX_DAMAGE);
    }

    public PickaxeMaterial getNextTier() {
        PickaxeMaterial[] values = values();
        int nextIndex = (ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public Component getFormattedPickaxeTypeName() {
        return Component.text(StringUtils.toTitleCase(this.pickaxeType.toString()));
    }

    public Component getFormattedUpgradeTokenName() {
        return Component.text(StringUtils.toTitleCase(this.upgradeTokenType.toString()));
    }

    @Override
    public String toString() {
        return StringUtils.toTitleCase(name());
    }
}