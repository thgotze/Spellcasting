package com.gotze.spellcasting.pickaxe.ability.todo;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class SlamAbility extends Ability implements BlockBreaker {
    private static final double JUMP_STRENGTH = 1.2; // Upward velocity multiplier
    private static final int MAX_FALL_TIME = 100; // 5 seconds max fall time (safety)

    private boolean isActive;

    public SlamAbility() {
        super(AbilityType.SLAM);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (isActive) return;
        isActive = true;
        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!", YELLOW)));

        // Launch player upward
        double jumpHeight = JUMP_STRENGTH + (getLevel() * 0.3); // Higher levels = higher jump
        Vector jumpVelocity = new Vector(0, jumpHeight, 0);
        player.setVelocity(player.getVelocity().add(jumpVelocity));

        // Track when player lands
        new BukkitRunnable() {
            boolean hasLeftGround = false;
            int ticksInAir = 0;

            @Override
            public void run() {
                ticksInAir++;

                // First, wait for player to leave the ground
                if (!hasLeftGround && !player.isOnGround()) {
                    hasLeftGround = true;
                }

                // Once they've left the ground, check if they've landed
                if (hasLeftGround && player.isOnGround()) {
                    // Player has landed - perform slam effect
                    performSlamEffect(player, pickaxeData);
                    isActive = false;
                    cancel();
                    return;
                }

                // Safety timeout to prevent infinite loops
                if (ticksInAir >= MAX_FALL_TIME) {
                    isActive = false;
                    cancel();
                }
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 1L, 1L);
    }

    private void performSlamEffect(Player player, PickaxeData pickaxeData) {
        // Get the block the player is standing on
        Block centerBlock = player.getLocation().subtract(0, 1, 0).getBlock();
        
        // Calculate radius based on level (level 1 = 3x3, level 2 = 5x5, etc.)
        int radius = 1 + getLevel();
        int size = radius * 2 + 1;
        
        // Break blocks in a square pattern around the impact point
        List<Block> blocksToBreak = BlockUtils.getBlocksInSquarePattern(
            centerBlock, 
            size,  // width
            2,     // height (2 blocks down from impact)
            size   // depth
        );
        blocksToBreak.removeIf(blockToBreak -> blockToBreak.getType().isAir());
        
        // Break the blocks
        breakBlocks(player, blocksToBreak, pickaxeData);
        
        // Optional: Add visual/sound effect here if desired
        player.sendActionBar(text("SLAM! ", YELLOW).append(text("Broke " + blocksToBreak.size() + " blocks!")));
    }
}