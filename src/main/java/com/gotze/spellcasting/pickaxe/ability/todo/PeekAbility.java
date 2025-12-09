//package com.gotze.spellcasting.pickaxe.ability.todo;
//
//import com.gotze.spellcasting.Spellcasting;
//import com.gotze.spellcasting.data.PickaxeData;
//import com.gotze.spellcasting.pickaxe.ability.Ability;
//import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
//import com.gotze.spellcasting.pickaxe.enchantment.PhantomQuarryEnchantment;
//import com.gotze.spellcasting.util.block.BlockCategories;
//import com.gotze.spellcasting.util.block.BlockUtils;
//import org.bukkit.Material;
//import org.bukkit.Particle;
//import org.bukkit.Sound;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.block.data.BlockData;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static net.kyori.adventure.text.Component.text;
//import static net.kyori.adventure.text.format.NamedTextColor.RED;
//import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
//
//public class PeekAbility extends Ability {
//
//    private boolean isActive;
//    private final Map<Block, BlockData> affectedBlocks = new HashMap<>();
//
//    public PeekAbility() {
//        super(AbilityType.PEEK);
//    }
//
//    @Override
//    public void activateAbility(Player player, PickaxeData pickaxeData) {
//        if (this.isActive) return;
//        this.isActive = true;
//        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!").color(YELLOW)));
//
//        Block centerBlock = player.getLocation().getBlock().getRelative(BlockFace.UP);
//
//        List<Block> blockList = switch (getLevel()) {
//            case 1 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 5, 4, 5);
//            case 2 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 7, 6, 7);
//            case 3 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 9, 8, 9);
//            case 4 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 11, 10, 11);
//            case 5 -> BlockUtils.getBlocksInSquarePattern(centerBlock, 13, 12, 13);
//            default -> throw new IllegalStateException("Unexpected value: " + getLevel());
//        };
//
//        // Edge case to not glassify marked blocks from phantom quarry enchantment
//        PhantomQuarryEnchantment phantomQuarryEnchantment = (PhantomQuarryEnchantment) pickaxeData.getEnchantment(Enchantment.EnchantmentType.PHANTOM_QUARRY);
//
//        for (Block block : blockList) {
//            Material blockType = block.getType();
//            if (blockType.isAir()) continue;
//
//            if (!BlockCategories.FILLER_BLOCKS.contains(blockType)) continue;
//
//            // Edge case to not glassify marked blocks from phantom quarry enchantment
//            if (phantomQuarryEnchantment != null && phantomQuarryEnchantment.isActive) {
//                if (phantomQuarryEnchantment.markedCornerBlocks.containsKey(block)) continue;
//            }
//            affectedBlocks.put(block, block.getBlockData());
//            block.setType(Material.AIR);
//        }
//
//        player.playSound(player, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.75f, 1.0f);
//        player.spawnParticle(Particle.WAX_OFF, centerBlock.getLocation().toCenterLocation(), 100, 3, 3,3, 20);
////        player.spawnParticle(Particle.WITCH, centerBlock.getLocation().toCenterLocation(), 1000, 2, 2,2, 0);
////        player.spawnParticle(Particle.DRAGON_BREATH, centerBlock.getLocation().toCenterLocation(), 1000, 2, 2,2, 0);
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                for (Map.Entry<Block, BlockData> entry : affectedBlocks.entrySet()) {
//                    Block block = entry.getKey();
//                    if (!block.getType().isAir()) continue;
//                    block.setBlockData(entry.getValue());
//                }
//                isActive = false;
//                player.sendActionBar(text("Peek ability expired",RED));
//                affectedBlocks.clear();
//            }
//        }.runTaskLater(plugin(), 20L * 10);
//    }
//}
