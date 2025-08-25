package com.gotze.spellcasting;

import com.gotze.spellcasting.gui.EnchantmentsGUI;
import com.gotze.spellcasting.gui.MaterialsGUI;
import com.gotze.spellcasting.gui.PickaxeGUI;
import com.gotze.spellcasting.gui.SpellsGUI;
import com.gotze.spellcasting.listener.PlayerJoinListener;
import com.gotze.spellcasting.listener.ShiftRightClickWithPickaxeListener;
import com.gotze.spellcasting.listener.SwapHandItemsListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin { // TODO RENAME
    private PlayerPickaxeService pickaxeService; // TODO

    @Override
    public void onEnable() {
        pickaxeService = new PlayerPickaxeService();

        // GUIs
        getServer().getPluginManager().registerEvents(new EnchantmentsGUI(this), this); // TODO
        getServer().getPluginManager().registerEvents(new MaterialsGUI(this), this); // TODO
        getServer().getPluginManager().registerEvents(new PickaxeGUI(this), this); // TODO
        getServer().getPluginManager().registerEvents(new SpellsGUI(this), this); // TODO

        // Event Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this); // TODO
        getServer().getPluginManager().registerEvents(new ShiftRightClickWithPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new SwapHandItemsListener(), this);
    }

    public PlayerPickaxeService getPickaxeService() { // TODO
        return pickaxeService;
    }
}