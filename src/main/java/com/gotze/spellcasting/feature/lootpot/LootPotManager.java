package com.gotze.spellcasting.feature.lootpot;

import com.gotze.spellcasting.common.Drop;
import com.gotze.spellcasting.feature.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.BlockUtils;
import com.gotze.spellcasting.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static com.gotze.spellcasting.common.FillerBlocks.FILLER_BLOCKS;
import static com.gotze.spellcasting.common.LootContainerBlocks.LOOT_CONTAINERS;
import static com.gotze.spellcasting.common.OreBlocks.ORE_BLOCKS;

public class LootPotManager implements Listener {
    private static final Random random = new Random();
    private static final float POT_SPAWN_CHANCE = 1.0f / 300; // 1 in 300 chance

    public static final Map<Material, List<Drop>> LOOT_POTS = Map.of(
            Material.PLENTY_POTTERY_SHERD, List.of(
                    new Drop(Material.RAW_COPPER, 24, 72, 0.75),
                    new Drop(Material.RAW_IRON, 16, 48, 0.50),
                    new Drop(Material.RAW_GOLD, 8, 24, 0.25)
            ),
            Material.PRIZE_POTTERY_SHERD, List.of(
                    new Drop(Material.DIAMOND, 1, 0.25),
                    new Drop(Material.EMERALD, 1, 0.25),
                    new Drop(Material.QUARTZ, 1, 2),
                    new Drop(Material.AMETHYST_SHARD, 1, 2)
            ),
            Material.MINER_POTTERY_SHERD, Stream.of(Enchantment.EnchantmentType.values())
                    .map(enchantmentType ->
                            new Drop(enchantmentType.getMaterialRepresentation(),
                                    1,
                                    enchantmentType.getRarity().getWeight()))
                    .toList()
    );

    @EventHandler
    public void onLootPotBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DECORATED_POT) return;

        org.bukkit.block.data.type.DecoratedPot potData = (org.bukkit.block.data.type.DecoratedPot) block.getBlockData();
//        if (!potData.isCracked()) return;

        DecoratedPot pot = (DecoratedPot) block.getState();
        Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);
        if (!LOOT_POTS.containsKey(sherdType)) return;

        event.setDropItems(false);

        List<Drop> drops = LOOT_POTS.get(sherdType);
        List<ItemStack> loot = new ArrayList<>();

        do {
            for (Drop drop : drops) {
                drop.toItemStack().ifPresent(loot::add);
            }
            // test
            event.getPlayer().sendMessage("\nPotential loot:");
            for (ItemStack itemStack : loot) {
                event.getPlayer().sendMessage(StringUtils.toTitleCase(itemStack.getType().toString()));
            }
            //
        } while (loot.isEmpty());

        if (sherdType == Material.MINER_POTTERY_SHERD) {
            ItemStack itemStack = loot.get(random.nextInt(loot.size()));
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), itemStack);
            // test
            event.getPlayer().sendMessage("\nYou rollled: " + StringUtils.toTitleCase(itemStack.getType().toString()));
            //
        } else {
            for (ItemStack itemStack : loot) {
                block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), itemStack);
                event.getPlayer().sendMessage(itemStack.toString());
            }
        }
    }

    @EventHandler
    public void onBlockBreakTrySpawnLootPot(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (!FILLER_BLOCKS.contains(blockType) &&
                !ORE_BLOCKS.containsKey(blockType) &&
                !LOOT_CONTAINERS.containsKey(blockType)) return;

        if (random.nextFloat() >= POT_SPAWN_CHANCE) return;

        Player player = event.getPlayer();

        List<Block> candidates = BlockUtils.getBlocksInSquarePattern(
                block.getRelative(player.getFacing(), 7),
                5, 1, 5);

        Block chosenBlock = candidates.get(random.nextInt(candidates.size()));
        chosenBlock.setType(Material.DECORATED_POT);

        org.bukkit.block.data.type.DecoratedPot potData = (org.bukkit.block.data.type.DecoratedPot) chosenBlock.getBlockData();
        potData.setCracked(true);
        chosenBlock.setBlockData(potData);

        DecoratedPot pot = (DecoratedPot) chosenBlock.getState();
        Material randomSherd = new ArrayList<>(LOOT_POTS.keySet()).get(random.nextInt(LOOT_POTS.size()));
        pot.setSherd(DecoratedPot.Side.FRONT, randomSherd);
        pot.setSherd(DecoratedPot.Side.BACK, randomSherd);
        pot.setSherd(DecoratedPot.Side.RIGHT, randomSherd);
        pot.setSherd(DecoratedPot.Side.LEFT, randomSherd);
        pot.update(true, false);

        Location potLocation = pot.getLocation();
        player.playSound(potLocation, Sound.BLOCK_DECORATED_POT_INSERT, 10.0f, 1.0f);
        player.spawnParticle(Particle.DUST_PLUME, potLocation.clone().add(0.5, 1, 0.5),
                10, 0, 0, 0, 0);
    }

    @EventHandler
    public void onLootPotInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.DECORATED_POT) return;

        DecoratedPot pot = (DecoratedPot) block.getState();
        Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);
        if (!LOOT_POTS.containsKey(sherdType)) return;
        if (event.getItem() == null) return;

        event.setCancelled(true);
    }
}