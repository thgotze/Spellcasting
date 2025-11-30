package com.gotze.spellcasting.command;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.data.PlayerProfile;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.menu.PickaxeMenu;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PickaxeCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            new PickaxeMenu(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "menu", "m" -> new PickaxeMenu(player);
            case "get" -> {
                if (player.hasPermission("spellcasting.pickaxe.get")) {
                    player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(player));
                    player.sendMessage(text("You received your pickaxe!", GREEN));
                }
            }
            case "clone" -> {
                if (player.hasPermission("spellcasting.admin")) {
                    if (args.length == 1) {
                        // Clone own pickaxe
                        player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(player));
                        player.sendMessage(text("You cloned your pickaxe!", GREEN));
                    } else if (args.length == 2) {
                        // Clone another player's pickaxe
                        String targetPlayerName = args[1];
                        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                        if (targetPlayer == null) {
                            player.sendMessage(text("Player '" + targetPlayerName + "' is not online!", RED));
                            return;
                        }

                        player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(targetPlayer));
                        player.sendMessage(text("You cloned " + targetPlayer.getName() + "'s pickaxe!", GREEN));
                    } else {
                        player.sendMessage(text("Usage: /pickaxe clone <player>", RED));
                    }
                }
            }
            case "debug" -> {
                if (player.hasPermission("spellcasting.pickaxe.debug")) {
                    player.sendMessage(text("Debug mode: ENABLED", GREEN)); // TODO
                }
            }
            case "repair", "rep" -> {
                if (player.hasPermission("spellcasting.admin")) {
                    if (args.length == 1) {
                        // Repair own pickaxe
                        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                        if (pickaxe == null) return;

                        Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                            pickaxe.setData(DataComponentTypes.DAMAGE, 0);

                            PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                            pickaxeData.setDurabilityDamage(0);

                            pickaxe.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));

                            player.sendMessage(text("Your pickaxe has been repaired!", GREEN));
                        }, 1L);

                    } else if (args.length == 2) {
                        // Repair another player's pickaxe
                        String targetPlayerName = args[1];
                        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                        if (targetPlayer == null) {
                            player.sendMessage(text("Player '" + targetPlayerName + "' is not online!", RED));
                            return;
                        }

                        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(targetPlayer, false);
                        if (pickaxe == null) {
                            player.sendMessage(text("The targeted player needs to have a pickaxe in their main hand!", RED));
                            return;
                        }

                        Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                            pickaxe.setData(DataComponentTypes.DAMAGE, 0);

                            PickaxeData pickaxeData = PickaxeData.fromPlayer(targetPlayer);
                            pickaxeData.setDurabilityDamage(0);

                            pickaxe.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));

                            targetPlayer.sendMessage(text("Your pickaxe has been repaired!", GREEN));
                            player.sendMessage(text(targetPlayer.getName() + "'s pickaxe has been repaired!", GREEN));

                        }, 1L);
                    }
                }
            }
            case "reset" -> {
                if (player.hasPermission("spellcasting.admin")) {
                    if (args.length == 1) {
                        // Reset own pickaxe
                        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                        if (pickaxe == null) return;

                        PlayerProfile.fromPlayer(player).setPickaxeData(new PickaxeData());
                        player.getInventory().setItem(EquipmentSlot.HAND, pickaxe);
                        player.sendMessage(text("Your pickaxe data has been reset!", GREEN));

                    } else if (args.length == 2) {
                        // Reset another player's pickaxe
                        String targetPlayerName = args[1];
                        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                        if (targetPlayer == null) {
                            player.sendMessage(text("Player '" + targetPlayerName + "' is not online!", RED));
                            return;
                        }

                        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(targetPlayer, false);
                        if (pickaxe == null) {
                            player.sendMessage(text("The targeted player needs to have a pickaxe in their main hand!", RED));
                            return;
                        }

                        PlayerProfile.fromPlayer(targetPlayer).setPickaxeData(new PickaxeData());
                        targetPlayer.getInventory().setItem(EquipmentSlot.HAND, PlayerPickaxeService.getPlayerPickaxe(targetPlayer));
                        targetPlayer.sendMessage(text("Your pickaxe data has been reset!", GREEN));
                        player.sendMessage(text("Pickaxe data of " + targetPlayer.getName() + " has been reset!", GREEN));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return Collections.emptyList();

        // No arguments - suggest possible subcommand options
        if (args.length == 0) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("menu");

            if (player.hasPermission("spellcasting.admin")) {
                suggestions.addAll(List.of("get","clone", "debug", "repair", "reset"));
            }
            return suggestions;

        } else if (args.length == 1) {
            // First argument - suggest possible subcommand options based on input
            String input = args[0].toLowerCase();
            if ("menu".startsWith(input)) return List.of("menu");

            if (player.hasPermission("spellcasting.admin")) {
                if ("get".startsWith(input)) return List.of("get");
                if ("clone".startsWith(input)) return List.of("clone");
                if ("debug".startsWith(input)) return List.of("debug");
                if ("repair".startsWith(input)) return List.of("repair");
                if ("reset".startsWith(input)) return List.of("reset");
            }

        } else if (args.length == 2) {
            // Second argument - only suggest players for command if admin
            if ((args[0].equalsIgnoreCase("get") ||
                    args[0].equalsIgnoreCase("clone") ||
                    args[0].equalsIgnoreCase("debug") ||
                    args[0].equalsIgnoreCase("repair") ||
                    args[0].equalsIgnoreCase("reset"))
                    && player.hasPermission("spellcasting.admin")) {
                String input = args[1].toLowerCase();

                if (input.isEmpty()) {
                    // Suggest all online players
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .toList();
                }

                // Filter players by input
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .toList();
            }
        }
        // No suggestions for other arguments
        return Collections.emptyList();
    }
}