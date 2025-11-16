package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.util.LifecycleManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MachineManager implements Listener, LifecycleManager {
    private static final Map<Location, Machine> machines = new HashMap<>();
    private static BukkitTask tickTask;

    @Override
    public void start() {
        tickTask = Bukkit.getScheduler().runTaskTimer(
                Spellcasting.getPlugin(),
                () -> machines.values().forEach(Machine::tick),
                0L, 1L);
    }

    @Override
    public void stop() {
        if (tickTask != null) {
            tickTask.cancel();
        }
    }

    public Machine getMachine(Location location) {
        return machines.get(location);
    }

    public void addMachine(Machine machine) {
        machines.put(machine.getLocation(), machine);
    }

    public void removeMachine(Location location) {
        machines.remove(location);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlaceMachine(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();

        NamespacedKey machineKey = new NamespacedKey(Spellcasting.getPlugin(), "machine");
        String machineValue = itemInHand
                .getPersistentDataContainer()
                .get(machineKey, PersistentDataType.STRING);
        if (machineValue == null) return;

        Machine.MachineType machineType = Machine.MachineType.valueOf(machineValue.toUpperCase());

        Player player = event.getPlayer();
        Location blockLocation = event.getBlockPlaced().getLocation();

        Machine newMachine;
        try {
            newMachine = machineType.getMachineClass()
                    .getConstructor(Location.class, Player.class)
                    .newInstance(blockLocation, player);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        addMachine(newMachine);
        player.spawnParticle(Particle.HAPPY_VILLAGER, blockLocation.toCenterLocation(), 10, 0.5, 0.5, 0.5);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMachineRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Machine machine = getMachine(block.getLocation());
        if (machine == null) return;

        event.setCancelled(true);
        machine.open(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBreakMachine(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        Machine machine = getMachine(blockLocation);
        if (machine == null) return;

        event.setDropItems(false);
        World world = blockLocation.getWorld();
        Location centerLocation = blockLocation.toCenterLocation();

        // Drop the machine item itself
        Machine.MachineType type = machine.getMachineType();
        world.dropItemNaturally(centerLocation, type.getMachineItem());

        // Drop input/output items
        ItemStack inputItem = machine.getInputItem();
        ItemStack outputItem = machine.getOutputItem();
        if (inputItem != null) {
            world.dropItemNaturally(centerLocation, inputItem);
        }
        if (outputItem != null) {
            world.dropItemNaturally(centerLocation, outputItem);
        }

        removeMachine(blockLocation);
    }

//    private static final EnumSet<InventoryAction> ALLOWED_OUTPUT_ACTIONS = EnumSet.of(
//            InventoryAction.PICKUP_ALL,
//            InventoryAction.PICKUP_ONE,
//            InventoryAction.PICKUP_SOME,
//            InventoryAction.PICKUP_HALF,
//            InventoryAction.MOVE_TO_OTHER_INVENTORY,
//            InventoryAction.DROP_ALL_SLOT,
//            InventoryAction.DROP_ONE_SLOT,
//            InventoryAction.PICKUP_ALL_INTO_BUNDLE,
//            InventoryAction.PICKUP_SOME_INTO_BUNDLE
//    );
//
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (!(event.getInventory().getHolder() instanceof Crusher crusher)) return;
//
//        // ---------------
//        // Crusher Inventory
//        // ---------------
//        if (event.getClickedInventory() == event.getView().getTopInventory()) {
//            int slot = event.getSlot();
//
//            // Allow interaction with the input slot
//            if (slot == 11) return;
//
//            // Allow interaction with the output slot if the action is allowed
//            if (slot == 15 && ALLOWED_OUTPUT_ACTIONS.contains(event.getAction())) return;
//
//            // Cancel event for all other slots and output slot if the action wasn't allowed
//            event.setCancelled(true);
//            return;
//        }
//
//        // ---------------
//        // Player Inventory
//        // ---------------
//        ClickType clickType = event.getClick();
//        if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT) return;
//
//        ItemStack clickedItem = event.getCurrentItem();
//        if (clickedItem == null) return;
//
//        ItemStack inputItem = crusher.getInputItem();
//        if (inputItem == null) return;
//
//        if (clickedItem.isSimilar(inputItem)) {
//            int clickedItemAmount = clickedItem.getAmount();
//            int inputItemAmount = inputItem.getAmount();
//
//            if (clickedItemAmount + inputItemAmount <= 64) return;
//
//            int transferred = 64 - inputItemAmount;
//            inputItem.setAmount(64);
//            clickedItem.subtract(transferred);
//        }
//        event.setCancelled(true);
//    }
}