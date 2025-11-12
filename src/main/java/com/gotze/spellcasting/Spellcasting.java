package com.gotze.spellcasting;

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

    public static Spellcasting getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerProfileManager(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new LootPotManager(), this);
        pluginManager.registerEvents(new ResourcePackManager(), this);
        pluginManager.registerEvents(new MerchantManager(), this);

        PlayerPickaxeManager pickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(pickaxeManager, this);
        registerCommand("pickaxe", pickaxeManager);

        MachineManager machineManager = new MachineManager();
        pluginManager.registerEvents(machineManager, this);
        lifecycleManagers.add(machineManager);

        lifecycleManagers.add(new MineManager());

        RecipeRegistry.registerRecipes();

        lifecycleManagers.forEach(LifecycleManager::start);
    }

    @Override
    public void onDisable() {
        lifecycleManagers.forEach(LifecycleManager::stop);
    }
}