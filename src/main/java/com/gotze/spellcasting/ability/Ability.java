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

public abstract class Ability {
    private final AbilityType abilityType;
    private int level;

    public Ability(AbilityType abilityType) {
        this.abilityType = abilityType;
        this.level = 1;
    }

    public abstract void activateAbility(Player player, PickaxeData pickaxeData);

    public AbilityType abilityType() {
        return abilityType;
    }

    public int level() {
        return level;
    }

    public int maxLevel() {
        return abilityType.maxLevel();
    }

    public Rarity rarity() {
        return abilityType.rarity();
    }

    public boolean isMaxLevel() {
        return level == abilityType.maxLevel();
    }

    public void increaseLevel() {
        if (level < maxLevel()) {
            this.level++;
        }
    }

    public enum AbilityType {
        PEEK(PeekAbility.class, Rarity.COMMON, 5),
//        CONGLOMERATE(ConglomerateAbility.class, Rarity.RARE, 5),
        HAMMER(HammerAbility.class, Rarity.EPIC, 5),
        FLURRY(FlurryAbility.class, Rarity.LEGENDARY, 5),
        BAZOOKA(BazookaAbility.class, Rarity.LEGENDARY, 5),
//        LASER(LaserAbility.class, Rarity.LEGENDARY, 5),
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

        public Rarity rarity() {
            return rarity;
        }

        public int maxLevel() {
            return maxLevel;
        }

        public Component formattedName() {
            return Component.text()
                    .append(Component.text("⚡ ")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(this.toString())
                            .color(this.rarity().color()))
                    .build();
        }

        public ItemStack upgradeToken() {
            Material material = switch (this) {
                case PEEK -> Material.SPYGLASS;
//                case CONGLOMERATE -> Material.DIORITE;
                case HAMMER -> Material.MACE;
                case FLURRY -> Material.IRON_SWORD;
                case BAZOOKA -> Material.FIREWORK_ROCKET;
//                case LASER -> Material.AMETHYST_SHARD;
            };

            return new ItemStackBuilder(Material.PAPER)
                    .name(upgradeTokenName())
                    .itemModel(NamespacedKey.minecraft(material.name().toLowerCase()))
                    .build();
        }

        public Component upgradeTokenName() {
            return formattedName()
                    .append(Component.text(" Token")
                            .color(this.rarity().color()));
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}