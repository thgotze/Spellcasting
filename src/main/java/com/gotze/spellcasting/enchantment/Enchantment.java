package com.gotze.spellcasting.enchantment;

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

    public enum EnchantmentType {
        EFFICIENCY("Efficiency", 5),
        UNBREAKING("Unbreaking", 3),
        FORTUNE("Fortune", 3),
        CUSTOM_ENCHANT("Custom Enchant", 7); // TODO: temp

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

