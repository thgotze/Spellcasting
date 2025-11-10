package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class DrillDashAbility extends Ability implements BlockBreaker {
    private static final float DASH_SPEED = 0.5f;
    private static final int DASH_LENGTH_TICKS = 10;

    private boolean isActive;

    public DrillDashAbility() {
        super(AbilityType.DRILL_DASH);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;
        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!").color(YELLOW)));

        ItemDisplay blockDisplay = player.getWorld().spawn(
                player.getEyeLocation().subtract(0, 0.5, 0),
                ItemDisplay.class);
        blockDisplay.setItemStack(ItemStack.of(Material.NETHERITE_PICKAXE));
        blockDisplay.setBrightness(new Display.Brightness(15, 15));
        blockDisplay.setPersistent(false);
        blockDisplay.setTransformationMatrix(new Matrix4f()
                .rotateZ((float) Math.toRadians(90f))
                .rotateX((float) Math.toRadians(90f))
                .translate(-1.5f, 1, -1)
                .scale(2f, 5f, 2f)
        );
        player.addPassenger(blockDisplay);
        player.setGravity(false);
        player.setRiptiding(true);

        new BukkitRunnable() {
            final Vector startingDirection = player.getLocation().getDirection();
            int ticks = 0;

            @Override
            public void run() {
                Block centerBlock = player.getLocation().add(startingDirection).getBlock();
                List<Block> blocksToBreak = BlockUtils.getBlocksInSquarePattern(centerBlock, 3, 3, 3);
                breakBlocks(player, blocksToBreak, pickaxeData, false);

                player.setVelocity(startingDirection.clone().multiply(DASH_SPEED));

                ticks++;
                if (ticks >= DASH_LENGTH_TICKS) {
                    blockDisplay.remove();
                    player.setGravity(true);
                    player.setRiptiding(false);
                    isActive = false;
                    cancel();
                }
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
    }
}