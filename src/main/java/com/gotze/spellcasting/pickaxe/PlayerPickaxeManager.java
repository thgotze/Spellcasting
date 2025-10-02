package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.menu.PickaxeMenu;
import com.gotze.spellcasting.util.*;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlayerPickaxeManager implements Listener, BasicCommand {

    @EventHandler(priority = EventPriority.LOWEST)
    public void masterBlockBreakEventHandler(BlockBreakEvent event) {

        Block block = event.getBlock();
        Material blockType = block.getType();
        Player player = event.getPlayer();
        World world = block.getWorld();
        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        if (player.getGameMode() == GameMode.CREATIVE) return;

        // *** check player is holding their pickaxe
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;

        // *** check if pickaxe is about to break, cancel event if so
        if (pickaxeData.getDurabilityDamage() + 1 == pickaxeData.getPickaxeMaterial().getMaxDurability()) {
            event.setCancelled(true);
            player.sendMessage("Warning! Pickaxe has low durability!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
            SoundUtils.playErrorSound(player);
            return;
        }

        // At this point the block break event is allowed to go through i.e. NOT cancelled

        // *** increment block broken counter
        pickaxeData.addBlocksBroken(1);

        // *** update pickaxe lore
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), () -> {
            int durabilityDamage = heldItem.getData(DataComponentTypes.DAMAGE);

            if (durabilityDamage > pickaxeData.getDurabilityDamage()) {
                pickaxeData.setDurabilityDamage(durabilityDamage);
            }
            heldItem.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
        }, 1L);

        // *** check if broken blocks blocktype is an ore
        Loot loot = BlockCategories.ORE_BLOCKS.get(blockType);
        if (loot != null) {
            if (BlockCategories.ORE_BLOCKS.containsKey(blockType)) {
                event.setDropItems(false);
                BlockCategories.ORE_BLOCKS.get(blockType).rollChance().ifPresent(itemStack ->
                        world.dropItemNaturally(block.getLocation().toCenterLocation(), itemStack)
                );
            }
        }


        // *** abilities and enchantments
        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockBreakAware blockBreakAware) {
                blockBreakAware.onBlockBreak(player, event, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockBreakAware blockBreakAware) {
                blockBreakAware.onBlockBreak(player, event, pickaxeData);
            }
        }


//        // *** handle extra broken blocks
//        List<Block> blocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
//        blocks.remove(block);
//
//        for (Block b : blocks) {
//            Material type = b.getType();
//            if (type.isAir()) continue;
//
//            Location blockLocation = b.getLocation().toCenterLocation();
//            world.playEffect(blockLocation, Effect.STEP_SOUND, b.getBlockData());
//            SoundGroup soundGroup = b.getBlockSoundGroup();
//
//            world.playSound(blockLocation, soundGroup.getBreakSound(), soundGroup.getVolume(), soundGroup.getPitch());
//
//            if (BlockCategories.ORE_BLOCKS.containsKey(type)) {
//                BlockCategories.ORE_BLOCKS.get(type).rollChance().ifPresent(itemStack ->
//                        world.dropItemNaturally(blockLocation, itemStack)
//                );
//            }
//            b.setType(Material.AIR, false);
//        }
    }

    @EventHandler
    public void onShiftRightClickHoldingPickaxe(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction().isRightClick() && player.isSneaking())) return;
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;

        event.setCancelled(true);

        new PickaxeMenu(player);
    }

    @EventHandler
    public void onShiftRightClickPickaxeInInventory(InventoryClickEvent event) {
        if (event.getClick() != ClickType.SHIFT_RIGHT) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != player.getInventory()) return;

        // TODO: find better way of knowing if it's the players pickaxe
        if (event.getCurrentItem().getType() != PlayerPickaxeService.getPickaxeData(player).getPickaxeMaterial().getType())
            return;

        new PickaxeMenu(player);
    }


    @EventHandler
    public void onBlockDamageEvent(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
            if (enchantment instanceof BlockDamageAware blockDamageAware) {
                blockDamageAware.onBlockDamage(player, event, pickaxeData);
            }
        }

        for (Ability ability : pickaxeData.getAbilities()) {
            if (ability instanceof BlockDamageAware blockDamageAware) {
                blockDamageAware.onBlockDamage(player, event, pickaxeData);
            }
        }
    }

    @EventHandler
    public void onBlockBreakHandlePickaxeDurability(BlockBreakEvent event) {
//        Player player = event.getPlayer();
//        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
//
//        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
//
//        if (pickaxeData.getDurabilityDamage() + 1 == pickaxeData.getPickaxeMaterial().getMaxDurability()) {
//            event.setCancelled(true);
//            player.sendMessage("Warning! Pickaxe has low durability!");
//            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 2));
//            player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f, 404);
//            SoundUtils.playErrorSound(player);
//            return;
//        }
//
//        pickaxeData.addBlocksBroken(1);
//
//        ItemStack heldItem = player.getInventory().getItemInMainHand();
//
//        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Spellcasting.class), () -> {
//            int durabilityDamage = heldItem.getData(DataComponentTypes.DAMAGE);
//
//            if (durabilityDamage > pickaxeData.getDurabilityDamage()) {
//                pickaxeData.setDurabilityDamage(durabilityDamage);
//            }
//            heldItem.lore(PlayerPickaxeService.getPickaxeLore(pickaxeData));
//        }, 1L);
    }

    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
//        Block block = event.getBlock();
//        Material blockType = block.getType();
//
//        if (!BlockCategories.ORE_BLOCKS.containsKey(blockType)) return;
//
//        event.setCancelled(true);
//        event.setDropItems(false);
//
//        block.breakNaturally();
//
//        BlockCategories.ORE_BLOCKS.get(blockType).rollChance().ifPresent(itemStack ->
//                block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), itemStack)
//        );
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
//        Player player = event.getPlayer();
//        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
//
//        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);
//
//        for (Enchantment enchantment : pickaxeData.getEnchantments()) {
//            if (enchantment instanceof BlockBreakAware blockBreakAware) {
//                blockBreakAware.onBlockBreak(player, event, pickaxeData);
//            }
//        }
//
//        for (Ability ability : pickaxeData.getAbilities()) {
//            if (ability instanceof BlockBreakAware blockBreakAware) {
//                blockBreakAware.onBlockBreak(player, event, pickaxeData);
//            }
//        }
    }

    @EventHandler
    public void onHandSwapActivatePickaxeAbilities(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
        event.setCancelled(true);

        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

        for (Ability ability : pickaxeData.getAbilities()) {
            ability.activate(player, pickaxeData);
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

        player.sendMessage(Component.text("Unknown subcommand. Usage: /pickaxe <get|reset>")
                .color(NamedTextColor.RED));
    }
}