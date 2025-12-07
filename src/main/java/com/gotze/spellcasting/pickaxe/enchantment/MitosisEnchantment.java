package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MitosisEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener {

    private BlockFace blockFace;

    public MitosisEnchantment() {
        super(EnchantmentType.MITOSIS);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        this.blockFace = event.getBlockFace();
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (!BlockCategories.ORE_BLOCKS.containsKey(block.getType())) return;
        if (ThreadLocalRandom.current().nextDouble() > (0.02 + getLevel() * 0.02)) return; // 4%, 6%, 8%, 10%, 12%

        List<Block> candidateBlocks = switch (blockFace) {
            case NORTH, SOUTH -> BlockUtils.getBlocksInSquarePattern(block, 3, 3, 1);
            case EAST, WEST -> BlockUtils.getBlocksInSquarePattern(block, 1, 3, 3);
            case UP, DOWN -> BlockUtils.getBlocksInSquarePattern(block, 3, 1, 3);
            default -> null;
        };
        if (candidateBlocks == null) return;
        candidateBlocks.removeIf(candidate -> !BlockCategories.FILLER_BLOCKS.contains(candidate.getType()));
        if (candidateBlocks.isEmpty()) return;

        Block chosenBlock1 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
        chosenBlock1.setType(block.getType());
        player.playSound(chosenBlock1.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1f, 1f);
        player.playEffect(chosenBlock1.getLocation(), Effect.STEP_SOUND, block.getType());
        for (Location blockOutlineForParticle : BlockUtils.getBlockOutlineForParticles(chosenBlock1.getLocation(), 0.10)) {
//            player.spawnParticle(Particle.BLOCK, blockOutlineForParticle, 0, 0, 0, 0, 0, block.getBlockData());
            player.spawnParticle(Particle.TOTEM_OF_UNDYING, blockOutlineForParticle, 0, 0, 0, 0, 0);
        }
        if (candidateBlocks.isEmpty()) return;

        Block chosenBlock2 = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
        chosenBlock2.setType(block.getType());
        player.playSound(chosenBlock2.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1f, 1f);
        player.playEffect(chosenBlock2.getLocation(), Effect.STEP_SOUND, block.getType());
        for (Location blockOutlineForParticle : BlockUtils.getBlockOutlineForParticles(chosenBlock2.getLocation(), 0.10)) {
            player.spawnParticle(Particle.TOTEM_OF_UNDYING, blockOutlineForParticle, 0, 0, 0, 0, 0);
        }
    }
}