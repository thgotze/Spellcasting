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

        // Set the initial item model component
        meta.setItemModel(NamespacedKey.minecraft("crescent_frontside00"));
        particleItem.setItemMeta(meta);

        // Spawn the ItemDisplay
        ItemDisplay display = location.getWorld().spawn(spawnLocation, ItemDisplay.class);
        display.setItemStack(particleItem);
        display.setBrightness(new Display.Brightness(15, 15));

        display.setTransformationMatrix(new Matrix4f()
                .rotateZ((float) Math.PI / 2.5f)
                .rotateY((float) Math.PI / 2)
                .scale(1, 1, 0.01f)
        );

        new BukkitRunnable() {
            private int ticks = 0;
            private final int frames = 12;
            private final int loops = 20;
            private final int totalTicks = frames * loops;

            @Override
            public void run() {
                if (ticks >= totalTicks) {
                    this.cancel();
                    return;
                }

                int frame = ticks % frames;
                String modelName = String.format("crescent_frontside%02d", frame);

                ItemStack newItem = new ItemStack(Material.PAPER);
                ItemMeta newMeta = newItem.getItemMeta();
                newMeta.setItemModel(NamespacedKey.minecraft(modelName));
                newItem.setItemMeta(newMeta);

                display.setItemStack(newItem);
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    protected void remove() {

    }
}
