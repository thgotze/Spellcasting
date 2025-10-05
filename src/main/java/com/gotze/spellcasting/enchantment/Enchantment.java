package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;
//    private boolean killSwitch; // TODO: add killswitch

    public Enchantment(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        this.level = 1;
    }

    public EnchantmentType enchantmentType() {
        return enchantmentType;
    }

    public int level() {
        return level;
    }

    public int maxLevel() {
        return enchantmentType.maxLevel();
    }

    public boolean isMaxLevel() {
        return level == enchantmentType.maxLevel();
    }

    public void increaseLevel() {
        if (level < maxLevel()) {
            this.level++;
        }
    }

    public enum EnchantmentType {
        EFFICIENCY(EfficiencyEnchantment.class, Rarity.COMMON, 5),
        FORTUNE(FortuneEnchantment.class, Rarity.COMMON, 3),
        UNBREAKING(UnbreakingEnchantment.class, Rarity.COMMON, 3),
//        UNCOVER(UncoverEnchantment.class, Rarity.UNCOMMON, 5),
//        MOMENTUM(MomentumEnchantment.class, Rarity.RARE, 5),
        PHANTOM_QUARRY(PhantomQuarryEnchantment.class, Rarity.EPIC, 5),
        SCATTER(ScatterEnchantment.class, Rarity.UNCOMMON, 5),
        ;
        private final Class<? extends Enchantment> enchantmentClass;
        private final Rarity rarity;
        private final int maxLevel;

        EnchantmentType(Class<? extends Enchantment> enchantmentClass, Rarity rarity, int maxLevel) {
            this.enchantmentClass = enchantmentClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
        }

        public Class<? extends Enchantment> enchantmentClass() {
            return enchantmentClass;
        }

        public Rarity rarity() {
            return rarity;
        }

        public int maxLevel() {
            return maxLevel;
        }

        public Component formattedName() {
            return Component.text()
                    .append(Component.text(this.toString())
                            .color(this.rarity().color()))
                    .build();
        }

        public ItemStack upgradeToken() {
            Material material = switch (this) {
                case EFFICIENCY -> Material.REDSTONE;
                case FORTUNE -> Material.LAPIS_LAZULI;
                case UNBREAKING -> Material.OBSIDIAN;
//                case UNCOVER -> Material.DECORATED_POT;
//                case MOMENTUM -> Material.SUGAR;
                case PHANTOM_QUARRY -> Material.TINTED_GLASS;
                case SCATTER -> Material.BONE_MEAL;
            };

            return new ItemStackBuilder(Material.PAPER)
                    .name(upgradeTokenName())
                    .itemModel(NamespacedKey.minecraft(material.toString().toLowerCase()))
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
