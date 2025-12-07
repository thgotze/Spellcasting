package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.bossbar.LootCrateFeature;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageAbortListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDropItemListener;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.menu.YourPickaxeMenu;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.block.BlockCategories;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class PlayerPickaxeManager implements Listener {
    private final Map<Player, Integer> selectedAbilityIndex = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreakWithPickaxe(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Check player is holding their own pickaxe
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) {
            event.setCancelled(true);
            player.sendActionBar(text("This is not your pickaxe!", RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20, 2));
            SoundUtils.playBassNoteBlockErrorSound(player);
            return;
        }

        // Check if pickaxe is about to break, cancel event if so
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        if (pickaxeData.getDurabilityDamage() + 1 >= pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            event.setCancelled(true);
            player.sendActionBar(text("Durability too low to continue mining!", RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20, 2));
            SoundUtils.playBassNoteBlockErrorSound(player);
            return;
        }

        // At this point the block break event is allowed to go through i.e., NOT canceled
        Block block = event.getBlock();

        // Increment blocks broken
        pickaxeData.addBlocksBroken(1);

        // Notify all block break listeners of this natural block break
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

        LootCrateFeature.applyEnergyFromBlockBreak(player, block);

        // Update pickaxe durability and lore a tick later
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

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

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
    public void onAbortBlockDamageEvent(BlockDamageAbortEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockDamageAbortListener blockDamageAbortListener) {
                blockDamageAbortListener.onBlockDamageAbort(player, event, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockDamageAbortListener blockDamageAbortListener) {
                blockDamageAbortListener.onBlockDamageAbort(player, event, pickaxeData);
            }
        }
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        BlockState blockState = event.getBlockState();
        List<Item> droppedItems = event.getItems();
        if (droppedItems.isEmpty()) return;

        Loot oreLoot = BlockCategories.ORE_BLOCKS.get(blockState.getType());
        if (oreLoot != null) {
            droppedItems.getFirst().setItemStack(oreLoot.drop());
        }

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockDropItemListener blockDropItemListener) {
                blockDropItemListener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockDropItemListener blockDropItemListener) {
                blockDropItemListener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
            }
        }
    }

    @EventHandler
    public void onHandSwapCycleAbility(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        int current = selectedAbilityIndex.getOrDefault(player, 0);
        int next = (current + 1) % abilities.size();
        selectedAbilityIndex.put(player, next);

        Ability selectedAbility = abilities.get(next);
        player.sendActionBar(text("Selected ability: ", YELLOW).append(selectedAbility.getAbilityType().getFormattedName()));
    }

    @EventHandler
    public void onShiftRightClickActivateAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().isRightClick()) return;
        if (!player.isSneaking()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        event.setCancelled(true);

        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        List<Ability> abilities = pickaxeData.getAbilities();
        if (abilities.isEmpty()) return;

        if (pickaxeData.getDurabilityDamage() + 20 >= pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            player.sendMessage(text("Pickaxe durability too low to activate ability", RED));
            SoundUtils.playBassNoteBlockErrorSound(player);
            SoundUtils.playVillagerErrorSound(player);
            return;
        }

        int index = selectedAbilityIndex.getOrDefault(player, 0);

        Ability selectedAbility = abilities.get(index);
        if (selectedAbility != null) {
            selectedAbility.activateAbility(player, pickaxeData);
        }
    }

    @EventHandler
    public void onTryDropPickaxe(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(event.getItemDrop().getItemStack(), player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickPickaxeInInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Shift right-clicking the player's pickaxe in the player's own inventory cancels the event and opens the pickaxe menu
        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) {
                    event.setCancelled(true);

                    if (event.getInventory().getHolder() instanceof Player) {
                        Bukkit.getScheduler().runTask(Spellcasting.getPlugin(), () -> new YourPickaxeMenu(player));
                    }
                }
            }
            return;
        }

        // Trying to swap the player pickaxe's inventory slot location is only allowed within
        // the player's own inventory and only in the 9 hotbar slots or the offhand slot
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            ItemStack clickedItem = player.getInventory().getItem(event.getHotbarButton());
            if (clickedItem != null) {
                if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) {
                    event.setCancelled(true);

                    if (event.getInventory().getHolder() instanceof Player) {
                        if (event.getRawSlot() >= 36 && event.getRawSlot() <= 45) {
                            event.setCancelled(false);
                        }
                    }
                }
            }
            return;
        }

        // Trying to interact with the player pickaxe in any other way cancels the event
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null) {
            if (PlayerPickaxeService.isItemStackPlayerOwnPickaxe(clickedItem, player)) {
                event.setCancelled(true);
            }
        }
    }
}