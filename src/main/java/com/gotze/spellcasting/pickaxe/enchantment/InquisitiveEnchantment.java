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

public class InquisitiveEnchantment extends Enchantment implements BlockBreakListener {

    public InquisitiveEnchantment() {
        super(EnchantmentType.INQUISITIVE);
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
        COAL_ORE(Material.COAL_ORE,0, 2),
        REDSTONE_ORE(Material.REDSTONE_ORE,1, 5),
        LAPIS_ORE(Material.LAPIS_ORE,2, 5),
        NETHER_QUARTZ_ORE(Material.NETHER_QUARTZ_ORE,2, 5),
        DIAMOND_ORE(Material.DIAMOND_ORE,3, 7),
        EMERALD_ORE(Material.EMERALD_ORE,3, 7);

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