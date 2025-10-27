package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public abstract class Enchantment {
    private final EnchantmentType enchantmentType;
    private int level;
//    private boolean killSwitch; // TODO: add killswitch

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

    public void setLevel(int level) {
        this.level = level;
    }

    public enum EnchantmentType {
        EFFICIENCY(EfficiencyEnchantment.class, Rarity.COMMON, 5, Material.REDSTONE),
        FORTUNE(FortuneEnchantment.class, Rarity.COMMON, 3, Material.LAPIS_LAZULI),
        UNBREAKING(UnbreakingEnchantment.class, Rarity.COMMON, 3, Material.OBSIDIAN),
        GLACIATE(GlaciateEnchantment.class, Rarity.RARE, 1, Material.PACKED_ICE),
        SCATTER(ScatterEnchantment.class, Rarity.UNCOMMON, 5, Material.BONE_MEAL),
        PHANTOM_QUARRY(PhantomQuarryEnchantment.class, Rarity.EPIC, 5, Material.TINTED_GLASS),
        ;
        private final Class<? extends Enchantment> enchantmentClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material upgradeTokenType;

        EnchantmentType(Class<? extends Enchantment> enchantmentClass, Rarity rarity, int maxLevel, Material upgradeTokenType) {
            this.enchantmentClass = enchantmentClass;
            this.rarity = rarity;
            this.maxLevel = maxLevel;
            this.upgradeTokenType = upgradeTokenType;
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

        public Material getUpgradeTokenType() {
            return upgradeTokenType;
        }

        public ItemStack getUpgradeToken() {
            return new ItemStackBuilder(Material.PAPER)
                    .name(getUpgradeTokenName())
                    .itemModel(upgradeTokenType)
                    .build();
        }

        public Component getFormattedName() {
            return text(this.toString(), this.getRarity().getColor());
        }

        public Component getUpgradeTokenName() {
            return getFormattedName()
                    .append(text(" Token", this.getRarity().getColor()));
        }

        @Override
        public String toString() {
            return StringUtils.toTitleCase(name());
        }
    }
}