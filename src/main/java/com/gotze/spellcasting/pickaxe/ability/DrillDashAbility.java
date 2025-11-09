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

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class DrillDashAbility extends Ability implements BlockBreaker {

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
        blockDisplay.setBrightness(new Display.Brightness(15, 15));
        blockDisplay.setPersistent(false);
        blockDisplay.setTransformationMatrix(new Matrix4f()
                .rotateZ((float) Math.toRadians(90f))
                .rotateX((float) Math.toRadians(90f))
                .translate(-1.5f, 1, -1)
                .scale(2f, 5f, 2f)
        );
        player.addPassenger(blockDisplay);
        player.setRiptiding(true);
        blockDisplay.setItemStack(ItemStack.of(Material.NETHERITE_PICKAXE));

        Vector startingDirection = player.getLocation().getDirection().normalize();
        player.setGravity(false);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks == 10) {
                    player.setGravity(true);
                    player.setRiptiding(false);
                    blockDisplay.remove();
                    cancel();
                    isActive = false;

                } else if (ticks < 10) {
                    Block block = player.getLocation()
                            .add(startingDirection)
                            .getBlock();

                    breakBlocks(player,
                            BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3),
                            pickaxeData,
                            false
                    );

                    double speed = 0.5f;
                    player.setVelocity(startingDirection.clone().multiply(speed));
                }
                ticks++;
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
    }
}