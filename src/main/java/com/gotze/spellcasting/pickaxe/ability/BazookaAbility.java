package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BazookaAbility extends Ability implements BlockBreaker {

    private static final long BASE_COOLDOWN = 5_000;

    private long cooldown;

    public BazookaAbility() {
        super(AbilityType.BAZOOKA);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (System.currentTimeMillis() < cooldown) return;
        this.cooldown = System.currentTimeMillis() + BASE_COOLDOWN;
        player.sendMessage("Bazooka ability activated!");

        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector lookingDirection = player.getLocation().getDirection();
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
                        .add(lookingDirection.clone().multiply(ticks * 1.1));
//                        .add(lookingDirection.clone().multiply(ticks * 1.5));

                world.spawnParticle(Particle.POOF, laserLocation, 3, 0, 0, 0, 0);
                Block targetBlock = laserLocation.getBlock();
                if (!targetBlock.getType().isAir()) {
                    List<Block> blocksToBreak = new ArrayList<>(BlockUtils.getBlocksInSpherePattern(targetBlock, 9, 7, 9));
                    breakBlocks(player, blocksToBreak, pickaxeData, false);

                    world.playSound(targetBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    world.spawnParticle(Particle.POOF, laserLocation, 350, 2, 2, 2, 0.35);

                    this.cancel();
                }

                ticks++;
                if (ticks >= 64) this.cancel();
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Spellcasting.class), 0, 1);
    }
}