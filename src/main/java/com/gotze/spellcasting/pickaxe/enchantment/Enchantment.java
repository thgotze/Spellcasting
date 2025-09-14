package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.Rarity;
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
        EFFICIENCY(5, Rarity.BASIC, EfficiencyEnchantment.class),
        UNBREAKING(3, Rarity.BASIC, UnbreakingEnchantment.class),
        FORTUNE(3, Rarity.BASIC, FortuneEnchantment.class),
        // custom
        REINFORCED(5, Rarity.UNIQUE, ReinforcedEnchantment.class),
        MOMENTUM(5, Rarity.EPIC, MomentumEnchantment.class);

        private final int maxLevel;
        private final Rarity rarity;
        private final Class<? extends Enchantment> enchantmentClass;

        EnchantmentType(int maxLevel, Rarity rarity, Class<? extends Enchantment> enchantmentClass) {
            this.maxLevel = maxLevel;
            this.rarity = rarity;
            this.enchantmentClass = enchantmentClass;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public Class<? extends Enchantment> getEnchantmentClass() {
            return enchantmentClass;
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
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {}
    }

    public static class FortuneEnchantment extends Enchantment {
        public FortuneEnchantment() {
            super(EnchantmentType.FORTUNE);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {}
    }

    public static class UnbreakingEnchantment extends Enchantment {
        public UnbreakingEnchantment() {
            super(EnchantmentType.UNBREAKING);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {}
    }
}