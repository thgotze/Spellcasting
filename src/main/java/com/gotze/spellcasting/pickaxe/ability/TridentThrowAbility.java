package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TridentThrowAbility extends Ability implements BlockBreaker {

    private boolean isActive;

    public TridentThrowAbility() {
        super(AbilityType.TRIDENT_THROW);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;

        Trident trident = player.launchProjectile(
                Trident.class,
                player.getLocation().getDirection(),
                t -> {
            t.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            t.setDamage(0);
            t.setGlowing(true);
//            t.setLoyaltyLevel(3);
        });


//        trident.setItemStack(PlayerPickaxeService.getPlayerPickaxe(player));
//        Vector halfVelocity = trident.getVelocity().divide(new Vector(2, 2, 2));
//        trident.setVelocity(halfVelocity);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (trident.isDead() || !trident.isValid()) {
                    isActive = false;
                    this.cancel();
                    return;
                }

                // If it gets stuck despite our efforts, remove it so it doesn't hang in the air/fall
                if (trident.isInBlock()) {
                    trident.remove();
                    isActive = false;
                    this.cancel();
                    return;
                }

                List<Block> blocksToBreak = new ArrayList<>();
                World world = trident.getWorld();
                Location location = trident.getLocation();
                Vector velocity = trident.getVelocity();

                // 1. Scan current location (3x3)
                addBlocksInRadius(location, world, blocksToBreak);

                // 2. Look ahead! RayTrace along the velocity vector to catch blocks BEFORE collision
                if (velocity.lengthSquared() > 0.01) {
                    // Check distance equal to speed + small buffer
                    RayTraceResult hit = world.rayTraceBlocks(
                            location,
                            velocity,
                            velocity.length() + 1.0,
                            FluidCollisionMode.NEVER,
                            true
                    );

                    if (hit != null && hit.getHitBlock() != null) {
                        // Break the block we are about to hit
                        addBlocksInRadius(hit.getHitBlock().getLocation(), world, blocksToBreak);
                    }
                }
                breakBlocks(player, blocksToBreak, pickaxeData);
            }

            private void addBlocksInRadius(Location center, World world, List<Block> blocks) {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Block block = world.getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                            if (block.getType() != Material.AIR && !blocks.contains(block)) {
                                blocks.add(block);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(this.getClass()), 1L, 1L);
    }
}
