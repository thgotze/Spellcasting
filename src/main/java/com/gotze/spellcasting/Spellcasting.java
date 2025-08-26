package com.gotze.spellcasting;

import com.gotze.spellcasting.gui.EnchantmentsGUI;
import com.gotze.spellcasting.gui.MaterialsGUI;
import com.gotze.spellcasting.gui.PickaxeGUI;
import com.gotze.spellcasting.gui.SpellsGUI;
import com.gotze.spellcasting.listener.PlayerJoinListener;
import com.gotze.spellcasting.listener.ShiftRightClickWithPickaxeListener;
import com.gotze.spellcasting.listener.SwapHandItemsListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Spellcasting extends JavaPlugin {
    public static Spellcasting INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // GUIs
        getServer().getPluginManager().registerEvents(new EnchantmentsGUI(), this);
        getServer().getPluginManager().registerEvents(new MaterialsGUI(), this);
        getServer().getPluginManager().registerEvents(new PickaxeGUI(), this);
        getServer().getPluginManager().registerEvents(new SpellsGUI(), this);

        // Event Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new ShiftRightClickWithPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new SwapHandItemsListener(), this);
    }
}