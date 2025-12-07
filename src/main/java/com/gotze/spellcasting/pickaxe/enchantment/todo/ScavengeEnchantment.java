//package com.gotze.spellcasting.pickaxe.enchantment.todo;
//
//import com.gotze.spellcasting.data.PickaxeData;
//import com.gotze.spellcasting.pickaxe.capability.BlockDropItemListener;
//import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
//import com.gotze.spellcasting.util.block.BlockCategories;
//import org.bukkit.Material;
//import org.bukkit.block.BlockState;
//import org.bukkit.entity.Item;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class ScavengeEnchantment extends Enchantment implements BlockDropItemListener {
//
//    private static final Material[] SCAVENGE_MATERIALS = {
//            Material.RAW_COPPER,
//            Material.RAW_IRON,
//            Material.RAW_GOLD
//    };
//
//    public ScavengeEnchantment() {
//        super(EnchantmentType.SCAVENGE);
//    }
//
//    @Override
//    public void onBlockDropItem(Player player, BlockState blockState, List<Item> droppedItems, PickaxeData pickaxeData) {
//        if (!BlockCategories.FILLER_BLOCKS.contains(blockState.getType())) return;
//        // 1% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.01) return;
//
//        // Randomly select one of the three raw materials
//        Material scavengedMaterial = SCAVENGE_MATERIALS[ThreadLocalRandom.current().nextInt(SCAVENGE_MATERIALS.length)];
//
//        // Create the item and drop it at the block location
//        ItemStack scavengedItem = ItemStack.of(scavengedMaterial, 1);
//        Item droppedItem = blockState.getWorld().dropItemNaturally(blockState.getLocation().toCenterLocation(), scavengedItem);
//        droppedItems.add(droppedItem);
//    }
//}