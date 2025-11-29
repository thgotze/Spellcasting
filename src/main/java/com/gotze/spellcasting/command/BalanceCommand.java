package com.gotze.spellcasting.command;

import com.gotze.spellcasting.data.PlayerProfile;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class BalanceCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            PlayerProfile playerProfile = PlayerProfile.fromPlayer(player);
            player.sendMessage(text("Your balance: " + playerProfile.getBalance()));
            return;
        }

        String targetPlayerName = args[0];

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(text("Player '" + targetPlayerName + "' is not online!", RED));
            return;
        }

        PlayerProfile targetProfile = PlayerProfile.fromPlayer(targetPlayer);
        player.sendMessage(text("Balance of " + targetPlayerName + ": " + targetProfile.getBalance()));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull []
            args) {
        // No arguments - suggest all online player names (excluding the sender)
        if (args.length == 0) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(commandSourceStack.getSender()))
                    .map(Player::getName)
                    .toList();

        } else if (args.length == 1) {
            String input = args[0].toLowerCase();
            // First argument empty - suggest all online player names (excluding the sender)
            if (input.isEmpty()) {
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !p.equals(commandSourceStack.getSender()))
                        .map(Player::getName)
                        .toList();
            }

            // First argument not empty - suggest online player names filtered by input
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(commandSourceStack.getSender()))
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .toList();
        }

        // No suggestions for other arguments
        return Collections.emptyList();
    }
}