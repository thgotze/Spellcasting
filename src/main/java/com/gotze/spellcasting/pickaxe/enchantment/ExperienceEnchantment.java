package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExperienceEnchantment extends Enchantment implements BlockBreakListener {

    public ExperienceEnchantment() {
        super(EnchantmentType.EXPERIENCE);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        OreExp oreExp = OreExp.fromMaterial(block.getType());
        if (oreExp == null) return; // Not an EXP-dropping ore

        double random = ThreadLocalRandom.current().nextDouble();
        int multiplier = switch (getLevel()) {
            case 1 -> random < 0.33 ? 2 : 1;
            case 2 -> random < 0.25 ? 3 : random < 0.50 ? 2 : 1;
            case 3 -> random < 0.20 ? 4 : random < 0.40 ? 3 : random < 0.60 ? 2 : 1;
            default -> throw new IllegalStateException("Unexpected inquisitive level: " + getLevel());
        };
        if (multiplier == 1) return;

        int extraExp = oreExp.rollExp() * multiplier;

        if (extraExp > 0) {
            block.getWorld().spawn(block.getLocation().toCenterLocation(), ExperienceOrb.class, experienceOrb -> experienceOrb.setExperience(extraExp));
        }
    }

    public enum OreExp {
        NETHER_GOLD_ORE(Material.NETHER_GOLD_ORE, 0, 1),

        COAL_ORE(Material.COAL_ORE, 0, 2),
        DEEPSLATE_COAL_ORE(Material.DEEPSLATE_COAL_ORE, 0, 4),
        COAL_BLOCK(Material.COAL_BLOCK, 0, 6),

        REDSTONE_ORE(Material.REDSTONE_ORE, 1, 5),
        DEEPSLATE_REDSTONE_ORE(Material.DEEPSLATE_REDSTONE_ORE, 2, 10),
        REDSTONE_BLOCK(Material.REDSTONE_BLOCK, 3, 15),

        LAPIS_ORE(Material.LAPIS_ORE, 2, 5),
        DEEPSLATE_LAPIS_ORE(Material.DEEPSLATE_LAPIS_ORE, 4, 10),
        LAPIS_BLOCK(Material.LAPIS_BLOCK, 6, 15),

        NETHER_QUARTZ_ORE(Material.NETHER_QUARTZ_ORE, 2, 5),
        QUARTZ_BLOCK(Material.QUARTZ_BLOCK, 4, 10),

        DIAMOND_ORE(Material.DIAMOND_ORE, 3, 7),
        DEEPSLATE_DIAMOND_ORE(Material.DEEPSLATE_DIAMOND_ORE, 6, 14),
        DIAMOND_BLOCK(Material.DIAMOND_BLOCK, 9, 21),

        EMERALD_ORE(Material.EMERALD_ORE, 3, 7),
        DEEPSLATE_EMERALD_ORE(Material.DEEPSLATE_EMERALD_ORE, 6, 14),
        EMERALD_BLOCK(Material.EMERALD_BLOCK, 9, 21),
        ;

        private final Material material;
        private final int minExp;
        private final int maxExp;

        OreExp(Material material, int minExp, int maxExp) {
            this.material = material;
            this.minExp = minExp;
            this.maxExp = maxExp;
        }

        private Material getMaterial() {
            return material;
        }

        private static final Map<Material, OreExp> MATERIAL_MAP;
        static {
            MATERIAL_MAP = Stream.of(values())
                    .collect(Collectors.toMap(OreExp::getMaterial, ore -> ore));
        }

        public static OreExp fromMaterial(Material material) {
            return MATERIAL_MAP.get(material);
        }

        public int rollExp() {
            return ThreadLocalRandom.current().nextInt(minExp, maxExp + 1);
        }
    }
}