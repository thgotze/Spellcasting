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

public class HammerAbility extends Ability implements BlockBreakAware, BlockDamageAware {

    boolean isActive;
    BlockFace blockFace;

    public HammerAbility() {
        super(AbilityType.HAMMER);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        isActive = true;
        player.sendMessage("Hammer ability activated!");

//        Material material = Material.REDSTONE;
//        ItemStack itemStack = new ItemStackBuilder(Material.PAPER)
//                .itemModel(NamespacedKey.minecraft(material.name().toLowerCase()))
//                .build();
//
//        ItemStack itemStack1 = new ItemStackBuilder(heldItemClone)
//                .itemModel(NamespacedKey.minecraft(material1.name().toLowerCase()))
//                .build();

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Material originalType = heldItem.getType();
        Material mace = Material.MACE;

        heldItem.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(mace.name().toLowerCase()));

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

        Block centerBlock = event.getBlock();

        List<Block> blocksToBreak;
        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 1, 3);
        } else if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 3, 1);
        } else {
            blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 1, 3, 3);
        }
        blocksToBreak.removeIf(Block::isEmpty);

        for (Block block : blocksToBreak) {
            block.breakNaturally(true);
        }

        pickaxeData.addBlocksBroken(blocksToBreak.size() - 1);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (!this.isActive) return;
        this.blockFace = event.getBlockFace();
    }
}