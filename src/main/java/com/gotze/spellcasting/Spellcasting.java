package com.gotze.spellcasting;

import com.gotze.spellcasting.bossbar.LootCrateFeature;
import com.gotze.spellcasting.command.*;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.machine.MachineManager;
import com.gotze.spellcasting.merchant.MerchantManager;
import com.gotze.spellcasting.mine.MineManager;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.pickaxe.capability.ItemModelManager;
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

        MineManager mineManager = new MineManager();
        MachineManager machineManager = new MachineManager();
        PlayerProfileManager playerProfileManager = new PlayerProfileManager();

        // Event listeners
        pluginManager.registerEvents(new GlobalListener(), this);
        pluginManager.registerEvents(new PlayerPickaxeManager(), this);
        pluginManager.registerEvents(playerProfileManager, this);
        pluginManager.registerEvents(new MenuListener(), this);
//        pluginManager.registerEvents(new LootPotManager(), this);
//        pluginManager.registerEvents(new ResourcePackManager(), this);
        pluginManager.registerEvents(new MerchantManager(), this);
        pluginManager.registerEvents(machineManager, this);
        pluginManager.registerEvents(new ItemModelManager(), this);
        pluginManager.registerEvents(new LootCrateFeature(), this);
        pluginManager.registerEvents(new IslandManager(), this); // TODO: add impl

        // Commands
        registerCommand("admin", new AdminCommand());
        registerCommand("balance", List.of("bal"), new BalanceCommand());
        registerCommand("pay", new PayCommand());
        registerCommand("pickaxe", List.of("pick", "p"), new PickaxeCommand());
        registerCommand("spawn", new SpawnCommand());
        registerCommand("message", List.of("msg", "dm", "pm", "tell", "whisper", "w"), new MessageCommand());

        // Other
        RecipeRegistry.registerRecipes();

        IslandManager islandManager = new IslandManager();
        pluginManager.registerEvents(islandManager, this);

        registerCommand("island", List.of("is"), new IslandCommand(islandManager)); // TODO: add impl

        lifecycleManagers.add(mineManager);
        lifecycleManagers.add(machineManager);
        lifecycleManagers.add(playerProfileManager);

        lifecycleManagers.forEach(LifecycleManager::start);
    }

    @Override
    public void onDisable() {
        lifecycleManagers.forEach(LifecycleManager::stop);
    }
}