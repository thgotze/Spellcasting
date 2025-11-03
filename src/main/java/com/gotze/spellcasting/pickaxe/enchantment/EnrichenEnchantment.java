package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener {

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
        // 5% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;
        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
        candidateBlocks.removeIf(candidate -> !applicableOreTypes.containsKey(candidate.getType()) ||
                candidate.equals(block));

        if (candidateBlocks.isEmpty()) return;

        for (Block candidateBlock : candidateBlocks) {
            candidateBlock.setType(applicableOreTypes.get(candidateBlock.getType()));
        }
        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));
    }
}