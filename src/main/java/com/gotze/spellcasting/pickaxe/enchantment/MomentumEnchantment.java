package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;

public class MomentumEnchantment extends Enchantment implements BlockBreakListener, BlockBreaker {
    private static final BlockData TINTED_GLASS = Material.TINTED_GLASS.createBlockData();

    private Block markedBlock;
    private BlockDisplay markedBlockDisplay;

    public MomentumEnchantment() {
        super(EnchantmentType.MOMENTUM);

    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        // Create the block display once
        if (markedBlockDisplay == null) {
            markedBlockDisplay = (BlockDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.BLOCK_DISPLAY);
            markedBlockDisplay.setTransformationMatrix(new Matrix4f().scale(0.9921875f, 0.9921875f, 0.9921875f));
            markedBlockDisplay.setBlock(TINTED_GLASS);
            markedBlockDisplay.setGlowing(true);
            markedBlockDisplay.setGlowColorOverride(Color.YELLOW);
            markedBlockDisplay.setVisibleByDefault(false);
        }

        // ---------------
        // First time
        // ---------------
        if (markedBlock == null) {
            if (isNaturalBreak) {
                markNewNearbyOreBlock(player, block);
            }
            return;
        }

        // ---------------
        // Subsequent times
        // ---------------
        if (markedBlock.equals(block)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, 0, false, false)); // Haste I for 3 seconds
            markNewNearbyOreBlock(player, markedBlock);
        }
    }

    private void markNewNearbyOreBlock(Player player, Block origin) {
        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(origin, 3, 3, 3);
        candidateBlocks.removeIf(candidate -> !BlockCategories.ORE_BLOCKS.containsKey(candidate.getType()) ||
                candidate.equals(origin));

        if (candidateBlocks.isEmpty()) {
            markedBlock = null;
            player.hideEntity(Spellcasting.getPlugin(), markedBlockDisplay);
        } else {
            markedBlock = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
            markedBlockDisplay.teleport(markedBlock.getLocation().add(0.001953125f, 0.001953125f, 0.001953125f));
            player.showEntity(Spellcasting.getPlugin(), markedBlockDisplay);

            player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));
        }
    }
}