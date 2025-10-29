package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MinersSenseEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker {
    private static final BlockData TINTED_GLASS = Material.TINTED_GLASS.createBlockData();

    private Block markedBlock;
    private BlockDisplay markedBlockDisplay;

    public MinersSenseEnchantment() {
        super(EnchantmentType.MINERS_SENSE);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (markedBlock != null) return;

        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(block, 5, 5, 5);
        candidateBlocks.removeIf(candidate -> !BlockCategories.ORE_BLOCKS.containsKey(candidate.getType()) ||
                candidate.equals(block));

        if (candidateBlocks.isEmpty()) return;
        markedBlock = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));

        Location displayLocation = markedBlock.getLocation().add(0.001953125f, 0.001953125f, 0.001953125f);

        markedBlockDisplay = (BlockDisplay) markedBlock.getWorld().spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
        markedBlockDisplay.setTransformationMatrix(new Matrix4f().scale(0.9921875f, 0.9921875f, 0.9921875f));
        markedBlockDisplay.setBlock(TINTED_GLASS);
        markedBlockDisplay.setGlowing(true);
        markedBlockDisplay.setGlowColorOverride(Color.ORANGE);
        markedBlockDisplay.setVisibleByDefault(false);
        player.showEntity(JavaPlugin.getPlugin(Spellcasting.class), markedBlockDisplay);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (markedBlock == null) return;

        if (event.getBlock().equals(markedBlock)) {
            breakBlock(player, markedBlock, pickaxeData, false);
            markedBlock = null;
            markedBlockDisplay.remove();
            markedBlockDisplay = null;
        }
    }
}