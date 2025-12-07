package com.gotze.islands;

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

    private final IslandManager islandManager;

    public IslandCommand(IslandManager islandManager) {
        this.islandManager = islandManager;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            player.sendMessage(text("Usage: /island <create|home>", GREEN));
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            islandManager.createIsland(player);
        } else if (args[0].equalsIgnoreCase("home")) {
            islandManager.teleportHome(player);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("create", "home");
        }
        return Collections.emptyList();
    }
}