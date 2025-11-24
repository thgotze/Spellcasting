package com.gotze.spellcasting.data;

import net.kyori.adventure.text.format.NamedTextColor;

public enum Rank {
    A(NamedTextColor.WHITE, 0),
    B(NamedTextColor.GREEN, 10_000),
    C(NamedTextColor.AQUA, 100_000),
    D(NamedTextColor.LIGHT_PURPLE, 1_000_000),
    E(NamedTextColor.GOLD, 10_000_000),
    ;

    private final NamedTextColor color;
    private final int cost;

    Rank(NamedTextColor color, int cost) {
        this.color = color;
        this.cost = cost;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public int getCost() {
        return cost;
    }
}