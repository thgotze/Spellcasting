package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.gotze.spellcasting.Spellcasting.plugin;

public class DrillDashAbility extends Ability implements BlockBreaker {
    private static final float DASH_SPEED = 0.5f;

    private boolean isActive;

    public DrillDashAbility() {
        super(AbilityType.DRILL_DASH);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;

        final int DASH_DURATION_TICKS = 5 + getLevel() * 5; // 10, 15, 20, 25, 30

        player.setGravity(false);
        player.setRiptiding(true);

        new BukkitRunnable() {
            final Vector startingDirection = player.getLocation().getDirection();
            int ticks = 0;

            @Override
            public void run() {
                Block centerBlock = player.getLocation().add(startingDirection).getBlock();
                List<Block> blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 3, 3);
                breakBlocks(player, blocksToBreak, pickaxeData);

                player.setVelocity(startingDirection.clone().multiply(DASH_SPEED));

                ticks++;
                if (ticks >= DASH_DURATION_TICKS) {
                    player.setGravity(true);
                    player.setRiptiding(false);
                    isActive = false;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
