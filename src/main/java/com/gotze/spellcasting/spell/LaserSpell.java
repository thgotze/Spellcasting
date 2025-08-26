package com.gotze.spellcasting.spell;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LaserSpell extends Spell {

    public LaserSpell(Player player) {
        super.player = player;
        super.spellName = "Laser";
    }

    @Override
    public void cast() {
        Location startLocation = player.getEyeLocation();
        Vector lookingDirection = player.getEyeLocation().getDirection().normalize();
        World world = startLocation.getWorld();

        world.playSound(startLocation, Sound.ENTITY_GUARDIAN_ATTACK, 0.1f, 1.5f);

        new BukkitRunnable() {
            double distance = 0;

            @Override
            public void run() {
                if (distance >= 20) {
                    this.cancel();
                    return;
                }

                Location currentLocation = startLocation.clone().add(lookingDirection.clone().multiply(distance));

                world.spawnParticle(Particle.DUST, currentLocation, 1,
                        new Particle.DustOptions(Color.RED, 1.0f));
                world.spawnParticle(Particle.FLAME, currentLocation, 1,
                        0, 0, 0, 0);

                ArrayList<Block> blocksToBreak = BlockUtils.getBlocksInShape(player, currentLocation, "diamond", 3);

                for (Block block : blocksToBreak) {
                    if (!block.getType().isAir() && block.getType() != Material.BEDROCK) {
                        block.setType(Material.AIR);
//                        block.breakNaturally();
                    }
                }
                distance += 1;
            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0L, 1L);
    }
}