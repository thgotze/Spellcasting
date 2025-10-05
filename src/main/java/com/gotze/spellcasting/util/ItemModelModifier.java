package com.gotze.spellcasting.util;

import com.gotze.spellcasting.Spellcasting;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public interface ItemModelModifier {
    default void modifyItemModelTemporarily(ItemStack itemStack, Material newType, long durationTicks, Runnable onComplete) {
        var originalModel = itemStack.getData(DataComponentTypes.ITEM_MODEL);

        itemStack.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(newType.name().toLowerCase()));

        // Schedule automatic revert AND callback
        new BukkitRunnable() {
            @Override
            public void run() {
                itemStack.setData(DataComponentTypes.ITEM_MODEL, originalModel);

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), durationTicks);
    }
}