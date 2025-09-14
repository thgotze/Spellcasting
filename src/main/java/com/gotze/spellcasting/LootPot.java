package com.gotze.spellcasting;

import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LootPot implements Listener {
    private static final float POT_SPAWN_CHANCE = 1.0f / 300; // 1 in 300

    private static final Random random = new Random();
    private static final Set<Material> FILLER_BLOCKS = EnumSet.of(
            Material.STONE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,
            Material.NETHERRACK,
            Material.SANDSTONE);

    private static final Material[] SHERD_TYPES = {
            Material.BREWER_POTTERY_SHERD,
            Material.MINER_POTTERY_SHERD,
            Material.BURN_POTTERY_SHERD,
            Material.PRIZE_POTTERY_SHERD,
            Material.FLOW_POTTERY_SHERD,
            Material.PLENTY_POTTERY_SHERD
    };

    @EventHandler
    public void onBlockBreakSpawnLootPot(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Player player = event.getPlayer();

        if (blockType == Material.DECORATED_POT) {
            event.setDropItems(false);
            DecoratedPot pot = (DecoratedPot) block.getState();
            Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);

            Material loot = switch (sherdType) {
                case BREWER_POTTERY_SHERD -> Material.EXPERIENCE_BOTTLE;
                case MINER_POTTERY_SHERD -> Material.GOLD_INGOT;
                case BURN_POTTERY_SHERD -> Material.BLAZE_POWDER;
                case PRIZE_POTTERY_SHERD -> Material.DIAMOND;
                case FLOW_POTTERY_SHERD -> Material.EMERALD;
                case PLENTY_POTTERY_SHERD -> Material.IRON_ORE;
                default -> null;
            };

            if (loot != null) {
                block.getWorld().dropItemNaturally(block.getLocation(),ItemStack.of(loot));
            }
            return;
        }

        if (FILLER_BLOCKS.contains(blockType) && random.nextFloat() < POT_SPAWN_CHANCE) {
            BlockFace playerFacing = player.getFacing();

            List<Block> candidates = BlockUtils.getBlocksInSquarePattern(
                    block.getRelative(playerFacing, 7), 5, 1, 5);
            Block chosen = candidates.get(random.nextInt(candidates.size()));

            chosen.setType(Material.DECORATED_POT);
            DecoratedPot lootPot = (DecoratedPot) chosen.getState();

            Material randomSherd = SHERD_TYPES[random.nextInt(SHERD_TYPES.length)];
            lootPot.setSherd(DecoratedPot.Side.FRONT, randomSherd);
            lootPot.setSherd(DecoratedPot.Side.BACK, randomSherd);
            lootPot.setSherd(DecoratedPot.Side.RIGHT, randomSherd);
            lootPot.setSherd(DecoratedPot.Side.LEFT, randomSherd);

            lootPot.update(true, false);

            player.sendMessage("Loot pot spawned! Sherd: " + randomSherd);
        }
    }
}