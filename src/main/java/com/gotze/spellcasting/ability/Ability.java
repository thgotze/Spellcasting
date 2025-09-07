package com.gotze.spellcasting.ability;

import java.util.Objects;

public class Ability {
    private final AbilityType abilityType;
    private int level;

    public Ability(AbilityType abilityType) {
        this.abilityType = abilityType;
        this.level = 1;
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return abilityType.getName();
    }

    public int getMaxLevel() {
        return abilityType.getMaxLevel();
    }

    public boolean isMaxLevel() {
        return level == abilityType.getMaxLevel();
    }

    public void increaseLevel() {
        if (level < getMaxLevel()) {
            this.level++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ability that)) return false;
        return level == that.level &&
                abilityType == that.abilityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(abilityType, level);
    }

    @Override
    public String toString() {
        return "Ability{" +
                "abilityType=" + abilityType +
                ", level=" + level +
                '}';
    }

    public enum AbilityType {
        SLICE("Slice", 5),
        LASER("Laser", 3),
        ROCKET_LAUNCHER("Rocket Launcher", 3);

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
}