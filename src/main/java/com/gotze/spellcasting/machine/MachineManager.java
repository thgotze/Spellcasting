package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.machine.crusher.Crusher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MachineManager implements Listener {

    private final Map<Location, Machine> machines = new ConcurrentHashMap<>();

    public MachineManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                JavaPlugin.getPlugin(Spellcasting.class),
                () -> {
                    for (Machine machine : machines.values()) {
                        machine.tick();
                    }
                }, 0L, 1L);
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

    @EventHandler
    public void DEBUGConvertStoneCutterToCrusher(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.STONECUTTER) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        Location blockLocation = event.getBlock().getLocation();
        if (getMachine(blockLocation) != null) return;

        Crusher crusher = new Crusher(blockLocation, player);
        addMachine(crusher);
        event.setCancelled(true);

        player.spawnParticle(Particle.HAPPY_VILLAGER, blockLocation.toCenterLocation(), 10, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onPlaceCrusher(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand.getType() != Material.STONECUTTER) return;

        NamespacedKey crusherKey = new NamespacedKey(JavaPlugin.getPlugin(Spellcasting.class), "machine");
        String crusherValue = itemInHand.getPersistentDataContainer().get(crusherKey, PersistentDataType.STRING);
        if (crusherValue != null) {
            Player player = event.getPlayer();
            Location blockLocation = event.getBlockPlaced().getLocation();
            Crusher crusher = new Crusher(blockLocation, player);
            addMachine(crusher);
            player.spawnParticle(Particle.HAPPY_VILLAGER, blockLocation.toCenterLocation(), 10, 0.5, 0.5, 0.5);
        }
    }

    @EventHandler
    public void onBreakCrusher(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        Crusher crusher = (Crusher) getMachine(blockLocation);
        if (crusher == null) return;

        event.setDropItems(false);
        World world = blockLocation.getWorld();
        Location centerCrusherLocation = blockLocation.toCenterLocation();

        world.dropItemNaturally(centerCrusherLocation, crusher.toItemStack());

        ItemStack inputItem = crusher.getInputItem();

        if (inputItem != null) {
            world.dropItemNaturally(centerCrusherLocation, inputItem);
        }

        ItemStack outputItem = crusher.getOutputItem();
        if (outputItem != null) {
            world.dropItemNaturally(centerCrusherLocation, outputItem);
        }

        removeMachine(blockLocation);
    }

    @EventHandler
    public void onCrusherInWorldRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Crusher crusher = (Crusher) getMachine(block.getLocation());
        if (crusher == null) return;

        event.setCancelled(true);
        event.getPlayer().openInventory(crusher.getInventory());
    }

    private static final EnumSet<InventoryAction> ALLOWED_OUTPUT_ACTIONS = EnumSet.of(
            InventoryAction.PICKUP_ALL,
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_SOME,
            InventoryAction.PICKUP_HALF,
            InventoryAction.MOVE_TO_OTHER_INVENTORY,
            InventoryAction.DROP_ALL_SLOT,
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.PICKUP_ALL_INTO_BUNDLE,
            InventoryAction.PICKUP_SOME_INTO_BUNDLE
    );

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Crusher crusher)) return;

        // ---------------
        // Crusher Inventory
        // ---------------
        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            int slot = event.getSlot();

            // Allow interaction with the input slot
            if (slot == 11) return;

            // Allow interaction with the output slot if the action is allowed
            if (slot == 15 && ALLOWED_OUTPUT_ACTIONS.contains(event.getAction())) return;

            // Cancel event for all other slots and output slot if the action wasn't allowed
            event.setCancelled(true);
            return;
        }

        // ---------------
        // Player Inventory
        // ---------------
        ClickType clickType = event.getClick();
        if (clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        ItemStack inputItem = crusher.getInputItem();
        if (inputItem == null) return;

        if (clickedItem.isSimilar(inputItem)) {
            int clickedItemAmount = clickedItem.getAmount();
            int inputItemAmount = inputItem.getAmount();

            if (clickedItemAmount + inputItemAmount <= 64) return;

            int transferred = 64 - inputItemAmount;
            inputItem.setAmount(64);
            clickedItem.subtract(transferred);
        }
        event.setCancelled(true);
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof Crusher)) return;
        // Cancel event if player tries to drag an item to the output slot (15)
        if (event.getRawSlots().contains(15)) {
            event.setCancelled(true);
        }
    }
}