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

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                Location laserLocation = startLocation.clone()
                        .add(lookingDirection.clone().multiply(ticks * 1.5));

//                if (ticks % 16 == 0 && ticks != 0) {
                world.spawnParticle(Particle.DUST, laserLocation, 1,
                        new Particle.DustOptions(Color.LIME, 2.0f));
//                }

                Block targetBlock = laserLocation.getBlock();
                if (!targetBlock.getType().isAir()) {
                    ArrayList<Block> blocksToBreak = new ArrayList<>();
                    blocksToBreak.add(targetBlock);

                    int x = targetBlock.getX();
                    int y = targetBlock.getY();
                    int z = targetBlock.getZ();

                    blocksToBreak.addAll(BlockUtils.getBlocksInSpherePattern(targetBlock, 4));
                    // Vertical blocks
//                    blocksToBreak.add(world.getBlockAt(x, y + 1, z));
//                    blocksToBreak.add(world.getBlockAt(x, y - 1, z));

                    // Horizontal blocks
                    switch (playerFacing) {
                        case NORTH, SOUTH -> {
                            blocksToBreak.add(world.getBlockAt(x + 1, y, z));
                            blocksToBreak.add(world.getBlockAt(x - 1, y, z));
                        }
                        case EAST, WEST -> {
                            blocksToBreak.add(world.getBlockAt(x, y, z + 1));
                            blocksToBreak.add(world.getBlockAt(x, y, z - 1));
                        }
                    }
                    int blocksBroken = 0;

                    for (Block block : blocksToBreak) {
                        if (block.getType().isAir() || block.getType() == Material.BEDROCK) continue;
                        block.breakNaturally(true);
                        blocksBroken++;
                    }
                    player.sendMessage(String.valueOf(blocksBroken));
                    this.cancel();
                }


                ticks++;
                if (ticks >= 64) this.cancel();
            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0, 1);
    }
}