package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.feature.mines.MineManager;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.gotze.spellcasting.Spellcasting.plugin;

public class WindscatterAbility extends Ability {

    public WindscatterAbility() {
        super(AbilityType.WINDSCATTER);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        player.playSound(player, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1f, 1f);
        player.swingMainHand();

        int level = getLevel();

        spawnAndWatch(player, 0, 0); // center
        spawnAndWatch(player, -25, 0); // left
        spawnAndWatch(player, 25, 0); // right

        if (level >= 2) {
            spawnAndWatch(player, 0, -25); // up
            spawnAndWatch(player, 0, 25);  // down
        }

        if (level >= 3) {
            spawnAndWatch(player, 25, 25);   // bottom-right
            spawnAndWatch(player, -25, 25);  // bottom-left
        }

        if (level >= 4) {
            spawnAndWatch(player, 25, -25);  // top-right
            spawnAndWatch(player, -25, -25); // top-left
        }

        if (level >= 5) {
            spawnAndWatch(player, -50, 0); // left-left
            spawnAndWatch(player, 50, 0); // right-right
        }
    }

    private void spawnAndWatch(Player player, float yawOffsetDegrees, float pitchOffsetDegrees) {
        Location base = player.getEyeLocation();
        Location rotated = base.clone();

        rotated.setYaw(base.getYaw() + yawOffsetDegrees);
        rotated.setPitch(clampPitch(base.getPitch() + pitchOffsetDegrees));

        Vector dir = rotated.getDirection().normalize();

        WindCharge windCharge = player.launchProjectile(WindCharge.class);

        double speed = windCharge.getVelocity().length();
        windCharge.setVelocity(dir.multiply(speed));

        watchWindCharge(windCharge);
    }

    private float clampPitch(float pitch) {
        if (pitch > 89f) return 89f;
        if (pitch < -89f) return -89f;
        return pitch;
    }

    private void watchWindCharge(WindCharge windCharge) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!windCharge.isValid()) {
                    List<Block> blocksInSquarePattern =
                            BlockUtils.getBlocksInSquarePattern(windCharge.getLocation().getBlock(), 3, 3, 3);
                    for (Block block : blocksInSquarePattern) {
                        if (!MineManager.isInAnyMine(block)) continue;
                        if (!BlockCategories.FILLER_BLOCKS.contains(block.getType())) continue;

                        block.setType(Material.AIR);
                    }
                    cancel();
                    return;

                } else if (ticks >= 100) {
                    cancel();
                    return;
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
