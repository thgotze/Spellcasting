package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.menu.PickaxeMenu;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.block.BlockCategories;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PlayerPickaxeManager implements Listener, BasicCommand {

    @EventHandler
    public void onBlockBreakWithPickaxe(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // check player is holding their pickaxe
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;

        // check if pickaxe is about to break, cancel event if so
        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);
        if (pickaxeData.getDurabilityDamage() + 1 == pickaxeData.getPickaxeMaterial().maxDurability()) {
            event.setCancelled(true);
            player.sendMessage(text("Pickaxe durability too low to continue mining!", RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        // ---------------
        // at this point the block break event is allowed to go through i.e., NOT cancelled
        // ---------------
        Block block = event.getBlock();
        // notify all listeners about this natural block break
        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, true);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockBreakListener blockBreakListener) {
                blockBreakListener.onBlockBreak(player, block, pickaxeData, true);
            }
        }

        // remove default ore drops
        if (BlockCategories.ORE_BLOCKS.containsKey(block.getType())) {
            event.setDropItems(false);
        }

        // handle the block break itself (increment blocks broken and drop items)
        BlockBreaker.handleBlockBreak(player, block, pickaxeData, true);

        // update pickaxe durability and lore a tick later
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), () -> {
            int durabilityDamage = pickaxe.getData(DataComponentTypes.DAMAGE);
            PlayerPickaxeService.setDurabilityDamage(player, durabilityDamage);
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

        if (!PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) return;

        new PickaxeMenu(player);
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;

        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockDamageListener blockDamageListener) {
                blockDamageListener.onBlockDamage(player, event, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockDamageListener blockDamageListener) {
                blockDamageListener.onBlockDamage(player, event, pickaxeData);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinLoadPickaxeData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerPickaxeService.loadPickaxeDataFromYAML(player);
    }


    @EventHandler
    public void onHandSwapActivatePickaxeAbilities(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false).orElse(null);
        if (pickaxe == null) return;
        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.pickaxeData(player);
        if (pickaxeData.getDurabilityDamage() + 50 >= pickaxeData.getPickaxeMaterial().maxDurability()) {
            player.sendMessage(Component.text("Pickaxe durability too low to activate ability", RED));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            ability.activateAbility(player, pickaxeData);
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("get")) {
                player.getInventory().addItem(PlayerPickaxeService.playerPickaxe(player));
                player.sendMessage(text("You received your pickaxe!", GREEN));
                return;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                PlayerPickaxeService.resetPickaxeData(player);
                player.sendMessage(Component.text("Pickaxe data reset!", GREEN));
                return;
            }
        }

        player.sendMessage(text("Usage: /pickaxe <get|reset>", RED));
    }
}