package com.gotze.spellcasting.machine.crusher;

import com.gotze.spellcasting.merchants.Merchant2;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Crusher2 extends Merchant2 {

    public Crusher2(int rows, Component title) {
        super(3, );
    }

    @Override
    protected void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

    }

    @Override
    protected void populate(Player player) {

    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }
}
