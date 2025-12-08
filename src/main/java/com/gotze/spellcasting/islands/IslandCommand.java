package com.gotze.spellcasting.islands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class IslandCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            player.sendMessage(text("Usage: /island <create|home|sethome|resethome>", GREEN));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> IslandManager.createIsland(player);
            case "home" -> IslandManager.teleportToIslandHome(player);
            case "sethome" -> IslandManager.setIslandHome(player);
            case "resethome" -> IslandManager.resetIslandHome(player);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("create", "home", "sethome", "resethome");
        }
        return Collections.emptyList();
    }
}