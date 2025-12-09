package com.gotze.spellcasting.feature.vaults;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PrivateVaultCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length != 1) {
            player.sendMessage(text("Usage: /pv <number>", RED));
            return;
        }

        // Parse vault number
        int vaultNumber;
        try {
            vaultNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(text("Usage: /pv <number>", RED));
            return;
        }

        Inventory playerVault = PrivateVaultManager.getPlayerPrivateVault(player, vaultNumber);
        if (playerVault == null) {
            player.sendMessage(text("Usage: /pv <number>", RED));
            return;
        }

        player.openInventory(playerVault);
    }
}
