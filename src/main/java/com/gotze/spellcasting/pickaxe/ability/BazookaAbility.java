package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BazookaAbility extends Ability {

    public BazookaAbility() {
        super(AbilityType.BAZOOKA);
    }

    @Override
    public void activate(Player player, PickaxeData pickaxeData) {
        final World world = player.getWorld();
        final Location startLocation = player.getEyeLocation();
        final Vector lookingDirection = player.getLocation().getDirection();
        final BlockFace playerFacing = player.getFacing();

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

                world.spawnParticle(Particle.POOF, laserLocation, 3, 0D, 0D, 0D, 0D);
                Block targetBlock = laserLocation.getBlock();
                if (!targetBlock.getType().isAir()) {
                    ArrayList<Block> blocksToBreak = new ArrayList<>();
                    blocksToBreak.addAll(
                            BlockUtils.getBlocksInSpherePattern(targetBlock, 9, 7, 9).stream()
                                    .filter(block -> !block.getType().isAir())
                                    .filter(block -> block.getType() != Material.BEDROCK)
                                    .toList()
                    );
                    for (Block block : blocksToBreak) {
                        block.breakNaturally(true);
                    }
                    player.sendMessage("You broke: " + blocksToBreak.size() + " blocks");
                    world.playSound(targetBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 1.0f);
//                    world.spawnParticle(Particle.EXPLOSION_EMITTER, laserLocation, 1);
                    world.spawnParticle(Particle.POOF, laserLocation, 500, 2D, 2D, 2D, 0.35D);

                    this.cancel();
                }

                ticks++;
                if (ticks >= 64) this.cancel();
            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0, 1);
    }
}