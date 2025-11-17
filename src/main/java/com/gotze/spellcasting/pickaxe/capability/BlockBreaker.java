package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBreaker {

    default void breakBlocks(Player player, List<Block> blocks, PickaxeData pickaxeData, boolean isNaturalBreak) {
        for (Block block : blocks) {
            breakBlock(player, block, pickaxeData, isNaturalBreak);
        }
    }

    default void breakBlock(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!Spellcasting.getMineManager().isInMine(block)) return;

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, isNaturalBreak);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, isNaturalBreak);
            }
        }
        pickaxeData.addBlocksBroken(1);
    }
}