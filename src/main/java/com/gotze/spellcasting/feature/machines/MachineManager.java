package com.gotze.spellcasting.feature.machines;

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

import static com.gotze.spellcasting.Spellcasting.plugin;

public class MachineManager implements Listener {
    private static final Map<Location, Machine> machines = new HashMap<>();
    private static BukkitTask tickTask;

    public static void startTickingMachines() {
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin,
                () -> machines.values().forEach(Machine::tick),
                0L, 1L);
    }

    public static void stopTickingMachines() {
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

        NamespacedKey machineKey = new NamespacedKey(plugin, "machine");
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

        // Drop input and output items
        ItemStack inputItem = machine.getInputItem();
        if (inputItem != null) {
            world.dropItemNaturally(centerLocation, inputItem);
        }
        ItemStack outputItem = machine.getOutputItem();
        if (outputItem != null) {
            world.dropItemNaturally(centerLocation, outputItem);
        }

        removeMachine(blockLocation);
    }
}
