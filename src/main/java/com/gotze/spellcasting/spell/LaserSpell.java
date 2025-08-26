package com.gotze.spellcasting.spell;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class LaserSpell extends Spell {

    public LaserSpell(Player player) {
        super.player = player;
        super.spellName = "Laser";
        super.world = player.getWorld();
    }

    @Override
    public void cast() {
        final Location startLocation = player.getLocation();
        final Vector lookingDirection = player.getLocation().getDirection();
        final BlockFace playerFacing = player.getFacing();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                Location laserLocation = startLocation.clone().add(lookingDirection.clone().multiply(ticks));

                world.spawnParticle(Particle.DUST, laserLocation, 1, new Particle.DustOptions(Color.LIME, 5.0f));

                ArrayList<Block> blocksToBreak = new ArrayList<>();

                Block targetBlock = player.getTargetBlockExact(10);

                if (targetBlock != null) {
                    blocksToBreak.add(targetBlock);

                    int x = targetBlock.getX();
                    int y = targetBlock.getY();
                    int z = targetBlock.getZ();

                    blocksToBreak.add(world.getBlockAt(x, y + 1, z));
                    blocksToBreak.add(world.getBlockAt(x, y - 1, z));

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

                    for (Block block : blocksToBreak) {
                        if (block.getType().isAir() || block.getType() == Material.BEDROCK) continue;
                        block.breakNaturally(true);
                    }
                }

                ticks++;
                if (ticks >= 60) this.cancel();
            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0, 1);
    }
}