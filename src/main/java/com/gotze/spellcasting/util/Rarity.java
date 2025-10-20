package com.gotze.spellcasting.util;

import net.kyori.adventure.text.format.NamedTextColor;

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

    public NamedTextColor color() {
        return color;
    }

    public double weight() {
        return weight;
    }

    @Override
    public String toString() {
        return StringUtils.toTitleCase(name());
    }
}