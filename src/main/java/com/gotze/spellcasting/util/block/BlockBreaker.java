package com.gotze.spellcasting.util.block;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.Loot;
import org.bukkit.*;
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
        Material blockType = block.getType();
        if (blockType.isAir() || blockType == Material.BEDROCK) return;

        for (Enchantment enchantment : pickaxeData.enchantments()) {
            if (enchantment instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, isNaturalBreak);
            }
        }

        for (Ability ability : pickaxeData.abilities()) {
            if (ability instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, isNaturalBreak);
            }
        }

        handleBlockBreak(player, block, pickaxeData, isNaturalBreak);
    }

    static void handleBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        pickaxeData.addBlocksBroken(1);

        World world = player.getWorld();
        Location blockLocation = block.getLocation().toCenterLocation();

        Loot oreLoot = BlockCategories.ORE_BLOCKS.get(block.getType());
        if (oreLoot != null) {
            world.dropItemNaturally(blockLocation, oreLoot.rollChance().orElseThrow());

            world.playEffect(blockLocation, Effect.STEP_SOUND, block.getBlockData());
            SoundGroup soundGroup = block.getBlockSoundGroup();

            world.playSound(blockLocation, soundGroup.getBreakSound(), soundGroup.getVolume(), soundGroup.getPitch());

            block.setType(Material.AIR, false);

        } else if (!isNaturalBreak) {
            block.breakNaturally(true);
        }
    }
}