package com.gotze.spellcasting.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockCategories;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeekAbility extends Ability {

    private boolean isActive;
    private final Map<Block, BlockData> blocksAffected = new HashMap<>();

    public PeekAbility() {
        super(AbilityType.PEEK);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;

        Block centerBlock = player.getLocation().getBlock().getRelative(BlockFace.UP);

        List<Block> blockList = switch (getLevel() + 4) {
            case 1 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 4, 5);
            case 2 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 7, 6, 7);
            case 3 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 9, 8, 9);
            case 4 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 11, 10, 11);
            case 5 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 13, 12, 13);
            default -> throw new IllegalStateException("Unexpected value: " + getLevel());
        };

        blockList.removeIf(block -> block.isEmpty() || !BlockCategories.FILLER_BLOCKS.contains(block.getType()));

        for (Block block : blockList) {
            blocksAffected.put(block, block.getBlockData());
            block.setType(Material.GLASS);
        }

        player.playSound(player, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.75f, 1.0f);
        player.spawnParticle(Particle.WAX_OFF, centerBlock.getLocation().toCenterLocation(), 100, 3, 3,3, 20);
//        player.spawnParticle(Particle.WITCH, centerBlock.getLocation().toCenterLocation(), 1000, 2, 2,2, 0);
//        player.spawnParticle(Particle.DRAGON_BREATH, centerBlock.getLocation().toCenterLocation(), 1000, 2, 2,2, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Block, BlockData> entry : blocksAffected.entrySet()) {
                    Block block = entry.getKey();
                    BlockData blockData = entry.getValue();

                    if (block.getType().isEmpty()) continue;


                    block.setBlockData(blockData);
                }
                isActive = false;
                blocksAffected.clear();
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 20L * 10);
    }
}