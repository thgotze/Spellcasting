package com.gotze.spellcasting.pickaxe.capability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemModelManager implements Listener {

    private static final Map<Player, ActiveModification> activeModifications = new HashMap<>();

    private record ActiveModification(
            ItemStack itemStack,
            Key originalItemModel,
            BukkitTask revertTask,
            Runnable onComplete
    ) {}

    /**
     * Check if a player has an active modification
     */
    public static boolean hasActiveModification(Player player) {
        return activeModifications.containsKey(player);
    }

    /**
     * Temporarily modify an item's model
     * <p>
     * <b>Checking if the player has an active modification
     * via {@link #hasActiveModification(Player)} should
     * always be done before calling this method!
     */
    public static void modifyItemModelTemporarily(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull Material newType,
                                           long durationTicks, @Nullable Runnable onComplete) {
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        Key originalModel = pickaxeData.getPickaxeMaterial().getPickaxeType().key();
        itemStack.setData(DataComponentTypes.ITEM_MODEL, newType.key());

        BukkitTask revertTask = Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
            revertActiveModification(player);
            if (onComplete != null) {
                onComplete.run();
            }
        }, durationTicks);

        // Store the active modification
        ActiveModification activeModification = new ActiveModification(itemStack, originalModel, revertTask, onComplete);
        activeModifications.put(player, activeModification);
    }

    /**
     * Revert a player's item model modification
     */
    private static void revertActiveModification(Player player) {
        ActiveModification modification = activeModifications.remove(player);
        if (modification != null) {
            modification.itemStack().setData(DataComponentTypes.ITEM_MODEL, modification.originalItemModel());
//            player.updateInventory();
        }
    }

    /**
     * Cancel a modification without running the onComplete callback
     */
    private static void cancelActiveModification(Player player) {
        ActiveModification modification = activeModifications.remove(player);
        if (modification != null) {
            modification.itemStack().setData(DataComponentTypes.ITEM_MODEL, modification.originalItemModel());
            if (modification.revertTask() != null && !modification.revertTask().isCancelled()) {
                modification.revertTask().cancel();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitCancelActiveModification(PlayerQuitEvent event) {
        cancelActiveModification(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEnsureCorrectPickaxeModel(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromInventory(player, false);
        if (pickaxe == null) return;

        Key currentModel = pickaxe.getData(DataComponentTypes.ITEM_MODEL);

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        Key correctModel = pickaxeData.getPickaxeMaterial().getPickaxeType().key();

        if (!correctModel.equals(currentModel)) {
            pickaxe.setData(DataComponentTypes.ITEM_MODEL, correctModel);
        }
    }
}