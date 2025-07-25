package com.gotze.magicParticles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;

public class LaserSpell extends AbstractSpell {

    public LaserSpell(JavaPlugin plugin, Location location, Player player) {
        super(plugin, location, player);
        spawn();
    }

    @Override
    protected void spawn() {

        Location spawnLocation = player.getLocation().getWorld().getBlockAt(player.getLocation()).getLocation();

        ItemStack particleItem = new ItemStack(Material.PAPER);
        ItemMeta meta = particleItem.getItemMeta();

        // Set the item model component directly
        meta.setItemModel(NamespacedKey.minecraft("particle"));
        particleItem.setItemMeta(meta);

        // Spawn the ItemDisplay
        ItemDisplay display = location.getWorld().spawn(spawnLocation, ItemDisplay.class);
        display.setItemStack(particleItem);


        display.setBrightness(new Display.Brightness(15, 15));

        display.setTransformationMatrix(new Matrix4f()
                .rotateZ((float) Math.PI / 2.5f)
                .rotateY((float) Math.PI / 2)
                .scale(1, 1, 1)
        );

        new BukkitRunnable() {
            private int stage = 0;
            private final Material[] swords = {
                    Material.WOODEN_SWORD,
                    Material.STONE_SWORD,
                    Material.IRON_SWORD,
                    Material.GOLDEN_SWORD,
                    Material.DIAMOND_SWORD,
                    Material.NETHERITE_SWORD
            };

            @Override
            public void run() {
                this.cancel();
                return;
            }
        }.runTaskTimer(plugin, 0, 20);
    }


//        EntityType.UNKNOWN

//        ItemDisplay itemDisplay = player.getWorld().spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY, true)

//        TextDisplayBuilder baseDisplay = new TextDisplayBuilder(spawnLocation);
//
//        TextDisplay red = baseDisplay.copy()
//                .scale(2,1,1)
//                .translate(0, 0,-0.125f)
//                .build();
//        red.setBackgroundColor(Color.fromARGB(255,255, 0, 0));
//        red.text(Component.text(" "));

//        TextDisplay redB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(180)
//                .translate(0, 0,-0.125f)
//                .build();
//        redB.setBackgroundColor(Color.fromARGB(255,255, 0, 0));
//        redB.text(Component.text(" "));
//
//
//
//
//        TextDisplay green = baseDisplay.copy()
//                .scale(2,1,1)
//                .translate(0,0,0.125f)
//                .build();
//        green.setBackgroundColor(Color.fromARGB(255,0, 255, 0));
//        green.text(Component.text(" "));
//        TextDisplay greenB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(180)
//                .translate(0,0,0.125f)
//                .build();
//        greenB.setBackgroundColor(Color.fromARGB(255,0, 255, 0));
//        greenB.text(Component.text(" "));
//
//
//
//        TextDisplay blue = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateX(90)
//                .translate(0,0.25f,-0.125f)
//                .build();
//        blue.setBackgroundColor(Color.fromARGB(255,0, 0, 255));
//        blue.text(Component.text(" "));
//        TextDisplay blueB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(180)
//                .rotateX(90)
//                .translate(0,0.25f,-0.125f)
//                .build();
//        blueB.setBackgroundColor(Color.fromARGB(255,0, 0, 255));
//        blueB.text(Component.text(" "));
//
//
//
//        TextDisplay black = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateX(90)
//                .translate(0,0,-0.125f)
//                .build();
//        black.setBackgroundColor(Color.fromARGB(255,0, 0, 0));
//        black.text(Component.text(" "));
//
//        TextDisplay blackB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateX(90)
//                .rotateZ(180)
//                .translate(0,0,-0.125f)
//                .build();
//        blackB.setBackgroundColor(Color.fromARGB(255,0, 0, 0));
//        blackB.text(Component.text(" "));
//
//
//
//        TextDisplay white = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(90)
//                .translate(0.125f,0f,0)
//                .build();
//        white.setBackgroundColor(Color.fromARGB(255,255, 255, 255));
//        white.text(Component.text(" "));
//
//        TextDisplay whiteB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(270)
//                .translate( 0.125f,0f,0)
//                .build();
//        whiteB.setBackgroundColor(Color.fromARGB(255,255, 255, 255));
//        whiteB.text(Component.text(" "));
//
//
//        TextDisplay gray = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(90)
//                .translate(-0.125f,0f,0)
//                .build();
//        gray.setBackgroundColor(Color.fromARGB(255,125, 125, 125));
//        gray.text(Component.text(" "));
//
//        TextDisplay grayB = baseDisplay.copy()
//                .scale(2,1,1)
//                .rotateY(270)
//                .translate(-0.125f,0f,0)
//                .build();
//        grayB.setBackgroundColor(Color.fromARGB(255,125, 125, 125));
//        grayB.text(Component.text(" "));



    @Override
    protected void remove() {

    }
}
