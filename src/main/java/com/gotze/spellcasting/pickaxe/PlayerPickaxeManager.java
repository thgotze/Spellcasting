package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.PlayerBalanceService;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.menu.PickaxeMenu;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.block.BlockCategories;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PlayerPickaxeManager implements Listener, BasicCommand, BlockBreaker {
    private final Map<Player, Integer> selectedAbilityIndex = new HashMap<>();

    @EventHandler
    public void onBlockBreakWithPickaxe(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // check player is holding their pickaxe
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        // check if pickaxe is about to break, cancel event if so
        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        if (pickaxeData.getDurabilityDamage() + 1 == pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            event.setCancelled(true);
            player.sendMessage(text("Pickaxe durability too low to continue mining!", RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 3 /*3 seconds*/, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        // at this point the block break event is allowed to go through i.e., NOT canceled
        Block block = event.getBlock();

        // handles the block break itself (increment blocks broken and drop items)
        // notifies all listeners about this natural block break
        breakBlock(player, block, pickaxeData, true);

        // remove default ore drops
        if (BlockCategories.ORE_BLOCKS.containsKey(block.getType())) {
            event.setDropItems(false);
        }

        // update pickaxe durability and lore a tick later
        Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), () -> {
            int durabilityDamage = pickaxe.getData(DataComponentTypes.DAMAGE);
            pickaxeData.setDurabilityDamage(durabilityDamage);
            pickaxe.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
        }, 1L);
    }

    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

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
    public void onHandSwapCycleAbility(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        new PickaxeMenu(player);
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        int current = selectedAbilityIndex.getOrDefault(player, 0);
        int next = (current + 1) % abilities.size();
        selectedAbilityIndex.put(player, next);

        Ability selectedAbility = abilities.get(next);
        player.sendActionBar(text("Selected ability: ").color(YELLOW).append(selectedAbility.getAbilityType().getFormattedName()));
    }

    @EventHandler
    public void onShiftRightClickActivateAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().isRightClick()) return;
        if (!player.isSneaking()) return;
        if (event.getHand() == EquipmentSlot.HAND) return;

        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        if (pickaxeData.getDurabilityDamage() + 20 >= pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            player.sendMessage(text("Pickaxe durability too low to activate ability", RED));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        int index = selectedAbilityIndex.getOrDefault(player, 0);

        Ability selectedAbility = abilities.get(index);
        selectedAbility.activateAbility(player, pickaxeData);
    }

    @EventHandler
    public void onRightClickPickaxeInInventoryOpenMenu(InventoryClickEvent event) {
        if (!event.getClick().isRightClick()) return;
        if (!(event.getInventory().getHolder() instanceof Player player)) return;
        if (event.getClickedInventory() != player.getInventory()) return;
        if (!event.getCursor().isEmpty()) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) {
            event.setCancelled(true);
            new PickaxeMenu(player);
        }
    }

    @EventHandler
    public void onPlayerJoinLoadPickaxeData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerPickaxeService.loadPickaxeDataFromYAML(player);
        PlayerBalanceService.loadBalanceFromYAML(player); // TODO: Temp location for this method call
    }

    @EventHandler
    public void onPlayerLeaveSavePickaxeData(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerPickaxeService.savePickaxeDataToYAML(player);
        PlayerBalanceService.saveBalanceDataToYAML(player); // TODO: Temp location for this method call
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) return;

        if (args.length == 0) {
            new PickaxeMenu(player);

        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("menu")) {
                new PickaxeMenu(player);

            } else if (args[0].equalsIgnoreCase("get")) {
                player.getInventory().addItem(PlayerPickaxeService.getPlayerPickaxe(player));
                player.sendMessage(text("You received your pickaxe!", GREEN));

            } else if (args[0].equalsIgnoreCase("reset")) {
                PlayerPickaxeService.resetPickaxeData(player);
                player.sendMessage(text("Pickaxe data reset!", GREEN));
            }
        } else {
            player.sendMessage(text("Usage: /pickaxe <get|reset>", RED));
        }
    }
}