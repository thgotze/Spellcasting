package com.gotze.spellcasting.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BazookaAbility extends Ability {

    public BazookaAbility() {
        super(AbilityType.BAZOOKA);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        final World world = player.getWorld();
        final Location startLocation = player.getEyeLocation();
        final Vector lookingDirection = player.getLocation().getDirection();

        float yaw = player.getLocation().getYaw();
        double radians = Math.toRadians(yaw);
        Vector horizontalDirection = new Vector(-Math.sin(radians), 0, Math.cos(radians)).normalize();
        Vector recoil = horizontalDirection.multiply(-1);
        player.setVelocity(recoil);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                Location laserLocation = startLocation.clone()
                        .add(lookingDirection.clone().multiply(ticks * 1.5));

                world.spawnParticle(Particle.POOF, laserLocation, 3, 0, 0, 0, 0);
                Block targetBlock = laserLocation.getBlock();
                if (!targetBlock.getType().isAir()) {

                    List<Block> blocksToBreak = new ArrayList<>(BlockUtils.getBlocksInSpherePattern(targetBlock, 9, 7, 9).stream()
                            .filter(block -> !block.getType().isAir())
                            .filter(block -> block.getType() != Material.BEDROCK)
                            .toList());

                    for (Block block : blocksToBreak) {
                        block.breakNaturally(player.getInventory().getItemInMainHand(), true);
                    }
                    player.sendMessage("You broke: " + blocksToBreak.size() + " blocks");
                    world.playSound(targetBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f);
                    world.spawnParticle(Particle.POOF, laserLocation, 350, 2, 2, 2, 0.35);

                    this.cancel();
                }

                ticks++;
                if (ticks >= 64) this.cancel();
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0, 1);
    }
}