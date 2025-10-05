package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.menu.PickaxeMenu;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.block.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockBreaker;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockDamageListener;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlayerPickaxeManager implements Listener, BasicCommand {

    @EventHandler(priority = EventPriority.LOWEST)
    public void masterBlockBreakEventHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // check player is holding their pickaxe
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;

        player.getInventory().getItem(EquipmentSlot.HAND);
        // check if pickaxe is about to break, cancel event if so
        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);
        if (pickaxeData.durabilityDamage() + 1 == pickaxeData.pickaxeMaterial().maxDurability()) {
            event.setCancelled(true);
            player.sendMessage("Warning! Pickaxe has low durability!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        // ***
        // at this point the block break event is allowed to go through i.e. NOT cancelled
        // ***

        Block block = event.getBlock();
        // notify all listeners about this natural block break
        for (Enchantment enchantment : pickaxeData.enchantments()) {
            if (enchantment instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, true);
            }
        }

        for (Ability ability : pickaxeData.abilities()) {
            if (ability instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, true);
            }
        }

        // remove default ore drops
        if (BlockCategories.ORE_BLOCKS.containsKey(block.getType())) {
            event.setDropItems(false);
        }

        // handle the block break itself (increment blocks broken and drop items if block is an ore)
        BlockBreaker.handleBlockBreak(player, block, pickaxeData, true);

        // update pickaxe durability and lore a tick later
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), () -> {
            int durabilityDamage = pickaxe.getData(DataComponentTypes.DAMAGE);
            pickaxeData.setDurabilityDamage(durabilityDamage);
            pickaxe.lore(PlayerPickaxeService.pickaxeLore(pickaxeData));
        }, 1L);
    }

    @EventHandler
    public void onShiftRightClickHoldingPickaxeOpenMenu(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction().isRightClick() && player.isSneaking())) return;
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;

        event.setCancelled(true);

        new PickaxeMenu(player);
    }

    @EventHandler
    public void onShiftRightClickPickaxeInInventoryOpenMenu(InventoryClickEvent event) {
        if (event.getClick() != ClickType.SHIFT_RIGHT) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != player.getInventory()) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) return;

        new PickaxeMenu(player);
    }


    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;

        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);

        for (Enchantment enchantment : pickaxeData.enchantments()) {
            if (enchantment instanceof BlockDamageListener blockDamageListener) {
                blockDamageListener.onBlockDamage(player, event, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.abilities()) {
            if (ability instanceof BlockDamageListener blockDamageListener) {
                blockDamageListener.onBlockDamage(player, event, pickaxeData);
            }
        }
    }

    @EventHandler
    public void onHandSwapActivatePickaxeAbilities(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;
        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);

        for (Ability ability : pickaxeData.abilities()) {
            ability.activateAbility(player, pickaxeData);
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /pickaxe <get|reset>")
                    .color(NamedTextColor.RED));
            return;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (PlayerPickaxeService.pickaxeData(player) == null) {
                PlayerPickaxeService.createPickaxeData(player);
            }
            player.getInventory().addItem(PlayerPickaxeService.playerPickaxe(player));
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

        player.sendMessage(Component.text("Unknown subcommand. Usage: /pickaxe <get|reset>")
                .color(NamedTextColor.RED));
    }
}