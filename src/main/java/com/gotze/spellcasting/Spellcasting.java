package com.gotze.spellcasting;

import com.gotze.spellcasting.gui.AbilityGUI;
import com.gotze.spellcasting.gui.EnchantmentsGUI;
import com.gotze.spellcasting.gui.MaterialsGUI;
import com.gotze.spellcasting.gui.PickaxeGUI;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spellcasting extends JavaPlugin {
    public static Spellcasting INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // GUIs
        getServer().getPluginManager().registerEvents(new PickaxeGUI(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentsGUI(), this);
        getServer().getPluginManager().registerEvents(new MaterialsGUI(), this);
        getServer().getPluginManager().registerEvents(new AbilityGUI(), this);

        getServer().getPluginManager().registerEvents(new ResourcePackManager(), this);

        PlayerPickaxeManager playerPickaxeManager = new PlayerPickaxeManager();
        getServer().getPluginManager().registerEvents(playerPickaxeManager, this);
        registerCommand("pickaxe", "Manage your custom pickaxe", playerPickaxeManager);
    }
}