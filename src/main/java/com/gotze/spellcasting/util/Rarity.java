package com.gotze.spellcasting.util;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.ThreadLocalRandom;

public enum Rarity {
    COMMON(NamedTextColor.WHITE, 0.5061),
    UNCOMMON(NamedTextColor.GREEN, 0.2581),
    RARE(NamedTextColor.AQUA, 0.1290),
    EPIC(NamedTextColor.LIGHT_PURPLE, 0.0645),
    LEGENDARY(NamedTextColor.GOLD, 0.0323),
    ;

    private final NamedTextColor color;
    private final double weight;

    Rarity(NamedTextColor color, double weight) {
        this.color = color;
        this.weight = weight;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public double getWeight() {
        return weight;
    }

    public static Rarity getRandom() {
        double totalWeight = 0;
        for (Rarity rarity : values()) {
            totalWeight += rarity.weight;
        }

        double target = ThreadLocalRandom.current().nextDouble(totalWeight);
        for (Rarity rarity : values()) {
            target -= rarity.weight;
            if (target <= 0) {
                return rarity;
            }
        }
        return COMMON;
    }

    @Override
    public String toString() {
        return StringUtils.toTitleCase(name());
    }
}