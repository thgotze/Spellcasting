package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public enum PickaxeMaterial {
    WOOD(Material.WOODEN_PICKAXE, Rarity.COMMON, Material.OAK_PLANKS),
    STONE(Material.STONE_PICKAXE, Rarity.COMMON, Material.COBBLESTONE),
    COPPER(Material.COPPER_PICKAXE, Rarity.UNCOMMON, Material.COPPER_INGOT),
    IRON(Material.IRON_PICKAXE, Rarity.RARE, Material.IRON_INGOT),
    DIAMOND(Material.DIAMOND_PICKAXE, Rarity.EPIC, Material.DIAMOND),
    NETHERITE(Material.NETHERITE_PICKAXE, Rarity.LEGENDARY, Material.NETHERITE_INGOT),
    ;

    private final Material pickaxeType;
    private final Rarity rarity;
    private final Material upgradeTokenType;

    PickaxeMaterial(Material pickaxeType, Rarity rarity, Material upgradeTokenType) {
        this.pickaxeType = pickaxeType;
        this.rarity = rarity;
        this.upgradeTokenType = upgradeTokenType;
    }

    public Material getPickaxeType() {
        return pickaxeType;
    }

    public Material getUpgradeTokenType() {
        return upgradeTokenType;
    }

    public Rarity getRarity() {
        return rarity;
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