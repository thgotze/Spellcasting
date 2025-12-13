package com.gotze.spellcasting.pickaxe.enchantment.todo;

//public class MomentumEnchantment extends Enchantment implements BlockBreakListener, BlockBreaker {
//    private static final BlockData TINTED_GLASS = Material.TINTED_GLASS.createBlockData();
//
//    private Block markedBlock;
//    private BlockDisplay markedBlockDisplay;
//
//    public MomentumEnchantment() {
//        super(EnchantmentType.MOMENTUM);
//    }
//
//    @Override
//    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
//        // Create the block display once
//        if (markedBlockDisplay == null) {
//            markedBlockDisplay = (BlockDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.BLOCK_DISPLAY);
//            markedBlockDisplay.setTransformationMatrix(new Matrix4f().scale(0.9921875f, 0.9921875f, 0.9921875f));
//            markedBlockDisplay.setBlock(TINTED_GLASS);
//            markedBlockDisplay.setGlowing(true);
//            markedBlockDisplay.setGlowColorOverride(Color.YELLOW);
//            markedBlockDisplay.setVisibleByDefault(false);
//        }
//
//        // ---------------
//        // First time
//        // ---------------
//        if (markedBlock == null) {
//            if (isNaturalBreak) {
//                markNewNearbyOreBlock(player, block);
//            }
//            return;
//        }
//
//        // ---------------
//        // Subsequent times
//        // ---------------
//        if (markedBlock.equals(block)) {
//            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, 0, false, false)); // Haste I for 3 seconds
//            markNewNearbyOreBlock(player, markedBlock);
//        }
//    }
//
//    private void markNewNearbyOreBlock(Player player, Block origin) {
//        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(origin, 3, 3, 3);
//        candidateBlocks.remove(origin);
//        candidateBlocks.removeIf(candidate -> !BlockCategories.ORE_BLOCKS.containsKey(candidate.getType()));
//
//        if (candidateBlocks.isEmpty()) {
//            markedBlock = null;
//            player.hideEntity(plugin(), markedBlockDisplay);
//        } else {
//            markedBlock = candidateBlocks.remove(ThreadLocalRandom.current().nextInt(candidateBlocks.size()));
//            markedBlockDisplay.teleport(markedBlock.getLocation().add(0.001953125f, 0.001953125f, 0.001953125f));
//            player.showEntity(plugin(), markedBlockDisplay);
//
//        }
//    }
//}
