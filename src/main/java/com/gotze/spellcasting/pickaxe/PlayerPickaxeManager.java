package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.ability.BazookaAbility;
import com.gotze.spellcasting.pickaxe.ability.SliceAbility;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerPickaxeManager implements Listener, BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /pickaxe <give|reset>")
                    .color(NamedTextColor.RED));
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (PlayerPickaxeService.getPickaxeData(player) == null) {
                PlayerPickaxeService.createPickaxeData(player);
            }
            player.getInventory().addItem(PlayerPickaxeService.getPickaxe(player));
            player.sendMessage(Component.text("You received your pickaxe!")
                    .color(NamedTextColor.GREEN));
            return;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            PlayerPickaxeService.createPickaxeData(player);
            player.sendMessage(Component.text("Pickaxe data reset!")
                    .color(NamedTextColor.GREEN));
            return;
        }

        player.sendMessage(Component.text("Unknown subcommand. Usage: /pickaxe <give|reset>")
                .color(NamedTextColor.RED));
    }

    @EventHandler
    public void activateAbility(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) return;
        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        if (!pickaxeData.hasAbilities()) return;

        Ability slice = pickaxeData.getAbility(Ability.AbilityType.SLICE);
        if (slice != null) {
            int level = slice.getLevel();
            int amplifier = level - 1;
            new SliceAbility(player).activate();
            return;
        }

        Ability bazooka = pickaxeData.getAbility(Ability.AbilityType.BAZOOKA);
        if (bazooka != null) {
            int level = bazooka.getLevel();
            int amplifier = level - 1;
            new BazookaAbility(player).activate();
            return;
        }

//        Ability laser = pickaxeData.getAbility(Ability.AbilityType.LASER);
//        if (laser != null) {
//            int level = laser.getLevel();
//            int amplifier = level - 1;
//            new LaserAbility(player).activate();
//            return;
//        }
//
//        Ability transformHammer = pickaxeData.getAbility(Ability.AbilityType.TRANSFORM_HAMMER);
//        if (transformHammer != null) {
//            int level = transformHammer.getLevel();
//            int amplifier = level - 1;
//            new TransformHammer(player).activate();
//            return;
//        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) return;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        pickaxeData.addBlocksBroken(1);

        if (pickaxeData.hasEnchantments()) {
            for (Enchantment enchantment : pickaxeData.getEnchantments()) {
                enchantment.getEnchantmentType().apply(player, event, pickaxeData, enchantment.getLevel());
            }
        }

//        pickaxeData.addDamage(1); TODO: make damage read the actual itemstacks damage value

        List<Component> lore = PlayerPickaxeService.getPickaxeLore(pickaxeData);
        heldItem.lore(lore);
    }
}