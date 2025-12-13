package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.gotze.spellcasting.Spellcasting.plugin;

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
        player.swingMainHand();

        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector lookingDirection = player.getLocation().getDirection();

        float yaw = player.getLocation().getYaw();
        double radians = Math.toRadians(yaw);
        Vector horizontalDirection = new Vector(-Math.sin(radians), 0, Math.cos(radians)).normalize();

        Vector recoil = horizontalDirection.multiply(-1);
        player.setVelocity(recoil);

        world.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

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
                    List<Block> blocksToBreak = BlockUtils.getBlocksInSpherePattern(targetBlock, 9, 7, 9);
                    blocksToBreak.removeIf(Block::isEmpty);
                    breakBlocks(player, blocksToBreak, pickaxeData);

                    Location targetBlockLocation = targetBlock.getLocation();

                    world.playSound(targetBlockLocation, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 10.0f, 1.0f);
                    world.playSound(targetBlockLocation, Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 1.0f);
                    world.spawnParticle(Particle.POOF, laserLocation, 350, 2, 2, 2, 0.35);

                    this.cancel();
                }

                ticks++;
                if (ticks >= 64) this.cancel();
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
