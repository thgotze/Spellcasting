package com.gotze.spellcasting;

import net.kyori.adventure.text.format.NamedTextColor;

public enum Rarity {
    BASIC(NamedTextColor.WHITE),
    UNIQUE(NamedTextColor.GREEN),
    ELITE(NamedTextColor.AQUA),
    EPIC(NamedTextColor.LIGHT_PURPLE),
    LEGENDARY(NamedTextColor.GOLD);

    private final NamedTextColor color;

    Rarity(NamedTextColor color) {
        this.color = color;
    }

    public NamedTextColor getColor() {
        return color;
    }
}