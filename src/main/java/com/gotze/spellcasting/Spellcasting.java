package com.gotze.spellcasting;

import com.gotze.spellcasting.lootpot.LootPotManager;
import com.gotze.spellcasting.machine.CrusherManager;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spellcasting extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new CrusherManager(), this);
        pluginManager.registerEvents(new LootPotManager(), this);
//        pluginManager.registerEvents(new ResourcePackManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", playerPickaxeManager);
    }
}