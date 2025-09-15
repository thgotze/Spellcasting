package com.gotze.spellcasting;

import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootPot implements Listener {
    private static final float POT_SPAWN_CHANCE = 1.0f / 300; // 1 in 300
    private static final Random random = new Random();
    private static final Map<Material, List<ItemStack>> POT_LOOT = Map.of(
            Material.PRIZE_POTTERY_SHERD, List.of(
                    ItemStack.of(Material.DIAMOND, 4),
                    ItemStack.of(Material.EMERALD, 4),
                    ItemStack.of(Material.AMETHYST_SHARD, 4),
                    ItemStack.of(Material.QUARTZ, 4),
                    ItemStack.of(Material.GOLD_INGOT, 4),
                    ItemStack.of(Material.IRON_INGOT, 4)),
            Material.PLENTY_POTTERY_SHERD, List.of(
                    ItemStack.of(Material.RAW_COPPER_BLOCK, 48),
                    ItemStack.of(Material.RAW_IRON_BLOCK, 32),
                    ItemStack.of(Material.RAW_GOLD_BLOCK, 16))
    );
    private static final Set<Material> FILLER_BLOCKS = EnumSet.of(
            Material.STONE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,
            Material.NETHERRACK,
            Material.SANDSTONE
    );

    @EventHandler
    public void onLootPotBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        if (blockType != Material.DECORATED_POT) return;

        event.setDropItems(false);

        DecoratedPot decoratedPot = (DecoratedPot) block.getState();
        Material sherdType = decoratedPot.getSherd(DecoratedPot.Side.FRONT);
        List<ItemStack> loot = POT_LOOT.get(sherdType);

        for (ItemStack item : loot) {
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5D, 0.5D, 0.5D), item);
        }
    }

    @EventHandler
    public void onFillerBlockBreakSpawnLootPot(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (!FILLER_BLOCKS.contains(blockType)) return;
        if (random.nextFloat() >= POT_SPAWN_CHANCE) return;

        Player player = event.getPlayer();
        BlockFace playerFacing = player.getFacing();

        List<Block> candidates = BlockUtils.getBlocksInSquarePattern(
                block.getRelative(playerFacing, 7), 5, 1, 5);
        Block chosen = candidates.get(random.nextInt(candidates.size()));

        chosen.setType(Material.DECORATED_POT);
        DecoratedPot lootPot = (DecoratedPot) chosen.getState();

        Material randomSherd = new ArrayList<>(POT_LOOT.keySet())
                .get(random.nextInt(POT_LOOT.size()));

        lootPot.setSherd(DecoratedPot.Side.FRONT, randomSherd);
        lootPot.setSherd(DecoratedPot.Side.BACK, randomSherd);
        lootPot.setSherd(DecoratedPot.Side.RIGHT, randomSherd);
        lootPot.setSherd(DecoratedPot.Side.LEFT, randomSherd);

        lootPot.update(true, false);

        player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }
}