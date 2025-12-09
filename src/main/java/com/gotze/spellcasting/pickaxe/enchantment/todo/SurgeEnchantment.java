//package com.gotze.spellcasting.pickaxe.enchantment;
//
//import com.gotze.spellcasting.feature.bossbar.LootCrateManager;
//import com.gotze.spellcasting.data.PickaxeData;
//import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//
//import java.util.concurrent.ThreadLocalRandom;
//
//public class SurgeEnchantment extends Enchantment implements BlockBreakListener {
//
//    public SurgeEnchantment() {
//        super(EnchantmentType.SURGE);
//    }
//
//    @Override
//    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
//        int energyFromBlock = LootCrateManager.getEnergyFromBlock(block);
//        if (energyFromBlock == 1) return;
//
//        double random = ThreadLocalRandom.current().nextDouble();
//        int multiplier = switch (getLevel()) {
//            case 1 -> random < 0.33 ? 2 : 1;
//            case 2 -> random < 0.25 ? 3 : random < 0.50 ? 2 : 1;
//            case 3 -> random < 0.20 ? 4 : random < 0.40 ? 3 : random < 0.60 ? 2 : 1;
//            default -> throw new IllegalStateException("Unexpected Surge enchantment level: " + getLevel());
//        };
//        if (multiplier == 1) return;
//
//        if (multiplier == 2) {
//            LootCrateManager.applyEnergyToBossBar(player, energyFromBlock);
//            player.sendMessage("Surge gave you " + energyFromBlock + " energy!");
//
//        } else if (multiplier == 3) {
//            LootCrateManager.applyEnergyToBossBar(player, energyFromBlock * 2);
//            player.sendMessage("Surge gave you " + energyFromBlock * 2 + " energy!");
//
//        } else if (multiplier == 4) {
//            LootCrateManager.applyEnergyToBossBar(player, energyFromBlock * 3);
//            player.sendMessage("Surge gave you " + energyFromBlock * 3 + " energy!");
//        }
//    }
//}
