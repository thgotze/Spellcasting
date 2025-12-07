package com.gotze.spellcasting.pickaxe.enchantment;

import com.destroystokyo.paper.ParticleBuilder;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener {
    private static final ParticleBuilder ENRICHEN_PARTICLE = Particle.DUST_COLOR_TRANSITION.builder()
            .count(1)
            .colorTransition(Color.FUCHSIA, Color.WHITE);
    private static final EnumMap<Material, Material> applicableOreTypes = new EnumMap<>(Material.class);

    static {
        applicableOreTypes.put(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_COPPER_ORE, Material.RAW_COPPER_BLOCK);
        applicableOreTypes.put(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_IRON_ORE, Material.RAW_IRON_BLOCK);
        applicableOreTypes.put(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_GOLD_ORE, Material.RAW_GOLD_BLOCK);
        applicableOreTypes.put(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);
    }

    public EnrichenEnchantment() {
        super(EnchantmentType.ENRICHEN);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (ThreadLocalRandom.current().nextDouble() > (0.02 + getLevel() * 0.02)) return; // 4%, 6%, 8%, 10%, 12%

        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
        candidateBlocks.removeIf(candidate -> !applicableOreTypes.containsKey(candidate.getType()));
        if (candidateBlocks.isEmpty()) return;

        for (Block candidateBlock : candidateBlocks) {
            Location candidateBlockLocation = candidateBlock.getLocation();

            candidateBlock.setType(applicableOreTypes.get(candidateBlock.getType()));

            player.playSound(candidateBlockLocation, Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.75f, 1f);

            for (Location blockOutlinePoint : BlockUtils.getBlockOutlineForParticles(candidateBlockLocation, 0.10)) {
                ENRICHEN_PARTICLE.clone()
                        .location(blockOutlinePoint)
                        .spawn();
            }
        }
    }
}