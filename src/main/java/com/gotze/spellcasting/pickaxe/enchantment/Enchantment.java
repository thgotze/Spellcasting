package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Rarity;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;

    public Enchantment(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.level = 1;
    }

    public EnchantmentType getEnchantmentType() {
        return enchantmentType;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return enchantmentType.getName();
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
        EFFICIENCY("Efficiency", 5, Rarity.BASIC) {
            @Override
            public void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level) {
                // No custom logic needed
            }
        },
        UNBREAKING("Unbreaking", 3, Rarity.BASIC) {
            @Override
            public void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level) {
                // No custom logic needed
            }
        },
        FORTUNE("Fortune", 3, Rarity.BASIC) {
            @Override
            public void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level) {
                // No custom logic needed
            }
        },
        HASTE_AND_SPEED("Haste And Speed", 5, Rarity.UNIQUE) {
            @Override
            public void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level) {
                double chance = level * 0.05;

                if (Math.random() < chance) {
                    int amplifier = level - 1;
                    if (Math.random() < 0.5) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * level, amplifier));
                    } else {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * level, amplifier));
                    }
                }
            }
        },
        MINE_BLOCK_ABOVE("Mine Block Above", 5, Rarity.UNIQUE) {
            @Override
            public void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level) {
                double chance = level * 0.05;

                if (Math.random() < chance) {
                    Block blockAbove = event.getBlock().getRelative(BlockFace.UP);
                    if (!blockAbove.getType().isAir()) {
                        blockAbove.breakNaturally(true);
                        pickaxeData.addBlocksBroken(1);
                    }
                }
            }
        };

        private final String name;
        private final int maxLevel;
        private final Rarity rarity;

        EnchantmentType(String name, int maxLevel, Rarity rarity) {
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

        public abstract void apply(Player player, BlockBreakEvent event, PickaxeData pickaxeData, int level); // TODO: logic should later NOT be coupled to enum
    }
}