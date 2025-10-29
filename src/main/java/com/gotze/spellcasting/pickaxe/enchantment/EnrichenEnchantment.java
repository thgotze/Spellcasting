package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener {

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

    private boolean isActive;

    public EnrichenEnchantment() {
        super(EnchantmentType.ENRICHEN);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (this.isActive) return;
        if (!isNaturalBreak) return;
        if (!BlockCategories.ORE_BLOCKS.containsKey(block.getType())) return;
        // 5% activation chance
        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;
        this.isActive = true;
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (!this.isActive) return;

        Block block = event.getBlock();
        Material blockType = block.getType();
        if (!applicableOreTypes.containsKey(blockType)) return;

        block.setType(applicableOreTypes.get(blockType));
        this.isActive = false;
        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));
    }
}