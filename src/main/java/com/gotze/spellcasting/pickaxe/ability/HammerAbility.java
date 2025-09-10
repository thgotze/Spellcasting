package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HammerAbility extends AbstractAbility {

    public HammerAbility(Player player) {
        super(player);
    }

    @Override
    public void activate() {

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                ticks++;
                if (ticks > 100) {
                    this.cancel();
                }

            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0, 1);
    }
}