package com.gotze.spellcasting.feature.pickaxe.enchantment;

import com.gotze.spellcasting.common.Rarity;
import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public abstract class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;

    public Enchantment(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.level = 1;
    }

    public abstract void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData);

    public EnchantmentType getEnchantmentType() {
        return enchantmentType;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return enchantmentType.getMaxLevel();
    }

    public boolean isMaxLevel() {
        return level == enchantmentType.getMaxLevel();
    }

    public void increaseLevel() {
        if (level < getMaxLevel()) {
            this.level++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enchantment that)) return false;
        return level == that.level &&
                enchantmentType == that.enchantmentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enchantmentType, level);
    }

    @Override
    public String toString() {
        return "Enchantment{" +
                "enchantmentType=" + enchantmentType +
                ", level=" + level +
                '}';
    }

    public enum EnchantmentType {
        EFFICIENCY(EfficiencyEnchantment.class, Rarity.COMMON, 5, Material.REDSTONE),
        UNBREAKING(UnbreakingEnchantment.class, Rarity.COMMON, 3, Material.OBSIDIAN),
        FORTUNE(FortuneEnchantment.class, Rarity.COMMON, 3, Material.LAPIS_LAZULI),
        // custom
        UNCOVER(UncoverEnchantment.class, Rarity.UNCOMMON, 5, Material.DECORATED_POT),
        MOMENTUM(MomentumEnchantment.class, Rarity.RARE, 5, Material.SUGAR),
        OVERLOAD(OverloadEnchantment.class, Rarity.EPIC, 5, Material.TNT),
        ;

        private final Class<? extends Enchantment> enchantmentClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material materialRepresentation;

        EnchantmentType(Class<? extends Enchantment> enchantmentClass, Rarity rarity, int maxLevel, Material materialRepresentation) {
            this.enchantmentClass = enchantmentClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
            this.materialRepresentation = materialRepresentation;
        }

        public Class<? extends Enchantment> getEnchantmentClass() {
            return enchantmentClass;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public Material getMaterialRepresentation() {
            return materialRepresentation;
        }

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static class EfficiencyEnchantment extends Enchantment {
        public EfficiencyEnchantment() {
            super(EnchantmentType.EFFICIENCY);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }

    public static class FortuneEnchantment extends Enchantment {
        public FortuneEnchantment() {
            super(EnchantmentType.FORTUNE);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }

    public static class UnbreakingEnchantment extends Enchantment {
        public UnbreakingEnchantment() {
            super(EnchantmentType.UNBREAKING);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }
}