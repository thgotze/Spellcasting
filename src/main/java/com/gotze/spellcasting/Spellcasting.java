package com.gotze.spellcasting;

import com.gotze.spellcasting.machine.MachineManager;
import com.gotze.spellcasting.mines.MineManager;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spellcasting extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new MachineManager(), this);
        pluginManager.registerEvents(new LootPotManager(), this);
//        pluginManager.registerEvents(new ResourcePackManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", playerPickaxeManager);

        RecipeRegistry.registerRecipes();

        MineManager mineManager = new MineManager(this,
                new Location(getServer().getWorld("world"), 602, 244, 641),
                new Location(getServer().getWorld("world"), 650, 184, 589));
        mineManager.startAutoRefill();
    }
}