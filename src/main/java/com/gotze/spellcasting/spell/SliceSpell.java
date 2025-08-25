package com.gotze.spellcasting.spell;

import com.gotze.spellcasting.Main;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

public class SliceSpell extends Spell {

    private static final String SPELL_NAME = "Slice";

    private static final int[] START_DELAYS = {0, 42, 74, 116, 148, 190};
    private static final float[] DISPLAY_ROTATIONS = {-75f, 75f, -45f, 45f, -15f, 15f};

    private static final String[] ANTI_CLOCKWISE_SPRITES = {
//            "crescent_frontside00", "crescent_frontside01", "crescent_frontside02", "crescent_frontside03",
//            "crescent_frontside04", "crescent_frontside05", "crescent_frontside06", "crescent_frontside07",
//            "crescent_frontside08", "crescent_frontside09", "crescent_frontside10", "crescent_frontside11"
            "pixel", "pixel", "pixel", "pixel",
            "pixel", "pixel", "pixel", "pixel",
            "pixel", "pixel", "pixel", "pixel"
    };

    private static final String[] CLOCKWISE_SPRITES = {
//            "crescent_backside00", "crescent_backside01", "crescent_backside02", "crescent_backside03",
//            "crescent_backside04", "crescent_backside05", "crescent_backside06", "crescent_backside07",
//            "crescent_backside08", "crescent_backside09", "crescent_backside10", "crescent_backside11"
            "pixel", "pixel", "pixel", "pixel",
            "pixel", "pixel", "pixel", "pixel",
            "pixel", "pixel", "pixel", "pixel"
    };

    public SliceSpell(Player player) {
        super.player = player;
        super.spellName = SPELL_NAME;
    }

    @Override
    public void cast() {
        ItemDisplay[] itemDisplays = new ItemDisplay[6];

        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2.3f));

        for (int i = 0; i < 6; i++) {
            itemDisplays[i] = (ItemDisplay) player.getWorld().spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
            itemDisplays[i].setBrightness(new Display.Brightness(15, 15));

            itemDisplays[i].setTransformationMatrix(new Matrix4f()
                    .rotateZ((float) Math.toRadians(DISPLAY_ROTATIONS[i]))
                    .rotateX((float) Math.toRadians(90f))
                    .scale(5f, 5f, 0.5f)
            );
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    if (ticks < START_DELAYS[i]) break;

                    int spriteIndex = (ticks - START_DELAYS[i]) % 12;

                    // Every tick - Update sprite
                    String spriteName = (i % 2 == 0)
                            ? ANTI_CLOCKWISE_SPRITES[spriteIndex]
                            : CLOCKWISE_SPRITES[spriteIndex];

                    ItemStack itemStack = new ItemStack(Material.PAPER);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setItemModel(NamespacedKey.minecraft(spriteName));
                    itemStack.setItemMeta(itemMeta);

                    itemDisplays[i].setItemStack(itemStack);

                    // Start of cycle - Play sound and teleport display
                    if (spriteIndex == 0) {
                        player.playSound(net.kyori.adventure.sound.Sound.sound(
                                Sound.ITEM_TRIDENT_THROW,
                                net.kyori.adventure.sound.Sound.Source.PLAYER,
                                0.20f,
                                1.35f
                        ));
                        itemDisplays[i].teleport(player.getEyeLocation()
                                .add(player.getLocation().getDirection().multiply(2.3f))
                        );


                    } else if (spriteIndex == 6) { // Middle of cycle - Break blocks
                        BlockUtils.breakBlocksInLineOfSight(player,i);
                    }
                }

                ticks++;

                if (ticks >= 12 * 20) {
                    for (ItemDisplay display : itemDisplays) {
                        display.remove();
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.INSTANCE, 0L, 1L);
    }
}