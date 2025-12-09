package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockDropItemListener;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.gotze.spellcasting.Spellcasting.plugin;

public class YankEnchantment extends Enchantment implements BlockDropItemListener {

    public YankEnchantment() {
        super(EnchantmentType.YANK);
    }

    @Override
    public void onBlockDropItem(Player player, BlockState blockState, List<Item> droppedItems, PickaxeData pickaxeData) {
        new BukkitRunnable() {
            @Override
            public void run() {
                droppedItems.forEach(item -> {
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
                    }.runTaskTimer(plugin, 0L, 1L);
                });
            }
        }.runTaskLater(plugin, 1L);
    }
}
