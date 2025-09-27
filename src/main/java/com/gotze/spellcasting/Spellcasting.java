package com.gotze.spellcasting;

import com.gotze.spellcasting.feature.ResourcePackManager;
import com.gotze.spellcasting.feature.lootpot.LootPotManager;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spellcasting extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new ResourcePackManager(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new LootPotManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", "Manage your custom pickaxe", playerPickaxeManager);
    }
}