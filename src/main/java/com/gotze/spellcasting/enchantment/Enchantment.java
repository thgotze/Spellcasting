package com.gotze.spellcasting.feature.pickaxe.enchantment;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public abstract class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;

    public Enchantment(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.level = 1;
    }

    public abstract void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData);

    public EnchantmentType getEnchantmentType() {
        return enchantmentType;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return enchantmentType.getMaxLevel();
    }

    public boolean isMaxLevel() {
        return level == enchantmentType.getMaxLevel();
    }

    public void increaseLevel() {
        if (level < getMaxLevel()) {
            this.level++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enchantment that)) return false;
        return level == that.level &&
                enchantmentType == that.enchantmentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enchantmentType, level);
    }

    @Override
    public String toString() {
        return "Enchantment{" +
                "enchantmentType=" + enchantmentType +
                ", level=" + level +
                '}';
    }

    public enum EnchantmentType {
        EFFICIENCY(EfficiencyEnchantment.class, Rarity.COMMON, 5),
        UNBREAKING(UnbreakingEnchantment.class, Rarity.COMMON, 3),
        FORTUNE(FortuneEnchantment.class, Rarity.COMMON, 3),
        UNCOVER(UncoverEnchantment.class, Rarity.UNCOMMON, 5),
        MOMENTUM(MomentumEnchantment.class, Rarity.RARE, 5),
        PHANTOM_QUARRY(PhantomQuarryEnchantment.class, Rarity.EPIC, 5);

        private final Class<? extends Enchantment> enchantmentClass;
        private final Rarity rarity;
        private final int maxLevel;

        EnchantmentType(Class<? extends Enchantment> enchantmentClass, Rarity rarity, int maxLevel) {
            this.enchantmentClass = enchantmentClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
        }

        public Class<? extends Enchantment> getEnchantmentClass() {
            return enchantmentClass;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public ItemStack getUpgradeToken() {
            return switch (this) {
                case EFFICIENCY -> new ItemStackBuilder(Material.REDSTONE)
                        .name(Component.text("Efficiency Enchantment Token")
                                .color(Rarity.COMMON.getColor()))
                        .build();
                case UNBREAKING -> new ItemStackBuilder(Material.OBSIDIAN)
                        .name(Component.text("Unbreaking Enchantment Token")
                                .color(Rarity.COMMON.getColor()))
                        .build();
                case FORTUNE -> new ItemStackBuilder(Material.LAPIS_LAZULI)
                        .name(Component.text("Fortune Enchantment Token")
                                .color(Rarity.COMMON.getColor()))
                        .build();
                case UNCOVER -> new ItemStackBuilder(Material.DECORATED_POT)
                        .name(Component.text("Uncover Enchantment Token")
                                .color(Rarity.UNCOMMON.getColor()))
                        .build();
                case MOMENTUM -> new ItemStackBuilder(Material.SUGAR)
                        .name(Component.text("Momentum Enchantment Token")
                                .color(Rarity.RARE.getColor()))
                        .build();
                case PHANTOM_QUARRY -> new ItemStackBuilder(Material.TINTED_GLASS)
                        .name(Component.text("Phantom Quarry Enchantment Token")
                                .color(Rarity.EPIC.getColor()))
                        .build();
            };
        }

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static class EfficiencyEnchantment extends Enchantment {
        public EfficiencyEnchantment() {
            super(EnchantmentType.EFFICIENCY);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }

    public static class FortuneEnchantment extends Enchantment {
        public FortuneEnchantment() {
            super(EnchantmentType.FORTUNE);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }

    public static class UnbreakingEnchantment extends Enchantment {
        public UnbreakingEnchantment() {
            super(EnchantmentType.UNBREAKING);
        }

        @Override
        public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        }
    }
}