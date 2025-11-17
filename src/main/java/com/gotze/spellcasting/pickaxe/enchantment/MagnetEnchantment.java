package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockDropItemLister;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MagnetEnchantment extends Enchantment implements BlockDropItemLister {

    public MagnetEnchantment() {
        super(EnchantmentType.MAGNET);
    }

    @Override
    public void onBlockDropItem(Player player, BlockDropItemEvent event, PickaxeData pickaxeData) {
        event.getItems().forEach(item -> {
            item.setPickupDelay(0);
            item.setGravity(false);

            new BukkitRunnable() {
                final Vector direction = player.getEyeLocation().toVector()
                        .subtract(item.getLocation().toVector());
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks == 0) {
                        item.setVelocity(direction.clone().multiply(0.25));
                    } else if (ticks == 1) {
                        item.setVelocity(direction.clone().multiply(0.125));
                    } else if (ticks == 2) {
                        item.setVelocity(direction.clone().multiply(0.0625));
                    } else if (ticks == 3) {
                        item.setGravity(true);
                        cancel();
                        return;
                    }
                    ticks++;
                }
            }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
        });
    }
}