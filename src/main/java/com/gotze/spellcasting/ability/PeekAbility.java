package com.gotze.spellcasting.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockCategories;
import com.gotze.spellcasting.util.BlockDamageAware;
import com.gotze.spellcasting.util.BlockUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class PeekAbility extends Ability implements BlockDamageAware {
    private boolean isActive;
    private Map<Block, BlockData> blocksAffected;

    public PeekAbility() {
        super(AbilityType.PEEK);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (!this.isActive) return;
        if (!blocksAffected.containsKey(event.getBlock())) return;
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;

        RayTraceResult rayTraceResult = player.rayTraceBlocks(4.5);
        if (rayTraceResult == null) return;

        Block targetBlock = rayTraceResult.getHitBlock();
        if (targetBlock == null) return;

        BlockFace blockFace = rayTraceResult.getHitBlockFace();
        if (blockFace == null) return;

        this.isActive = true;
        this.blocksAffected = new HashMap<>();

        Block centerBlock = targetBlock.getRelative(blockFace.getOppositeFace(), 2);
        List<Block> blockList = BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 5, 5);
        blockList.removeIf(Block::isEmpty);
        blockList.removeIf(block -> !BlockCategories.FILLER_BLOCKS.contains(block.getType()));

        for (Block block : blockList) {
            blocksAffected.put(block, block.getBlockData());
            block.setType(Material.GLASS);
        }
        player.playSound(player, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
        player.spawnParticle(Particle.WITCH, targetBlock.getLocation().toCenterLocation(), 100);
        ItemStack pickaxe = ItemStack.of(Material.STONE_PICKAXE);

        Tool stonePickaxeDefaultData = ItemType.STONE_PICKAXE.getDefaultData(DataComponentTypes.TOOL);
        List<Tool.Rule> defaultRules = stonePickaxeDefaultData.rules();


        List<BlockType> allBlocksExceptStone = new ArrayList<>();
        List<Tool.Rule> modifiedRules = new ArrayList<>();


        for (Tool.Rule rule : defaultRules) {
            // Get all block keys from this rule
            List<TypedKey<BlockType>> blocksWithoutStone = new ArrayList<>();

            for (TypedKey<BlockType> blockKey : rule.blocks()) {
                // Check if this block is NOT stone
                if (!blockKey.key().equals(BlockType.STONE.key().key())) {
                    blocksWithoutStone.add(blockKey);
                }
            }

            // Only add the rule if it still has blocks after removing stone
            if (!blocksWithoutStone.isEmpty()) {
                Tool.Rule modifiedRule = Tool.rule(
                        RegistrySet.keySet(RegistryKey.BLOCK, Set.copyOf(blocksWithoutStone)),
                        rule.speed(),
                        rule.correctForDrops()
                );
                modifiedRules.add(modifiedRule);
            }
        }



//        Tool.Rule hardToMineRule = defaultRules.getFirst();
//        Tool.Rule notHardToMineRule = defaultRules.getLast();
//
//        player.sendMessage("\n######## Hard to mine blocks: #########");
//        for (TypedKey<BlockType> block : hardToMineRule.blocks()) {
//            player.sendMessage(block.key().asMinimalString());
//        }
//
//        player.sendMessage("\n######### NOT Hard to mine blocks: #########");
//
//
//        RegistryKeySet<BlockType> blockTypeRegistryKeySet = notHardToMineRule.blocks();
//        blockTypeRegistryKeySet.()
//        blockTypeRegistryKeySet.resolve(blockTypeRegistryKeySet.registryKey())
//        for (TypedKey<BlockType> block : notHardToMineRule.blocks()) {
//            RegistrySet.keySet(RegistryKey.BLOCK, block);
//
//        }
//
//        for (TypedKey<BlockType> block : notHardToMineRule.blocks()) {
//            if (!block.key().equals(RegistryKey.BLOCK.typedKey(Material.STONE.key()))) {
//                allBlocksExceptStone.add(block.value());
//            }
////        }



//        Tool.Rule ruleWithoutStone = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
//                        allBlocksExceptStone),
//                1000f,
//                TriState.FALSE);
//
        Tool.Rule stoneRule = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
                        List.of(BlockType.GLASS)),
                2f,
                TriState.TRUE);

//        Tool.Rule dirtRule = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
//                        List.of(BlockType.DIRT)),
//                0.10f,
//                TriState.FALSE);



        Tool tool = Tool.tool()
                .addRules(modifiedRules)
                .addRule(stoneRule)
                .build();

        pickaxe.setData(DataComponentTypes.TOOL, tool);

//        player.getInventory().addItem(pickaxe);


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Block, BlockData> entry : blocksAffected.entrySet()) {
                    Block block = entry.getKey();
                    BlockData blockData = entry.getValue();

                    if (block.getType().isEmpty()) continue;


                    block.setBlockData(blockData);
                }
                isActive = false;
//                player.playSound(player, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.0f, 1.0f);
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), 20L * 5);
    }
}