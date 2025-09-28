package com.gotze.spellcasting.ability;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        PEEK(PeekAbility.class, Rarity.COMMON, 1),
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

        public Component getUpgradeTokenName() {
            return getColoredName()
                    .append(Component.text(" Token")
                            .color(this.getRarity().getColor()));
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}