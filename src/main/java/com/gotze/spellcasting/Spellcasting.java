package com.gotze.spellcasting;

import com.gotze.spellcasting.commands.*;
import com.gotze.spellcasting.data.PlayerProfileManager;
import com.gotze.spellcasting.feature.islands.IslandCommand;
import com.gotze.spellcasting.feature.islands.IslandManager;
import com.gotze.spellcasting.feature.lootcrate.LootCrateManager;
import com.gotze.spellcasting.feature.machines.MachineManager;
import com.gotze.spellcasting.feature.merchants.MerchantManager;
import com.gotze.spellcasting.feature.mines.MineManager;
import com.gotze.spellcasting.feature.recipes.RecipeRegistry;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeManager;
import com.gotze.spellcasting.pickaxe.capability.ItemModelManager;
import com.gotze.spellcasting.util.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Spellcasting extends JavaPlugin {
    public static Spellcasting plugin;
    public static World world;
    public static Location spawn;

    @Override
    public void onEnable() {
        plugin = this;
        world = Bukkit.getWorld("world");
        spawn = new Location(world, 0.50D, 100.0D, 0.5D, 0.0F, 0.0F);

        // Event listeners
        List.of(new GlobalListener(),
                new PlayerPickaxeManager(),
                new PlayerProfileManager(),
                new MenuListener(),
//                new LootPotManager(), // TODO: fix impl
//                new ResourcePackManager(), // TODO: remove?
                new MerchantManager(),
                new MachineManager(),
                new ItemModelManager(),
                new LootCrateManager(),
//                new PrivateVaultManager(), // TODO: fix impl
                new IslandManager(),
                new ActionBarManager()
        ).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));

        // Commands
        registerCommand("admin", new AdminCommand());
        registerCommand("balance", List.of("bal"), new BalanceCommand());
        registerCommand("pay", new PayCommand());
        registerCommand("pickaxe", List.of("pick", "p"), new PickaxeCommand());
        registerCommand("spawn", new SpawnCommand());
        registerCommand("message", List.of("msg", "dm", "pm", "tell", "whisper", "w"), new MessageCommand());
        registerCommand("island", List.of("is"), new IslandCommand());
//        registerCommand("pv", new PrivateVaultCommand());

        MineManager.startRefillingMines();
        MachineManager.startTickingMachines();
        RecipeRegistry.registerRecipes();
    }

    @Override
    public void onDisable() {
        MineManager.stopRefillingMines();
        MachineManager.stopTickingMachines();
        PlayerProfileManager.saveAllProfiles();
    }
}
