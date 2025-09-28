package com.gotze.spellcasting.feature.pickaxe.ability;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        SLICE(SliceAbility.class, Rarity.LEGENDARY, 1),
        BAZOOKA(BazookaAbility.class, Rarity.LEGENDARY, 1),
        LASER(LaserAbility.class, Rarity.LEGENDARY, 1),
        HAMMER(HammerAbility.class, Rarity.LEGENDARY, 1),
        ;

        private final Class<? extends Ability> abilityClass;
        private final Rarity rarity;
        private final int maxLevel;

        AbilityType(Class<? extends Ability> abilityClass, Rarity rarity, int maxLevel) {
            this.abilityClass = abilityClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
        }

        public Class<? extends Ability> getAbilityClass() {
            return abilityClass;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public Component getColoredName() {
            return Component.text()
                    .append(Component.text("⚡ ")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(this.toString())
                            .color(this.getRarity().getColor()))
                    .build();
        }

        public ItemStack getUpgradeToken() {
            return switch (this) {
                case SLICE -> new ItemStackBuilder(Material.IRON_SWORD)
                        .name(Component.text("Slice Ability Token")
                                .color(Rarity.LEGENDARY.getColor()))
                        .build();
                case BAZOOKA -> new ItemStackBuilder(Material.FIREWORK_ROCKET)
                        .name(Component.text("Bazooka Ability Token")
                                .color(Rarity.LEGENDARY.getColor()))
                        .build();
                case LASER -> new ItemStackBuilder(Material.LIGHTNING_ROD)
                        .name(Component.text("Laser Ability Token")
                                .color(Rarity.LEGENDARY.getColor()))
                        .build();
                case HAMMER -> new ItemStackBuilder(Material.MACE)
                        .name(Component.text("Hammer Ability Token")
                                .color(Rarity.LEGENDARY.getColor()))
                        .build();
            };
            Material material = switch (this) {
                case PEEK -> Material.GLASS;
                case SLICE -> Material.IRON_SWORD;
                case BAZOOKA -> Material.FIREWORK_ROCKET;
                case LASER -> Material.LIGHTNING_ROD;
                case HAMMER -> Material.MACE;
            };

            return new ItemStackBuilder(Material.PAPER)
                    .name(getUpgradeTokenName())
                    .itemModel(NamespacedKey.minecraft(material.name().toLowerCase()))
                    .build();
        }
        }

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}