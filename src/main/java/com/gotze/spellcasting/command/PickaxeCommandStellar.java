package com.gotze.spellcasting.command;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.data.PlayerProfile;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.pickaxe.menu.PickaxeMenu;
import com.undefined.stellar.StellarCommand;
import com.undefined.stellar.util.CommandUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class PickaxeCommandStellar {

    public static void register(JavaPlugin plugin) {
        CommandUtil.unregisterCommand("pickaxe", plugin);
        CommandUtil.unregisterCommand("/fastasyncworldedit:pickaxe", plugin);

        StellarCommand cmd = new StellarCommand("pick")
                .addAliases("pickaxe", "p")
                .setDescription("Interact with your custom pickaxe")
                .setUsageText("/pickaxe <menu|get|reset|repair|debug>")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();
                    new PickaxeMenu(player);
                });

        cmd.addLiteralArgument("menu")
                .addAliases("m")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();
                    new PickaxeMenu(player);
                });

        cmd.addLiteralArgument("get")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();
                    player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(player));
                    player.sendMessage(text("You received your pickaxe!", GREEN));
                });

        cmd.addLiteralArgument("reset")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();
                    PlayerProfile.fromPlayer(player).setPickaxeData(new PickaxeData());
                    player.sendMessage(text("Pickaxe data reset!", GREEN));
                });

        cmd.addLiteralArgument("repair")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();

                    ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, true);
                    if (pickaxe == null) return;

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        pickaxe.setData(DataComponentTypes.DAMAGE, 0);

                        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
                        pickaxeData.setDurabilityDamage(0);

                        pickaxe.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
                    }, 1L);

                    player.sendMessage(text("Successfully repaired your pickaxe!", GREEN));
                });

        cmd.addLiteralArgument("debug")
                .addExecution(Player.class, context -> {
                    Player player = context.getSender();
                    player.sendMessage(text("Debug mode: enabled", GREEN));
                    // TODO
                });

        cmd.hideDefaultFailureMessages(true, true)
                .addGlobalFailureMessage(
                        "<red>Usage: /pickaxe <menu|get|reset|repair|debug>"
                );

        cmd.register(plugin);
    }
}