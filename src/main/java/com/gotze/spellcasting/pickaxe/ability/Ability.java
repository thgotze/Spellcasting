package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public abstract class Ability {
    private final AbilityType abilityType;
    private int level;
    private int energy;

    public Ability(AbilityType abilityType) {
        this.abilityType = abilityType;
        this.level = 1;
        this.energy = 0;
    }

    public abstract void activateAbility(Player player, PickaxeData pickaxeData);

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public int getLevel() {
        return level;
    }

    public Rarity getRarity() {
        return abilityType.getRarity();
    }

    public boolean isMaxLevel() {
        return level == abilityType.getMaxLevel();
    }

    public void increaseLevel() {
        if (level < abilityType.getMaxLevel()) {
            this.level++;
        }
    }

    public void setLevel(int level) {
        if (level < 0) return;
        this.level = level;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean canActivateAbility() {
        return energy == abilityType.getRequiredEnergy();
    }

    public void setEnergy(int energy) {
        if (energy < 0) return;
        this.energy = Math.min(abilityType.getRequiredEnergy(), energy);
    }

    public void addEnergy(int energy) {
        if (energy < 0) return;
        this.energy += energy;
        this.energy = Math.min(abilityType.getRequiredEnergy(), this.energy);
    }

    public enum AbilityType {
        HAMMER(HammerAbility.class,
                Rarity.EPIC,
                1,
                Material.MACE,
                200,
                "Temporarily transform your",
                "pickaxe into a hammer, allowing",
                "you to break blocks within a 3x3 area"),
//        SLICE(SliceAbility.class,
//                Rarity.LEGENDARY,
//                1,
//                Material.IRON_SWORD,
//                "Repeatedly break blocks in the",
//                "direction you are facing"),
        BAZOOKA(BazookaAbility.class,
                Rarity.LEGENDARY,
                1,
                Material.FIREWORK_ROCKET,
                500,
                "Shoot an extremely destructive blast,",
                "breaking blocks in a large radius"),
        DRILL_DASH(DrillDashAbility.class,
                Rarity.LEGENDARY,
                5,
                Material.HOPPER,
                500,
                "Dash forwards and break blocks",
                "in the direction you are facing"),
//        SLAM(SlamAbility.class,
//                Rarity.RARE,
//                5,
//                Material.NETHERITE_BOOTS,
//                "Jump up and slam the ground",
//                "breaking many blocks on impact"),
//        LASER(LaserAbility.class,
//                Rarity.LEGENDARY,
//                1,
//                Material.AMETHYST_SHARD,
//                "Shoot a laser beam that breaks blocks",
//                "in its path"),
        TRIDENT_THROW(TridentThrowAbility.class,
                Rarity.RARE,
                1,
                Material.TRIDENT,
                300,
                "Throw a trident that",
                "breaks blocks in its path"),
        WIND_BURST(WindBurstAbility.class,
                Rarity.COMMON,
                5,
                Material.WIND_CHARGE,
                100,
                "Shoot wind charges that",
                "turn filler blocks into air... Poof!"),
        ;

        private final Class<? extends Ability> abilityClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material upgradeTokenType;
        private final int requiredEnergy;
        private final String[] description;

        private final ItemStack upgradeToken;
        private final ItemStack menuItem;

        AbilityType(Class<? extends Ability> abilityClass, Rarity rarity, int maxLevel,
                    Material upgradeTokenType, int requiredEnergy, String... description) {
            this.abilityClass = abilityClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
            this.upgradeTokenType = upgradeTokenType;
            this.requiredEnergy = requiredEnergy;
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
                menuItem.lore(text(line, GRAY));
            }

            menuItem.lore(text(""),
                    text(StringUtils.convertToSmallFont("requirements")),
                    text(getRequiredTokenAmount() + "x [", GRAY)
                            .append(getUpgradeTokenName())
                            .append(text("]", GRAY)));

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

        public int getRequiredEnergy() {
            return requiredEnergy;
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
                case COMMON -> 1;
                case UNCOMMON -> 1;
                case RARE -> 1;
                case EPIC -> 1;
                case LEGENDARY -> 1;
            };
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}
