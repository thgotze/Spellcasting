package com.gotze.spellcasting;

import com.gotze.spellcasting.islands.IslandManager;
import com.gotze.spellcasting.mines.MineManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (MineManager.isInAnyMine(block)) return;
        if (IslandManager.isLocationOnPlayerIsland(player, block.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (IslandManager.isLocationOnPlayerIsland(player, block.getLocation())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (event.getAction() == Action.PHYSICAL || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            if (MineManager.isInAnyMine(block)) return;
            if (IslandManager.isLocationOnPlayerIsland(player, block.getLocation())) return;

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        event.setCancelled(true);

        Location playerLocation = player.getLocation();

        player.teleport(new Location(
                playerLocation.getWorld(),
                playerLocation.getX(),
                325,
                playerLocation.getZ()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MineManager.teleportPlayerToSafety(player);
    }

    // Is here because of the Wind Burst Ability
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWindChargeHitEntity(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WindCharge) {
            event.setCancelled(true);
        }
    }

    // Is here because of the Wind Burst Ability
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWindChargeExplode(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof WindCharge) {
            event.setRadius(0f);
        }
    }
}