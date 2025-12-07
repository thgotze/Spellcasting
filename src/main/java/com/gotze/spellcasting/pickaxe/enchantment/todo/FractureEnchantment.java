//package com.gotze.spellcasting.pickaxe.enchantment;
//
//import com.gotze.spellcasting.Spellcasting;
//import com.gotze.spellcasting.data.PickaxeData;
//import com.gotze.spellcasting.mine.MineManager;
//import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
//import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
//import com.gotze.spellcasting.pickaxe.capability.BlockDamageAbortListener;
//import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
//import org.bukkit.Effect;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//import org.bukkit.event.block.BlockDamageAbortEvent;
//import org.bukkit.event.block.BlockDamageEvent;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.util.*;
//
//public class FractureEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker, BlockDamageAbortListener {
//
//    private static final float[] POSSIBLE_BLOCK_DAMAGE_STATES = {0.7f, 0.8f, 0.9f};
//
//    private boolean isActive;
//    private boolean currentlyMining;
//    private Block currentlyMiningBlock;
//    private final Random random = new Random();
//    private final Map<Block, Float> fracturedBlocks = new HashMap<>();
//
//    public FractureEnchantment() {
//        super(EnchantmentType.FRACTURE);
//    }
//
//    @Override
//    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
//        // ---------------
//        // First time
//        // ---------------
//        if (!this.isActive) {
//            if (!isNaturalBreak) return;
//            this.isActive = true;
//
//            List<Block> blocksToFracture = getFractureBlocks(block, getLevel());
//            if (blocksToFracture == null) return;
//            Collections.shuffle(blocksToFracture);
//
//            int damageStateIndex = player.getEntityId();
//
//            for (Block fracturedBlock : blocksToFracture) {
//                if (fracturedBlock.getType().isAir()) continue;
//                if (!MineManager.isInAnyMine(fracturedBlock)) continue;
//
//                float randomDamageState = POSSIBLE_BLOCK_DAMAGE_STATES[random.nextInt(3)];
//                fracturedBlocks.put(fracturedBlock, randomDamageState);
//
//                player.sendBlockDamage(fracturedBlock.getLocation(), randomDamageState, ++damageStateIndex);
//                player.playEffect(fracturedBlock.getLocation(), Effect.STEP_SOUND, fracturedBlock.getBlockData());
//            }
//
//            if (fracturedBlocks.isEmpty()) {
//                this.isActive = false;
//            }
//            return;
//        }
//        // ---------------
//        // Subsequent times
//        // ---------------
//        fracturedBlocks.remove(block);
//        if (fracturedBlocks.isEmpty()) {
//            this.isActive = false;
//        }
//    }
//
//    @Override
//    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
//        this.currentlyMiningBlock = event.getBlock();
//        this.currentlyMining = true;
//
//        if (!this.isActive) return;
//        if (!fracturedBlocks.containsKey(currentlyMiningBlock)) return;
//
//        float initialDamageState = fracturedBlocks.get(currentlyMiningBlock);
//
//        new BukkitRunnable() {
//            int miningDurationTicks = 0;
//
//            @Override
//            public void run() {
//                if (!currentlyMining ||
//                        !fracturedBlocks.containsKey(currentlyMiningBlock) ||
//                        !isActive ||
//                        currentlyMiningBlock != event.getBlock()) {
//                    player.sendBlockDamage(currentlyMiningBlock.getLocation(), initialDamageState, player.getEntityId() + 123);
//                    cancel();
//                    return;
//                }
//                miningDurationTicks++;
//
//                if (miningDurationTicks == 1) {
//                    player.sendBlockDamage(currentlyMiningBlock.getLocation(), Math.min(initialDamageState + 0.1f, 1.0f), player.getEntityId() + 123);
//
//                } else if (miningDurationTicks == 2) {
//                    if (initialDamageState == 0.9f) {
//                        player.playEffect(currentlyMiningBlock.getLocation(), Effect.STEP_SOUND, currentlyMiningBlock.getBlockData());
//                        player.breakBlock(currentlyMiningBlock);
//                        player.sendBlockDamage(currentlyMiningBlock.getLocation(), 0.0f, player.getEntityId() + 123);
//                        cancel();
//                        return;
//                    }
//                    player.sendBlockDamage(currentlyMiningBlock.getLocation(), Math.min(initialDamageState + 0.2f, 1.0f), player.getEntityId() + 123);
//
//                } else if (miningDurationTicks == 3) {
//                    if (initialDamageState == 0.8f) {
//                        player.playEffect(currentlyMiningBlock.getLocation(), Effect.STEP_SOUND, currentlyMiningBlock.getBlockData());
//                        player.breakBlock(currentlyMiningBlock);
//                        player.sendBlockDamage(currentlyMiningBlock.getLocation(), 0.0f, player.getEntityId() + 123);
//                        cancel();
//                        return;
//                    }
//                    player.sendBlockDamage(currentlyMiningBlock.getLocation(), Math.min(initialDamageState + 0.3f, 1.0f), player.getEntityId() + 123);
//
//                } else if (miningDurationTicks == 4) {
//                    if (initialDamageState == 0.7f) {
//                        player.playEffect(currentlyMiningBlock.getLocation(), Effect.STEP_SOUND, currentlyMiningBlock.getBlockData());
//                        player.breakBlock(currentlyMiningBlock);
//                        player.sendBlockDamage(currentlyMiningBlock.getLocation(), 0.0f, player.getEntityId() + 123);
//                        cancel();
//                        return;
//                    }
//                    player.sendBlockDamage(currentlyMiningBlock.getLocation(), Math.min(initialDamageState + 0.4f, 1.0f), player.getEntityId() + 123);
//                }
//            }
//        }.runTaskTimer(Spellcasting.getPlugin(), 0, 1);
//    }
//
//    @Override
//    public void onBlockDamageAbort(Player player, BlockDamageAbortEvent event, PickaxeData pickaxeData) {
//        this.currentlyMining = false;
//    }
//
//    private List<Block> getFractureBlocks(Block origin, int level) {
//        return switch (level) {
//            case 1 -> getLevel1Blocks(origin);
//            case 2 -> getLevel2Blocks(origin);
//            case 3 -> getLevel3Blocks(origin);
//            default -> null;
//        };
//    }
//
//    private List<Block> getLevel1Blocks(Block origin) {
//        List<Block> blocks = new ArrayList<>();
//
//        // 6 blocks in all cardinal directions + up/down
//        blocks.add(origin.getRelative(1, 0, 0));   // East
//        blocks.add(origin.getRelative(-1, 0, 0));  // West
//        blocks.add(origin.getRelative(0, 0, 1));   // South
//        blocks.add(origin.getRelative(0, 0, -1));  // North
//        blocks.add(origin.getRelative(0, 1, 0));   // Up
//        blocks.add(origin.getRelative(0, -1, 0));  // Down
//
//        return blocks;
//    }
//
//    private List<Block> getLevel2Blocks(Block origin) {
//        List<Block> blocks = new ArrayList<>();
//
//        // 3x3x3 cube with corners cut off (27 - 8 corners - 1 center = 18 blocks)
//        for (int x = -1; x <= 1; x++) {
//            for (int y = -1; y <= 1; y++) {
//                for (int z = -1; z <= 1; z++) {
//                    // Skip the center block (origin)
//                    if (x == 0 && y == 0 && z == 0) continue;
//
//                    // Skip the 8 corners
//                    if (Math.abs(x) == 1 && Math.abs(y) == 1 && Math.abs(z) == 1) continue;
//
//                    blocks.add(origin.getRelative(x, y, z));
//                }
//            }
//        }
//
//        return blocks;
//    }
//
//    private List<Block> getLevel3Blocks(Block origin) {
//        List<Block> blocks = new ArrayList<>();
//
//        // Full 3x3x3 cube minus center (26 blocks)
//        for (int x = -1; x <= 1; x++) {
//            for (int y = -1; y <= 1; y++) {
//                for (int z = -1; z <= 1; z++) {
//                    // Skip the center block (origin)
//                    if (x == 0 && y == 0 && z == 0) continue;
//
//                    blocks.add(origin.getRelative(x, y, z));
//                }
//            }
//        }
//
//        // Add 1 more block in each cardinal direction and up/down (6 additional blocks)
//        blocks.add(origin.getRelative(2, 0, 0));   // Far East
//        blocks.add(origin.getRelative(-2, 0, 0));  // Far West
//        blocks.add(origin.getRelative(0, 0, 2));   // Far South
//        blocks.add(origin.getRelative(0, 0, -2));  // Far North
//        blocks.add(origin.getRelative(0, 2, 0));   // Far Up
//        blocks.add(origin.getRelative(0, -2, 0));  // Far Down
//
//        return blocks;
//    }
//}