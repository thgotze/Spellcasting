package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.Spellcasting;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface ItemModelModifier {
    default void modifyItemModelTemporarily(ItemStack itemStack, Material newType, long durationTicks, Runnable onComplete) {
        var originalModel = itemStack.getData(DataComponentTypes.ITEM_MODEL);

        itemStack.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.minecraft(newType.name().toLowerCase()));
        // Schedule automatic revert AND callback
        Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
            itemStack.setData(DataComponentTypes.ITEM_MODEL, originalModel);

            if (onComplete != null) {
                onComplete.run();
            }
        }, durationTicks);
    }
}