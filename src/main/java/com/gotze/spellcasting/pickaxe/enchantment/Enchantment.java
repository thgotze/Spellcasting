package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Rarity;
import com.gotze.spellcasting.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

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
        EFFICIENCY(EfficiencyEnchantment.class,
                Rarity.COMMON,
                5,
                Material.REDSTONE,
                "Increase mining efficiency"),
        FORTUNE(FortuneEnchantment.class,
                Rarity.COMMON,
                3,
                Material.LAPIS_LAZULI,
                "Receive more ore drops"),
        UNBREAKING(UnbreakingEnchantment.class,
                Rarity.COMMON,
                3,
                Material.OBSIDIAN,
                "Reduce durability loss"),
        GLACIATE(GlaciateEnchantment.class,
                Rarity.RARE,
                1,
                Material.PACKED_ICE,
                "Freeze nearby blocks"),
//        "Chance to freeze nearby blocks",
        //                "making them quicker to mine"),
        PHANTOM_QUARRY(PhantomQuarryEnchantment.class,
                Rarity.EPIC,
                5,
                Material.TINTED_GLASS,
                "Quarry a 5x5 area after",
                "breaking marked corner blocks"),
//                "Chance to mark 3-4 corners of",
//                "a 5x5 square centering from",
//                "the broken block. Breaking all",
//                "the marked corners quarries",
//                "all blocks within the 5x5 square"),
        MITOSIS(MitosisEnchantment.class,
                Rarity.UNCOMMON,
                5,
                Material.BONE_MEAL,
        "Chance to spawn more",
        "ores around the mined ore"),
        ENRICHEN(EnrichenEnchantment.class,
                Rarity.UNCOMMON,
                5,
                Material.GOLD_INGOT,
                "Chance to increase",
                        "ore block quality"),
        MINERS_SENSE(MinersSenseEnchantment.class,
                Rarity.RARE,
                5,
                Material.ENDER_EYE,
                "Periodically mark an ore",
                "block. Marked ores break",
                "instantly")
        ;

        private final Class<? extends Enchantment> enchantmentClass;
        private final Rarity rarity;
        private final int maxLevel;
        private final Material upgradeTokenType;
        private final String[] description;

        private final ItemStack upgradeToken;
        private final ItemStack menuItem;

        EnchantmentType(Class<? extends Enchantment> enchantmentClass, Rarity rarity, int maxLevel, Material upgradeTokenType, String... description) {
            this.enchantmentClass = enchantmentClass;
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
                    .toggleEnchantmentGlint()
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
                    text(getTokenAmount() + "x [").color(GRAY)
                            .append(getUpgradeTokenName())
                            .append(text("]")).color(GRAY));

            return menuItem.build();
        }

        public ItemStack getMenuItem() {
            return menuItem.clone();
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
            return upgradeToken.clone();
        }

        public Material getUpgradeTokenType() {
            return upgradeTokenType;
        }

        public List<String> getDescription() {
            return List.of(description);
        }

        public Component getFormattedName() {
            return text(this.toString(), this.getRarity().getColor());
        }

        public int getTokenAmount() { // TODO: placeholder amounts
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