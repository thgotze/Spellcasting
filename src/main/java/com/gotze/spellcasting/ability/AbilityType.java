package com.gotze.spellcasting.ability;

public enum AbilityType {
    SLICE("Slice", 5),
    LASER("Laser", 3),
    ROCKET_LAUNCHER("Rocket Launcher", 3),
    ABILITY("ABILITY", 7);

    private final String name;
    private final int maxLevel;

    AbilityType(String name, int maxLevel) {
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
