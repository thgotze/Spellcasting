package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;
    private boolean killSwitch;

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
        FORTUNE(FortuneEnchantment.class, Rarity.COMMON, 3),
        UNBREAKING(UnbreakingEnchantment.class, Rarity.COMMON, 3),
        //        UNCOVER(UncoverEnchantment.class, Rarity.UNCOMMON, 5),
//        MOMENTUM(MomentumEnchantment.class, Rarity.RARE, 5),
        PHANTOM_QUARRY(PhantomQuarryEnchantment.class, Rarity.EPIC, 5),
        SPREAD(SpreadEnchantment.class, Rarity.UNCOMMON, 5),
        ;
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
            Material material = switch (this) {
                case EFFICIENCY -> Material.REDSTONE;
                case FORTUNE -> Material.LAPIS_LAZULI;
                case UNBREAKING -> Material.OBSIDIAN;
//                case UNCOVER -> Material.DECORATED_POT;
//                case MOMENTUM -> Material.SUGAR;
                case PHANTOM_QUARRY -> Material.TINTED_GLASS;
                case SPREAD -> Material.BONE_MEAL;
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

        public Component getColoredName() {
            return Component.text()
                    .append(Component.text(this.toString())
                            .color(this.getRarity().getColor()))
                    .build();
        }

        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }

    public static class EfficiencyEnchantment extends Enchantment {
        public EfficiencyEnchantment() {
            super(EnchantmentType.EFFICIENCY);
        }
    }

    public static class FortuneEnchantment extends Enchantment implements BlockBreakAware {
        public FortuneEnchantment() {
            super(EnchantmentType.FORTUNE);
        }

        @Override
        public void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
            handleBlockBreak(event, pickaxeData, event.getBlock());
        }

        public static void handleBlockBreak(BlockBreakEvent event, PickaxeData pickaxeData, Block... blocks) {
//
//            int fortuneEnchantmentLevel = 0;
//            if (pickaxeData.hasEnchantment(EnchantmentType.FORTUNE)) {
//                fortuneEnchantmentLevel = pickaxeData.getEnchantment(EnchantmentType.FORTUNE).getLevel();
//            }
//
//            for (Block block : blocks) {
//                Loot loot = BlockCategories.ORE_BLOCKS.get(block.getType());
//                if (loot == null) {
//                    block.breakNaturally(true);
//                    return;
//                }
//
//                int multiplier = 1;
//                var rolledChance = loot.rollChance();
//                if (rolledChance.isEmpty()) return;
//
//                ItemStack itemStack = rolledChance.get();
//
//                if (fortuneEnchantmentLevel > 0) {
//                    double random = ThreadLocalRandom.current().nextDouble();
//                    if (fortuneEnchantmentLevel == 1) {
//                        if (random < 0.33) {
//                            multiplier = 2;
//                        }
//
//                    } else if (fortuneEnchantmentLevel == 2) {
//                        if (random < 0.25) {
//                            multiplier = 3;
//
//                        } else if (random < 0.50) multiplier = 2;
//
//                    } else if (fortuneEnchantmentLevel == 3) {
//                        if (random < 0.20) {
//                            multiplier = 4;
//
//                        } else if (random < 0.40) {
//                            multiplier = 3;
//
//                        } else if (random < 0.60) {
//                            multiplier = 2;
//                        }
//                    }
//
//                    int amount = itemStack.getAmount();
//                    int finalAmount = amount * multiplier;
//                    itemStack.setAmount(finalAmount);
//
//                    event.getPlayer().sendMessage("Received " + amount + " x " + multiplier + " = " + finalAmount + " " + StringUtils.toTitleCase(itemStack.getType().toString()));
//                }
//
//                // actually break the block
//                block.setType(Material.AIR);
//
//                event.getPlayer().sendMessage("Hi");
//                // disable vanilla ore drops
//                event.setDropItems(false);
//
//                // drop custom ore drop
//                block.getWorld().dropItem(block.getLocation().toCenterLocation(), itemStack);
            }
        }

//            BlockCategories.ORE_BLOCKS.get(blockType).rollChance().ifPresent(itemStack ->
//                    block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), itemStack)
//            );

        // level 1
        // 66% 1×
        //33% 2×
        //(avg. 1.33× - +33%)

        // level 2
        //50% 1×
        //25% 2×
        //25% 3×
        //(avg. 1.75× - +75%)

        // level 3
        //40% 1×
        //20% 2×
        //20% 3×
        //20% 4×
        //(avg. 2.2× - +120%)
    }

    public static class UnbreakingEnchantment extends Enchantment {
        public UnbreakingEnchantment() {
            super(EnchantmentType.UNBREAKING);
        }
    }
}