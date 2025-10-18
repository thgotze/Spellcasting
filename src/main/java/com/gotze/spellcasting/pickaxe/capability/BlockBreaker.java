package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
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

        handleBlockBreak(player, block, pickaxeData, isNaturalBreak);
    }

    static void handleBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        Loot oreLoot = BlockCategories.ORE_BLOCKS.get(block.getType());

        if (oreLoot != null) {
            World world = player.getWorld();
            Location blockLocation = block.getLocation().toCenterLocation();
            world.dropItemNaturally(blockLocation, oreLoot.drop());

            if (!isNaturalBreak) {
                world.playEffect(blockLocation, Effect.STEP_SOUND, block.getBlockData());

                SoundGroup soundGroup = block.getBlockSoundGroup();
                world.playSound(blockLocation, soundGroup.getBreakSound(), soundGroup.getVolume(), soundGroup.getPitch());
                block.setType(Material.AIR, false);
            }
        } else if (!isNaturalBreak) {
            block.breakNaturally(true);
        }
        PlayerPickaxeService.addBlocksBroken(player, 1);
    }
}