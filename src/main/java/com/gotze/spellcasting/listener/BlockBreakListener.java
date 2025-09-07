package com.gotze.spellcasting.listener;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.data.PlayerPickaxeService;
import com.gotze.spellcasting.enchantment.Enchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) {
            player.sendMessage(Component.text("not holding own pickaxe")
                    .color(NamedTextColor.RED));
            return;
        }
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        PickaxeData pickaxeData = PlayerPickaxeService.getPlayerPickaxeData(player);

        int blocksBroken = 1;

        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
        if (enchantments != null) {
            Enchantment hasteAndSpeedEnchant = pickaxeData.getEnchantment(Enchantment.EnchantmentType.HASTE_AND_SPEED);
            if (hasteAndSpeedEnchant != null) {
                int level = hasteAndSpeedEnchant.getLevel();
                int amplifier = level - 1;

                if (Math.random() < 0.5) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 100, amplifier));
                    player.sendMessage(Component.text("Haste " + level + "!")
                            .color(NamedTextColor.YELLOW));
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, amplifier));
                    player.sendMessage(Component.text("Speed " + level + "!")
                            .color(NamedTextColor.AQUA));
                }
            }

            Enchantment mineBlockAboveEnchant = pickaxeData.getEnchantment(Enchantment.EnchantmentType.MINE_BLOCK_ABOVE);
            if (mineBlockAboveEnchant != null) {
//                if (Math.random() < 0.5) {
                if (Math.random() < 1) {
                    Block blockAbove = event.getBlock().getRelative(BlockFace.UP);

                    if (!blockAbove.getType().isAir()) {
                        blockAbove.breakNaturally(true);
                        blocksBroken++;
                    }

                    player.sendMessage(Component.text("Broke block above!")
                            .color(NamedTextColor.DARK_AQUA));
                }
            }
        }

        PlayerPickaxeService.incrementBlocksBrokenCounter(player, blocksBroken);

        List<Component> lore = PlayerPickaxeService.getPickaxeLore(player);
        heldItem.lore(lore);

        if (player.isSneaking()) {
            player.sendMessage(pickaxeData.toString());
        }
    }
}