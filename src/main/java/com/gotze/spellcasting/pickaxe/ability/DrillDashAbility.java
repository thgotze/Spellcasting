package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.ItemModelModifier;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class DrillDashAbility extends Ability implements BlockBreaker, ItemModelModifier {
    private static final float DASH_SPEED = 0.5f;

    private boolean isActive;

    public DrillDashAbility() {
        super(AbilityType.DRILL_DASH);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;
        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!", YELLOW)));

        final int DASH_DURATION_TICKS = 5 * getLevel() + 5; // 10, 15, 20, 25, 30

        modifyItemModelTemporarily(player.getInventory().getItemInMainHand(),
                Material.TRIDENT,
                DASH_DURATION_TICKS,
                player::updateInventory
        );

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
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
    }
}