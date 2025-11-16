package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
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
//        PEEK(PeekAbility.class,
//                Rarity.COMMON,
//                5,
//                Material.SPYGLASS,
//                "Temporarily transform nearby",
//                "filler blocks into glass"),
        HAMMER(HammerAbility.class,
                Rarity.EPIC,
                1,
                Material.MACE,
                "Temporarily transform your",
                "pickaxe into a hammer, allowing",
                "you to break blocks within a 3x3 area"),
        SLICE(SliceAbility.class,
                Rarity.LEGENDARY,
                1,
                Material.IRON_SWORD,
                "Repeatedly break blocks in the",
                "direction you are facing"),
        BAZOOKA(BazookaAbility.class,
                Rarity.LEGENDARY,
                1,
                Material.FIREWORK_ROCKET,
                "Shoot an extremely destructive blast,",
                "breaking blocks in a large radius"),
        DRILL_DASH(DrillDashAbility.class,
                Rarity.LEGENDARY,
                3,
                Material.POINTED_DRIPSTONE,
                "Dash forwards and break blocks",
                "in the direction you are facing"),
        ;

        private final Class<? extends Ability> abilityClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material upgradeTokenType;
        private final String[] description;
        private final ItemStack upgradeToken;
        private final ItemStack menuItem;

        AbilityType(Class<? extends Ability> abilityClass, Rarity rarity, int maxLevel, Material upgradeTokenType, String... description) {
            this.abilityClass = abilityClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
            this.upgradeTokenType = upgradeTokenType;
            this.description = description;

            this.upgradeToken = buildUpgradeToken();
            this.menuItem = buildMenuItem();
        }

        private ItemStack buildUpgradeToken() {
            return new ItemStackBuilder(Material.PAPER)
                    .name(getUpgradeTokenName())
                    .itemModel(upgradeTokenType)
                    .enchantmentGlint(true)
                    .build();
        }

        public Component getUpgradeTokenName() {
            return getFormattedName().append(text(" Token", getRarity().getColor()));
        }

        private ItemStack buildMenuItem() {
            ItemStackBuilder menuItem = new ItemStackBuilder(upgradeToken)
                    .name(getFormattedName());

            for (String line : description) {
                menuItem.lore(text(line).color(GRAY));
            }

            menuItem.lore(text(""),
                    text(StringUtils.convertToSmallFont("requirements")),
                    text(getRequiredTokenAmount() + "x [").color(GRAY)
                            .append(getUpgradeTokenName())
                            .append(text("]")).color(GRAY));

            return menuItem.build();
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

        public ItemStack getUpgradeToken() {
            return upgradeToken.clone();
        }

        public ItemStack getMenuItem() {
            return menuItem.clone();
        }

        public Component getFormattedName() {
            return textOfChildren(text("⚡ ", RED, BOLD),
                    text(this.toString(), this.rarity.getColor()));
        }

        public int getRequiredTokenAmount() { // TODO: placeholder amounts
            return switch (rarity) {
                case COMMON -> 16;
                case UNCOMMON -> 8;
                case RARE -> 4;
                case EPIC -> 2;
                case LEGENDARY -> 1;
            };
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}