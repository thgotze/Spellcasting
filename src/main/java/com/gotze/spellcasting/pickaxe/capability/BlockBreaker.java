package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.bossbar.LootCrateFeature;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.mine.MineManager;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface BlockBreaker {

    default void breakBlocks(Player player, List<Block> blocks, PickaxeData pickaxeData) {
        for (Block block : blocks) {
            breakBlock(player, block, pickaxeData);
        }
    }

    default void breakBlock(Player player, Block block, PickaxeData pickaxeData) {
        if (!MineManager.isInAnyMine(block)) return;

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, false);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, false);
            }
        }
        pickaxeData.addBlocksBroken(1);

        LootCrateFeature.processBlockBreak(player, block);

        List<Item> droppedItems = new ArrayList<>();

        World world = block.getWorld();
        Location blockLocation = block.getLocation().toCenterLocation();
        BlockState blockState = block.getState();

        Loot oreLoot = BlockCategories.ORE_BLOCKS.get(block.getType());
        if (oreLoot != null) {
            Item itemEntity = world.dropItemNaturally(blockLocation, oreLoot.drop());
            droppedItems.add(itemEntity);

        } else {
            for (ItemStack drop : block.getDrops()) {
                Item itemEntity = world.dropItemNaturally(blockLocation, drop);
                droppedItems.add(itemEntity);
            }
        }

        world.playEffect(blockLocation, Effect.STEP_SOUND, block.getBlockData());
        block.setType(Material.AIR, false);

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockDropItemListener listener) {
                listener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockDropItemListener listener) {
                listener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
            }
        }
    }
}