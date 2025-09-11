package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Rarity;
import com.gotze.spellcasting.pickaxe.PickaxeData;
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

    public String getName() {
        return enchantmentType.getName();
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
        EFFICIENCY("Efficiency", 5, Rarity.BASIC),
        UNBREAKING("Unbreaking", 3, Rarity.BASIC),
        FORTUNE("Fortune", 3, Rarity.BASIC),
        HASTE_AND_SPEED("Haste And Speed", 5, Rarity.UNIQUE),
        MINE_BLOCK_ABOVE("Mine Block Above", 5, Rarity.UNIQUE);

        private final String name;
        private final int maxLevel;
        private final Rarity rarity;

        EnchantmentType(String name, int maxLevel, Rarity rarity) {
            this.name = name;
            this.maxLevel = maxLevel;
            this.rarity = rarity;
        }

        public String getName() {
            return name;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public Rarity getRarity() {
            return rarity;
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