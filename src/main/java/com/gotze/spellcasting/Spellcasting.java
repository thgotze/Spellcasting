package com.gotze.spellcasting;

import com.gotze.spellcasting.feature.ResourcePackManager;
import com.gotze.spellcasting.feature.lootpot.LootPotManager;
import com.gotze.spellcasting.feature.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.gui.AbilityGUI;
import com.gotze.spellcasting.gui.EnchantmentsGUI;
import com.gotze.spellcasting.gui.MaterialsGUI;
import com.gotze.spellcasting.gui.PickaxeGUI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spellcasting extends JavaPlugin {
    public static Spellcasting INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        PluginManager pluginManager = getServer().getPluginManager();

        // Features
        pluginManager.registerEvents(new ResourcePackManager(), this);
        pluginManager.registerEvents(new LootPotManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        pluginManager.registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", "Manage your custom pickaxe", playerPickaxeManager);

        // GUIs
        pluginManager.registerEvents(new PickaxeGUI(), this);
        pluginManager.registerEvents(new EnchantmentsGUI(), this);
        pluginManager.registerEvents(new MaterialsGUI(), this);
        pluginManager.registerEvents(new AbilityGUI(), this);
    }
}