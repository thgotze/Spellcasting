package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.Rarity;
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
        SLICE(1, Rarity.LEGENDARY, SliceAbility.class),
        BAZOOKA(1, Rarity.LEGENDARY, BazookaAbility.class),
        LASER(1, Rarity.LEGENDARY, LaserAbility.class),
        HAMMER(1, Rarity.LEGENDARY, HammerAbility.class);

        private final int maxLevel;
        private final Rarity rarity;
        private final Class<? extends Ability> abilityClass;

        AbilityType(int maxLevel, Rarity rarity, Class<? extends Ability> abilityClass) {
            this.maxLevel = maxLevel;
            this.rarity = rarity;
            this.abilityClass = abilityClass;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public Class<? extends Ability> getAbilityClass() {
            return abilityClass;
        }

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}