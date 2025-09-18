package com.gotze.spellcasting.feature.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HammerAbility extends Ability {

    public HammerAbility() {
        super(AbilityType.HAMMER);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {

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