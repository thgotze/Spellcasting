package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.menu.PickaxeMenu;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.block.BlockCategories;
import io.papermc.paper.datacomponent.DataComponentTypes;
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
import org.bukkit.event.inventory.InventoryClickEvent;
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

public class PlayerPickaxeManager implements Listener, BlockBreaker {
    private static final Map<Player, Integer> selectedAbilityIndex = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreakWithPickaxe(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check player is holding their pickaxe
        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
        if (pickaxe == null) return;

        // Check if pickaxe is about to break, cancel event if so
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);
        if (pickaxeData.getDurabilityDamage() + 1 == pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            event.setCancelled(true);
            player.sendMessage(text("Pickaxe durability too low to continue mining!", RED));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the block break event is allowed to go through i.e., NOT canceled
        Block block = event.getBlock();

        // Handles the block break itself: increments blocks broken, drops items and notifies all block break listeners
        breakBlock(player, block, pickaxeData, true);

        // Remove default ore drops
        if (BlockCategories.ORE_BLOCKS.containsKey(block.getType())) {
            event.setDropItems(false);
        }

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
        player.sendActionBar(text("Selected ability: ").color(YELLOW).append(selectedAbility.getAbilityType().getFormattedName()));
        player.sendActionBar(text("Selected ability: ", YELLOW)
                .append(selectedAbility.getAbilityType().getFormattedName()));
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
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        int index = selectedAbilityIndex.getOrDefault(player, 0);

        Ability selectedAbility = abilities.get(index);
        if (selectedAbility != null) {
            selectedAbility.activateAbility(player, pickaxeData);
        }
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
            Bukkit.getScheduler().runTask(Spellcasting.getPlugin(), () -> new PickaxeMenu(player));
        }
    }
}