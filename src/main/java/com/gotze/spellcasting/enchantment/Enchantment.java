package com.gotze.spellcasting.enchantment;

import java.util.Objects;

public class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;

    public Enchantment(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.level = 1;
    }

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
        EFFICIENCY("Efficiency", 5),
        UNBREAKING("Unbreaking", 3),
        FORTUNE("Fortune", 3),
        HASTE_AND_SPEED("Haste And Speed", 3),
        MINE_BLOCK_ABOVE("Mine Block Above", 1);

        private final String name;
        private final int maxLevel;

        EnchantmentType(String name, int maxLevel) {
            this.name = name;
            this.maxLevel = maxLevel;
        }

        public String getName() {
            return name;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }
}