//package com.gotze.spellcasting.pickaxe.ability.todo;
//
//import com.destroystokyo.paper.ParticleBuilder;
//import com.gotze.spellcasting.data.PickaxeData;
//import com.gotze.spellcasting.pickaxe.ability.Ability;
//import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
//import org.bukkit.Color;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//import org.bukkit.util.RayTraceResult;
//import org.bukkit.util.Vector;
//
//import static net.kyori.adventure.text.Component.text;
//import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
//
//public class LaserAbility extends Ability implements BlockBreaker {
//    private static final ParticleBuilder LASER_PARTICLE = new ParticleBuilder(Particle.DUST)
//            .count(0)
//            .data(new Particle.DustOptions(Color.RED, 1.0f))
//            .offset(0, 0, 0)
//            .extra(0);
//
//    private boolean isActive = false;
//
//    public LaserAbility() {
//        super(AbilityType.LASER);
//    }
//
//    @Override
//    public void activateAbility(Player player, PickaxeData pickaxeData) {
//        if (this.isActive) return;
//        this.isActive = true;
//
//        player.swingMainHand();
//
//        // Perform rayTrace to find blocks within 20 block range
//        RayTraceResult rayTraceResult = player.rayTraceBlocks(20);
//
//        // Start from eye location and offset to the right
//        Location startLocation = player.getEyeLocation()
//                .add(player.getLocation().getDirection().clone()
//                        .crossProduct(new Vector(0, 1, 0))
//                        .normalize().multiply(player.getWidth() / 1.8));
//
//        Location targetLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().clone().multiply(20));
//
//        Vector laserDirection = targetLocation.toVector().subtract(startLocation.toVector()).normalize();
//
//        Location endLocation;
//        Block hitBlock = null;
//
//        if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
//            // Block was hit within 20 blocks
//            hitBlock = rayTraceResult.getHitBlock();
//            breakBlock(player, hitBlock, pickaxeData);
//
//            // Cast ray from our offset start position towards the target
//            RayTraceResult offsetRayTrace = player.getWorld().rayTraceBlocks(
//                    startLocation,
//                    laserDirection,
//                    20,
//                    org.bukkit.FluidCollisionMode.NEVER,
//                    true
//            );
//
//            if (offsetRayTrace != null) {
//                endLocation = offsetRayTrace.getHitPosition().toLocation(player.getWorld());
//            } else {
//                endLocation = rayTraceResult.getHitPosition().toLocation(player.getWorld());
//            }
//
//
//        } else {
//            // No block hit within 20 blocks, laser goes full distance
//            endLocation = targetLocation;
//        }
//
//        spawnLaserLine(startLocation, endLocation);
//
//        // While loop to shoot raycasts and break blocks 20 times
//        int shotCount = 0;
//        Location currentStartLocation = startLocation.clone();
//
//        while (shotCount < 20) {
//            RayTraceResult currentRayTrace = player.getWorld().rayTraceBlocks(
//                    currentStartLocation,
//                    laserDirection,
//                    20,
//                    org.bukkit.FluidCollisionMode.NEVER,
//                    true
//            );
//
//            if (currentRayTrace != null && currentRayTrace.getHitBlock() != null) {
//                Block blockToBreak = currentRayTrace.getHitBlock();
//
//                // Break the block
//                breakBlock(player, blockToBreak, pickaxeData);
//
//                // Update the start location to just past the broken block
//                Location hitLocation = currentRayTrace.getHitPosition().toLocation(player.getWorld());
//                currentStartLocation = hitLocation.add(player.getEyeLocation().getDirection().clone().multiply(0.1));
//
//                shotCount++;
//            } else {
//                // No more blocks to hit, break out of the loop
//                break;
//            }
//        }
//
//        this.isActive = false;
//    }
//
//    private void spawnLaserLine(Location startLocation, Location endLocation) {
//        double lineDistance = startLocation.distance(endLocation);
//        double particleSpacing = 0.05;
//        int particleCount = (int) (lineDistance / particleSpacing);
//        for (int i = 0; i <= particleCount; i++) {
//            LASER_PARTICLE.clone()
//                    .location(startLocation.clone()
//                            .add(endLocation.toVector().subtract(startLocation.toVector()).normalize().clone()
//                                    .multiply(i * particleSpacing)))
//                    .spawn();
//        }
//    }
//}
