package com.gotze.spellcasting.feature.pickaxe;

import com.gotze.spellcasting.feature.pickaxe.ability.Ability;
import com.gotze.spellcasting.feature.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.SoundUtils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.gotze.spellcasting.common.OreBlocks.ORE_BLOCKS;

public class PlayerPickaxeManager implements Listener, BasicCommand {

    @EventHandler
    public void onBlockBreakActivateEnchants(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) return;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        int maxDamage = heldItem.getData(DataComponentTypes.MAX_DAMAGE);
        int damage = heldItem.getData(DataComponentTypes.DAMAGE);

        if (damage + 1 == maxDamage) {
            event.setCancelled(true);
            player.sendMessage("Warning! Pickaxe has low durability!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
        for (Enchantment enchantment : enchantments) {
            enchantment.activate(player, event, pickaxeData);
        }

        pickaxeData.addBlocksBroken(1);
        pickaxeData.setDamage(damage + 1);

        heldItem.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
    }

    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (!ORE_BLOCKS.containsKey(blockType)) return;

        event.setDropItems(false);

        ORE_BLOCKS.get(blockType).toItemStack().ifPresent(itemStack ->
                block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), itemStack)
        );
    }

    @EventHandler
    public void onHandSwapActivateAbilities(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player)) return;
        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        for (Ability ability : pickaxeData.getAbilities()) {
            ability.activate(player, pickaxeData);
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /pickaxe <get|reset>").color(NamedTextColor.RED));
            return;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (PlayerPickaxeService.getPickaxeData(player) == null) {
                PlayerPickaxeService.createPickaxeData(player);
            }
            player.getInventory().addItem(PlayerPickaxeService.getPickaxe(player));
            player.sendMessage(Component.text("You received your pickaxe!").color(NamedTextColor.GREEN));
            return;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            PlayerPickaxeService.createPickaxeData(player);
            player.sendMessage(Component.text("Pickaxe data reset!").color(NamedTextColor.GREEN));
            return;
        }

        player.sendMessage(Component.text("Unknown subcommand. Usage: /pickaxe <get|reset>").color(NamedTextColor.RED));
    }
}