package com.gotze.spellcasting;

import com.gotze.spellcasting.lootpot.LootPotManager;
import com.gotze.spellcasting.machine.CrusherManager;
import com.gotze.spellcasting.machine.CrusherService;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Spellcasting extends JavaPlugin {

    private BukkitTask crusherTicker;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        CrusherService crusherService = new CrusherService();
        crusherTicker = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                crusherService::tickCrushers,
                0L,
                10L
        );
        pluginManager.registerEvents(new CrusherManager(crusherService), this);

        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new LootPotManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", playerPickaxeManager);

        pluginManager.registerEvents(new TestingBrewingStandListener(), this);
//        pluginManager.registerEvents(new ResourcePackManager(), this);
    }

    @Override
    public void onDisable() {
        crusherTicker.cancel();
    }
}