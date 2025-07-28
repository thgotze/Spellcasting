package com.gotze.magicParticles.entrypoints;

import com.gotze.magicParticles.SliceSpell;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CastSpellCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public CastSpellCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (sender instanceof Player player) {
            new SliceSpell(plugin, player.getLocation(), player);
        } else if (sender instanceof BlockCommandSender blockCommandSender) {
            new SliceSpell(plugin, blockCommandSender.getBlock().getLocation(), null);
        }

        return false;
    }
}