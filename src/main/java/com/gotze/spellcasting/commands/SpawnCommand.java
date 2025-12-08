package com.gotze.spellcasting.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;
        player.teleport(new Location(player.getWorld(), 0.5, 100, 0.5));
    }
}