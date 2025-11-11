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

        // Player profile manager
        PlayerProfileManager playerProfileManager = new PlayerProfileManager();
        pluginManager.registerEvents(playerProfileManager, this);

        // Menu listener
        MenuListener menuListener = new MenuListener();
        pluginManager.registerEvents(menuListener, this);

        // Loot pot manager
        LootPotManager lootPotManager = new LootPotManager();
        pluginManager.registerEvents(lootPotManager, this);

        // Resource pack manager
        ResourcePackManager resourcePackManager = new ResourcePackManager();
        pluginManager.registerEvents(resourcePackManager, this);

        // Machine manager
        MachineManager machineManager = new MachineManager();
        pluginManager.registerEvents(machineManager, this);
        lifecycleManagers.add(machineManager);

        // Mine manager
        MineManager mineManager = new MineManager();
        lifecycleManagers.add(mineManager);

        // Player pickaxe manager
        PlayerPickaxeManager pickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(pickaxeManager, this);
        registerCommand("pickaxe", pickaxeManager);

        // Merchant manager
        MerchantManager merchantManager = new MerchantManager();
        pluginManager.registerEvents(merchantManager, this);

        // Other
        RecipeRegistry.registerRecipes();

        // Life cycle managers
        lifecycleManagers.forEach(LifecycleManager::start);
    }

    @Override
    public void onDisable() {
        lifecycleManagers.forEach(LifecycleManager::stop);
    }
}