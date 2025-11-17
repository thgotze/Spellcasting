package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class SliceAbility extends Ability implements BlockBreaker {
    private final int[] START_DELAYS = {0, 6, 38, 44, 76, 82};
    private final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};
    private final String[] ANTI_CLOCKWISE_SPRITES = {
            "crescent_frontside00", "crescent_frontside01", "crescent_frontside02", "crescent_frontside03",
            "crescent_frontside04", "crescent_frontside05", "crescent_frontside06", "crescent_frontside07",
            "crescent_frontside08", "crescent_frontside09", "crescent_frontside10", "crescent_frontside11"
    };
    private final String[] CLOCKWISE_SPRITES = {
            "crescent_backside00", "crescent_backside01", "crescent_backside02", "crescent_backside03",
            "crescent_backside04", "crescent_backside05", "crescent_backside06", "crescent_backside07",
            "crescent_backside08", "crescent_backside09", "crescent_backside10", "crescent_backside11"
    };

    boolean isActive;

    public SliceAbility() {
        super(AbilityType.SLICE);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        this.isActive = true;
        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!", YELLOW)));

        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2.3f));
        World world = player.getWorld();

        ItemDisplay[] itemDisplays = new ItemDisplay[6];
        for (int i = 0; i < 6; i++) {
            itemDisplays[i] = (ItemDisplay) world.spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
            itemDisplays[i].setBrightness(new Display.Brightness(15, 15));
            itemDisplays[i].setPersistent(false);
            itemDisplays[i].setTransformationMatrix(new Matrix4f()
                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i]))
                    .rotateX((float) Math.toRadians(90f))
                    .scale(5f, 5f, 1f)
            );
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    if (ticks < START_DELAYS[i]) break;

                    int spriteIndex = (ticks - START_DELAYS[i]) % 12;

                    if (spriteIndex >= 4 && spriteIndex <= 6) {
                        if (spriteIndex == 4) {
                            itemDisplays[i].setItemStack(ItemStack.of(Material.PAPER));

                            itemDisplays[i].teleport(player.getEyeLocation()
                                    .add(player.getLocation().getDirection().multiply(2.3f)));

                            player.playSound(player, Sound.ITEM_TRIDENT_THROW, 0.20f, 1.35f);

                            List<Block> blocksToBreak = getBlocksInLineOfSight(i, player);
                            breakBlocks(player, blocksToBreak, pickaxeData, false);
                        }

                        String spriteName = (i % 2 == 0) ? ANTI_CLOCKWISE_SPRITES[spriteIndex] : CLOCKWISE_SPRITES[spriteIndex];

                        ItemStack itemStack = itemDisplays[i].getItemStack();
                        itemStack.editMeta(itemMeta -> itemMeta.setItemModel(NamespacedKey.minecraft(spriteName)));
                        itemDisplays[i].setItemStack(itemStack);

                    } else if (!itemDisplays[i].getItemStack().isEmpty()) {
                        itemDisplays[i].setItemStack(ItemStack.empty());
                    }
                }

                ticks++;
                if (ticks >= 12 * 20) {
                    this.cancel();
                    for (ItemDisplay display : itemDisplays) display.remove();
                    isActive = false;
                    player.sendActionBar(text("Slice ability expired", RED));
                }
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 0, 1);
    }

    private List<Block> getBlocksInLineOfSight(int displayIndex, Player player) {
        double reachDistance = 4.5;
        BlockFace playerFacing = player.getFacing();
        Location eyeLocation = player.getEyeLocation();

        List<Block> blocksInLineOfSight = player.getLineOfSight(null, 5);

        List<Block> blocksToBreak = new ArrayList<>(blocksInLineOfSight);
        for (Block block : blocksInLineOfSight) {
            Location blockLocation = block.getLocation();

            double nearestX = Math.max(blockLocation.getX(), Math.min(eyeLocation.getX(), blockLocation.getX() + 1));
            double nearestY = Math.max(blockLocation.getY(), Math.min(eyeLocation.getY(), blockLocation.getY() + 1));
            double nearestZ = Math.max(blockLocation.getZ(), Math.min(eyeLocation.getZ(), blockLocation.getZ() + 1));

            double distanceToNearestPoint = eyeLocation.distanceSquared(new Location(blockLocation.getWorld(), nearestX, nearestY, nearestZ));
            if (distanceToNearestPoint > reachDistance * reachDistance) continue;

            blocksToBreak.addAll(switch (displayIndex) {
                case 0, 1 -> BlockUtils.getVerticalBlocks(block);
                case 2 -> BlockUtils.getPositiveDiagonalBlocks(block, playerFacing, 1);
                case 3 -> BlockUtils.getNegativeDiagonalBlocks(block, playerFacing, 1);
                case 4, 5 -> BlockUtils.getHorizontalBlocks(block, playerFacing);
                default -> throw new IllegalStateException("Unexpected slice ability display index value: " + displayIndex);
            });
        }
        return blocksToBreak;
    }
}