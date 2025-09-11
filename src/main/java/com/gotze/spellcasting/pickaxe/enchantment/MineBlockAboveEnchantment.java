package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class MineBlockAboveEnchantment extends Enchantment {

    public MineBlockAboveEnchantment() {
        super(EnchantmentType.MINE_BLOCK_ABOVE);
    }

    @Override
    public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        double chance = getLevel() * 0.05;

        if (Math.random() < chance) {
            Block blockAbove = event.getBlock().getRelative(BlockFace.UP);
            if (!blockAbove.getType().isAir()) {
                blockAbove.breakNaturally(true);
                pickaxeData.addBlocksBroken(1);
            }
        }
    }
}