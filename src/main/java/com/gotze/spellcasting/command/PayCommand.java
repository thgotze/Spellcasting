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
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PayCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length != 2) {
            player.sendMessage(text("Usage: /pay <player> <amount>", RED));
            return;
        }

        String targetPlayerName = args[0];
        String amountStr = args[1];

        // Parse amount
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            player.sendMessage(text("Invalid amount! Please enter a valid number", RED));
            return;
        }

        // Validate amount
        if (amount <= 0) {
            player.sendMessage(text("Amount must be greater than 0!", RED));
            return;
        }

        // Find target player
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(text("Player '" + targetPlayerName + "' is not online!", RED));
            return;
        }

        // Check if trying to pay themselves
        if (player.equals(targetPlayer)) {
            player.sendMessage(text("You cannot pay yourself!", RED));
            return;
        }

        // Get player profiles
        PlayerProfile senderProfile = PlayerProfile.fromPlayer(player);
        PlayerProfile targetProfile = PlayerProfile.fromPlayer(targetPlayer);

        // Check if sender has enough balance
        if (senderProfile.getBalance() < amount) {
            player.sendMessage(text("Insufficient balance! You need $" + String.format("%.2f", amount) +
                    " but only have $" + String.format("%.2f", senderProfile.getBalance()), RED));
            return;
        }

        // Perform the transaction
        senderProfile.addBalance(-amount);
        targetProfile.addBalance(amount);

        // Send success messages
        player.sendMessage(text("Successfully paid $" + String.format("%.2f", amount) +
                " to " + targetPlayer.getName() + "!", GREEN));
        targetPlayer.sendMessage(text("You received $" + String.format("%.2f", amount) +
                " from " + player.getName() + "!", GREEN));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        // First argument - suggest online player names (excluding the sender)
        if (args.length == 0) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(commandSourceStack.getSender()))
                    .map(Player::getName)
                    .toList();

        } else if (args.length == 1) {
            String input = args[0].toLowerCase();

            // If input is empty, show all players
            if (input.isEmpty()) {
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !p.equals(commandSourceStack.getSender()))
                        .map(Player::getName)
                        .toList();
            }

            // Otherwise, filter by what they've typed
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