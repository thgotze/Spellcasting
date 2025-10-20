package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum PickaxeMaterial {
    WOOD(Material.WOODEN_PICKAXE, NamedTextColor.GRAY, Material.OAK_PLANKS),
    STONE(Material.STONE_PICKAXE, NamedTextColor.WHITE, Material.COBBLESTONE),
    COPPER(Material.COPPER_PICKAXE, NamedTextColor.GREEN, Material.COPPER_INGOT),
    IRON(Material.IRON_PICKAXE, NamedTextColor.AQUA, Material.IRON_INGOT),
    GOLD(Material.GOLDEN_PICKAXE, NamedTextColor.LIGHT_PURPLE, Material.GOLD_INGOT),
    DIAMOND(Material.DIAMOND_PICKAXE, NamedTextColor.GOLD, Material.DIAMOND),
    NETHERITE(Material.NETHERITE_PICKAXE, NamedTextColor.DARK_RED, Material.NETHERITE_INGOT),
    ;

    private final Material pickaxeType;
    private final NamedTextColor textColor;
    private final Material upgradeTokenType;

    PickaxeMaterial(Material pickaxeType, NamedTextColor textColor, Material upgradeTokenType) {
        this.pickaxeType = pickaxeType;
        this.textColor = textColor;
        this.upgradeTokenType = upgradeTokenType;
    }

    public Material pickaxeType() {
        return pickaxeType;
    }

    public Material upgradeTokenType() {
        return upgradeTokenType;
    }

    public NamedTextColor textColor() {
        return textColor;
    }

    public int maxDurability() {
        return this.pickaxeType.getDefaultData(DataComponentTypes.MAX_DAMAGE);
    }

    public PickaxeMaterial nextTier() {
        PickaxeMaterial[] values = values();
        int nextIndex = (ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public Component formattedPickaxeTypeName() {
        return Component.text(StringUtils.toTitleCase(this.pickaxeType.toString()));
    }

    public Component formattedUpgradeTokenName() {
        return Component.text(StringUtils.toTitleCase(this.upgradeTokenType.toString()));
    }

    @Override
    public String toString() {
        return StringUtils.toTitleCase(name());
    }
}