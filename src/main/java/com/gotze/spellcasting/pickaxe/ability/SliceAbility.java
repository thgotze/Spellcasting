package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

public class SliceAbility extends AbstractAbility {
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

    public SliceAbility(Player player) {
        super(player);
    }

    @Override
    public void activate() {
        final Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2.3f));

        ItemDisplay[] itemDisplays = new ItemDisplay[6];
        for (int i = 0; i < 6; i++) {
            itemDisplays[i] = (ItemDisplay) world.spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
            itemDisplays[i].setBrightness(new Display.Brightness(15, 15));
            itemDisplays[i].setTransformationMatrix(new Matrix4f()
                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i]))
                    .rotateX((float) Math.toRadians(90f))
                    .scale(5f, 5f, 0.1f)
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

                            BlockUtils.breakBlocksInLineOfSight(player, i, 4.5);
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
                }
            }
        }.runTaskTimer(Spellcasting.INSTANCE, 0, 1);
    }
}