package com.gotze.spellcasting;

import com.gotze.spellcasting.command.PickaxeCommand;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.machine.MachineManager;
import com.gotze.spellcasting.merchant.MerchantManager;
import com.gotze.spellcasting.mine.MineManager;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.LifecycleManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Spellcasting extends JavaPlugin {

    private final List<LifecycleManager> lifecycleManagers = new ArrayList<>();

    private static Spellcasting plugin;
    private static MineManager mineManager;

    public static Spellcasting getPlugin() {
        return plugin;
    }

    public static MineManager getMineManager() {
        return mineManager;
    }

    @Override
    public void onEnable() {
        plugin = this;
        mineManager = new MineManager();

        PluginManager pluginManager = getServer().getPluginManager();

        MachineManager machineManager = new MachineManager();

        // Event listeners
        pluginManager.registerEvents(new GlobalListener(mineManager), this);
        pluginManager.registerEvents(new PlayerPickaxeManager(), this);
        pluginManager.registerEvents(new PlayerProfileManager(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new LootPotManager(), this);
        pluginManager.registerEvents(new ResourcePackManager(), this);
        pluginManager.registerEvents(new MerchantManager(), this);
        pluginManager.registerEvents(machineManager, this);

        // Commands
        registerCommand("pickaxe", new PickaxeCommand());

        // Other
        RecipeRegistry.registerRecipes();

        lifecycleManagers.add(mineManager);
        lifecycleManagers.add(machineManager);

        lifecycleManagers.forEach(LifecycleManager::start);
    }

    @Override
    public void onDisable() {
        lifecycleManagers.forEach(LifecycleManager::stop);
    }
}