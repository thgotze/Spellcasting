package com.gotze.magicParticles;

import com.gotze.magicParticles.entrypoints.CastSpellCommand;
import com.gotze.magicParticles.entrypoints.SwingTool;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        getServer().getPluginManager().registerEvents(new SwingTool(this), this);
        getCommand("crescent").setExecutor(new CastSpellCommand(this));
    }
}