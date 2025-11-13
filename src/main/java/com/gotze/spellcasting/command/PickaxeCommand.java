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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PickaxeCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            new PickaxeMenu(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "menu", "m" -> new PickaxeMenu(player);
            case "get" -> {
                player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(player));
                player.sendMessage(text("You received your pickaxe!", GREEN));
            }
            case "reset" -> {
                PlayerProfile.fromPlayer(player).setPickaxeData(new PickaxeData());
                player.sendMessage(text("Pickaxe data reset!", GREEN));
            }
            case "repair" -> {
                ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                if (pickaxe == null) return;

                Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
                    pickaxe.setData(DataComponentTypes.DAMAGE, 0);

                    PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                    pickaxeData.setDurabilityDamage(0);
                    pickaxe.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
                }, 1L);
                player.sendMessage(text("Successfully repaired your pickaxe!", GREEN));
            }
            case "debug" -> player.sendMessage(text("Debug mode: enabled", GREEN));
            default -> player.sendMessage(text("Usage: /pickaxe <get|reset|repair|debug>", RED));
        }
    }
}