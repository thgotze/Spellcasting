package com.gotze.magicParticles.entrypoints;

import com.gotze.magicParticles.CrescentSpell;
import com.gotze.magicParticles.LaserSpell;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SwingTool implements Listener {

    private final JavaPlugin plugin;

    public SwingTool(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerArmSwingEvent(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();

        Material material = player.getInventory().getItemInMainHand().getType();

        if (material == Material.DIAMOND_SWORD || material == Material.DIAMOND_PICKAXE) {
            new CrescentSpell(plugin, player.getLocation(), player);
        }

        if (material == Material.STICK) {
             new LaserSpell(plugin, player.getLocation(), player);
        }
    }
}
