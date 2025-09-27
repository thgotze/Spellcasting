package com.gotze.spellcasting.feature.pickaxe.enchantment;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class PhantomQuarryEnchantment extends Enchantment {

    public PhantomQuarryEnchantment() {
        super(EnchantmentType.PHANTOM_QUARRY);
    }

    @Override
    public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {
        Block block = event.getBlock();
        World world = block.getWorld();

        List<Block> cornerBlocks = new ArrayList<>();

        Block corner1 = block.getRelative(2, 0, 2);
        Block corner2 = block.getRelative(2, 0, -2);
        Block corner3 = block.getRelative(-2, 0, 2);
        Block corner4 = block.getRelative(-2, 0, -2);

        cornerBlocks.add(corner1);
        cornerBlocks.add(corner2);
        cornerBlocks.add(corner3);
        cornerBlocks.add(corner4);

        boolean allEmpty = true;
        for (Block cornerBlock : cornerBlocks) {
            if (!cornerBlock.getType().isEmpty()) {
                allEmpty = false;
                break;
            }
        }

        if (allEmpty) {
            List<Block> blocksToBreak = BlockUtils.getBlocksInSquarePattern(block, 5, 1, 5);
            for (Block blockToBreak : blocksToBreak) {
                blockToBreak.breakNaturally(true);
            }
            return;
        }

        BlockData invisibleBlock = Material.TINTED_GLASS.createBlockData();

        for (Block chosenBlock : cornerBlocks) {
            Location blockLocation = chosenBlock.getLocation().add(0.0625f, 0.0625f, 0.0625f);
            BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(blockLocation, EntityType.BLOCK_DISPLAY);
            blockDisplay.setBlock(invisibleBlock);
            blockDisplay.setGlowing(true);
            blockDisplay.setGlowColorOverride(Color.YELLOW);
            blockDisplay.setBrightness(new Display.Brightness(15, 15));
            blockDisplay.setTransformationMatrix(new Matrix4f().scale(0.875f, 0.875f, 0.875f));
        }
    }
}