package com.gotze.spellcasting.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockBreakAware;
import com.gotze.spellcasting.util.BlockDamageAware;
import com.gotze.spellcasting.util.BlockUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TectonicShockwaveAbility extends Ability implements BlockBreakAware, BlockDamageAware {
    boolean isActive;
    BlockFace blockFace;

    public TectonicShockwaveAbility() {
        super(AbilityType.TECTONIC_SHOCKWAVE);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        isActive = true;
        player.sendMessage("Tectonic Shockwave ability activated!");

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Material originalType = heldItem.getType();
        Material sculkCatalyst = Material.SCULK_CATALYST;

        heldItem.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(sculkCatalyst.name().toLowerCase()));

        new BukkitRunnable() {
            @Override
            public void run() {
                heldItem.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(originalType.name().toLowerCase()));
                isActive = false;
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 20L * 10);
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        if (!this.isActive) return;

        player.sendMessage("proc tectonic shockwave");
        Block centerBlock = event.getBlock();

        List<Block> blocksToBreak;
        BlockFace oppositeFace = blockFace.getOppositeFace();

        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 1, 3);
        } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 3, 1);
        } else {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 1, 3, 3);
        }
        blocksToBreak.removeIf(Block::isEmpty);

        player.sendMessage("break1");
        for (Block block : blocksToBreak) {
            block.breakNaturally(true);
        }

        new BukkitRunnable() {
            List<Block> blocksToBreak;

            @Override
            public void run() {
                if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 1), 3, 1, 3);
                } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 1), 3, 3, 1);
                } else {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 1), 1, 3, 3);
                }
                blocksToBreak.removeIf(Block::isEmpty);

                player.sendMessage("break2");
                for (Block block : blocksToBreak) {
                    block.breakNaturally(true);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 2L);
        new BukkitRunnable() {
            List<Block> blocksToBreak;

            @Override
            public void run() {
                if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 2), 3, 1, 3);
                } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 2), 3, 3, 1);
                } else {
                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace, 2), 1, 3, 3);
                }
                blocksToBreak.removeIf(Block::isEmpty);

                player.sendMessage("break3");
                for (Block block : blocksToBreak) {
                    block.breakNaturally(true);
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 4L);

        new BukkitRunnable() {
            List<Block> blocksToBreak;

            @Override
            public void run() {
//                if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
//                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace,3), 3, 1, 3);
//                } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
//                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace,3), 3, 3, 1);
//                } else {
//                    blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock.getRelative(oppositeFace,3), 1, 3, 3);
//                }
//                blocksToBreak.removeIf(Block::isEmpty);
//
//                player.sendMessage("break4");
//                for (Block block : blocksToBreak) {
//                    block.breakNaturally(true);
//                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 6L);

    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.blockFace = event.getBlockFace();
    }
}
