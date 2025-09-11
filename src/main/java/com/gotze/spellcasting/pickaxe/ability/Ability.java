package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Rarity;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class Ability {
    private final AbilityType abilityType;
    private int level;

    public Ability(AbilityType abilityType) {
        this.abilityType = abilityType;
        this.level = 1;
    }

    public abstract void activate(Player player, PickaxeData pickaxeData);

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

    public Rarity getRarity() {
        return abilityType.getRarity();
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
        SLICE("Slice", 1, Rarity.LEGENDARY),
        BAZOOKA("Bazooka", 1, Rarity.LEGENDARY),
        LASER("Laser", 1, Rarity.LEGENDARY),
        HAMMER("Hammer", 1, Rarity.LEGENDARY);

        private final String name;
        private final int maxLevel;
        private final Rarity rarity;

        AbilityType(String name, int maxLevel, Rarity rarity) {
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
}