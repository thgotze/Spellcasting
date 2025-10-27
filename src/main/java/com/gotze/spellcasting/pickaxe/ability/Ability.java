package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public abstract class Ability {
    private final AbilityType abilityType;
    private int level;

    public Ability(AbilityType abilityType) {
        this.abilityType = abilityType;
        this.level = 1;
    }

    public abstract void activateAbility(Player player, PickaxeData pickaxeData);

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

    public void setLevel(int level) {
        this.level = level;
    }

    public enum AbilityType {
        PEEK(PeekAbility.class, Rarity.COMMON, 5, Material.SPYGLASS),
        HAMMER(HammerAbility.class, Rarity.EPIC, 1, Material.MACE),
        SLICE(SliceAbility.class, Rarity.LEGENDARY, 1, Material.IRON_SWORD),
        BAZOOKA(BazookaAbility.class, Rarity.LEGENDARY, 1, Material.FIREWORK_ROCKET),
        ;

        private final Class<? extends Ability> abilityClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material upgradeTokenType;

        AbilityType(Class<? extends Ability> abilityClass, Rarity rarity, int maxLevel, Material upgradeTokenType) {
            this.abilityClass = abilityClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
            this.upgradeTokenType = upgradeTokenType;
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

        public Material getUpgradeTokenType() {
            return upgradeTokenType;
        }

        public Component getFormattedName() {
            return textOfChildren(text("⚡ ", RED, BOLD),
                    text(this.toString(), this.rarity.getColor()));
        }

        public ItemStack getUpgradeToken() {
            return new ItemStackBuilder(Material.PAPER)
                    .name(upgradeTokenName())
                    .itemModel(upgradeTokenType)
                    .build();
        }

        public Component upgradeTokenName() {
            return getFormattedName()
                    .append(text(" Token", this.getRarity().getColor()));
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}