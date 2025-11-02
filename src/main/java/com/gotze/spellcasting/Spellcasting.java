package com.gotze.spellcasting;

import com.gotze.spellcasting.machine.MachineManager;
import com.gotze.spellcasting.mines.MineManager;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.LifecycleManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.event.Listener;
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

        // Simple listeners (no lifecycle)
        register(new MenuListener());
        register(new LootPotManager());
        register(new ResourcePackManager());

        // Lifecycle managers
        register(new MachineManager());
        register(new MineManager());

        // Special cases with commands
        PlayerPickaxeManager pickaxeManager = new PlayerPickaxeManager();
        register(pickaxeManager);
        registerCommand("pickaxe", pickaxeManager);

        // Non-listener, non-lifecycle
        RecipeRegistry.registerRecipes();

        lifecycleManagers.forEach(LifecycleManager::start);
    }

    private void register(Object object) {
        PluginManager pluginManager = getServer().getPluginManager();

        if (object instanceof Listener listener) {
            pluginManager.registerEvents(listener, this);
        }

        if (object instanceof LifecycleManager lifecycleManager) {
            lifecycleManagers.add(lifecycleManager);
        }
    }

    @Override
    public void onDisable() {
        lifecycleManagers.forEach(LifecycleManager::stop);
    }
}