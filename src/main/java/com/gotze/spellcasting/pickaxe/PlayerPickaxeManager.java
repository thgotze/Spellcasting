package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.menu.PickaxeMenu;
import com.gotze.spellcasting.util.BlockBreakAware;
import com.gotze.spellcasting.util.BlockCategories;
import com.gotze.spellcasting.util.BlockDamageAware;
import com.gotze.spellcasting.util.BlockUtils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerPickaxeManager implements Listener, BasicCommand {

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
    public void masterBlockBreakEventHandler(BlockBreakEvent event) {

        // *** ore breaking
        Block block = event.getBlock();
        Material blockType = block.getType();
        Player player = event.getPlayer();
        World world = block.getWorld();

        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;

        if (!BlockCategories.ORE_BLOCKS.containsKey(blockType)) return;

        BlockCategories.ORE_BLOCKS.get(blockType).rollChance().ifPresent(itemStack ->
                world.dropItemNaturally(block.getLocation().toCenterLocation(), itemStack)
        );
        block.setType(Material.AIR, false);


        List<Block> blocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
        blocks.remove(block);

        for (Block b : blocks) {
            Material type = b.getType();
            if (type.isAir()) continue;

            Location blockLocation = b.getLocation().toCenterLocation();
            world.playEffect(blockLocation, Effect.STEP_SOUND, b.getBlockData());
            SoundGroup soundGroup = b.getBlockSoundGroup();

            world.playSound(blockLocation, soundGroup.getBreakSound(), soundGroup.getVolume(), soundGroup.getPitch());

            if (BlockCategories.ORE_BLOCKS.containsKey(type)) {
                BlockCategories.ORE_BLOCKS.get(type).rollChance().ifPresent(itemStack ->
                        world.dropItemNaturally(blockLocation, itemStack)
                );
            }
            b.setType(Material.AIR, false);

        }


        player.sendMessage("aaa");

        // *** durability
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


        // *** abilities and enchantments
//        Player player = event.getPlayer();
//        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
//
        PickaxeData pickaxeData = PlayerPickaxeService.getPickaxeData(player);

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


        // *** lootpot breaking
//        Block block = event.getBlock();
//        if (block.getType() != Material.DECORATED_POT) return;
//
//        DecoratedPot pot = (DecoratedPot) block.getState();
//        Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);
//        if (sherdType != Material.MINER_POTTERY_SHERD) return;
//
//        event.setDropItems(false);
//
//        List<ItemStack> itemsInPot = new ArrayList<>();
//
//        for (Loot loot : RAW_ORE_LOOT) {
//            loot.rollChance().ifPresent(itemsInPot::add);
//        }
//
//        for (Loot loot : ABILITY_TOKENS) {
//            var token = loot.rollChance();
//            if (token.isPresent()) {
//                itemsInPot.add(token.get());
//                break;
//            }
//        }
//
//        for (Loot loot : ENCHANT_TOKENS) {
//            var token = loot.rollChance();
//            if (token.isPresent()) {
//                itemsInPot.add(token.get());
//                break;
//            }
//        }
//
//        for (Loot loot : MACHINE_PARTS) {
//            loot.rollChance().ifPresent(itemsInPot::add);
//        }
//
//        for (Loot loot : MONEY_LOOT) {
//            loot.rollChance().ifPresent(itemsInPot::add);
//        }
//
//        for (Loot loot : LOOT_BOXES) {
//            loot.rollChance().ifPresent(itemsInPot::add);
//        }
//
//        for (Loot loot : POT_SHERDS_LOOT) {
//            loot.rollChance().ifPresent(itemsInPot::add);
//        }
//
//        for (Loot loot : ENCHANTMENT_TOKEN_LOOT) {
//            var token = loot.rollChance();
//            if (token.isPresent()) {
//                itemsInPot.add(token.get());
//                break;
//            }
//        }
//
//        event.getPlayer().sendMessage(Component.text("You uncovered a lootpot!")
//                .color(NamedTextColor.GREEN));
//
//        for (ItemStack itemStack : itemsInPot) {
//            block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), itemStack);
//            Component displayName = itemStack.displayName();
//            event.getPlayer().sendMessage(displayName);
//        }
//
//        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
//            Enchantment.EnchantmentType[] enchantmentTypes = Enchantment.EnchantmentType.values();
//            int enchantmentIndex = ThreadLocalRandom.current().nextInt(enchantmentTypes.length);
//            Enchantment.EnchantmentType enchantmentType = enchantmentTypes[enchantmentIndex];
//            event.getPlayer().sendMessage(Component.text("Your pickaxe has been enchanted with " + enchantmentType + " for 30 seconds!")
//                    .color(NamedTextColor.GREEN));
//        }
//
//        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
//            Ability.AbilityType[] abilityTypes = Ability.AbilityType.values();
//            int abilityIndex = ThreadLocalRandom.current().nextInt(abilityTypes.length);
//            Ability.AbilityType abilityType = abilityTypes[abilityIndex];
//            event.getPlayer().sendMessage(Component.text("Your have received 1 usage of the " + abilityType + " ability!")
//                    .color(NamedTextColor.GREEN));
//        }
//
//        event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class, orb -> orb.setExperience(10));


        // *** loot pot spawning
//        Player player = event.getPlayer();
//        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
//        if (ThreadLocalRandom.current().nextDouble() >= POT_SPAWN_CHANCE) return;
//
//        Block block = event.getBlock();
//
//        List<Block> candidates = BlockUtils.getBlocksInSquarePattern(block.getRelative(player.getFacing(), 7),
//                5, 1, 5);
//
//        Block chosenBlock = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
//        chosenBlock.setType(Material.DECORATED_POT);
//
//        // Cracked will disallow picking up the pot
//        org.bukkit.block.data.type.DecoratedPot potData = (org.bukkit.block.data.type.DecoratedPot) chosenBlock.getBlockData();
//        potData.setCracked(true);
//        chosenBlock.setBlockData(potData);
//
//        DecoratedPot pot = (DecoratedPot) chosenBlock.getState();
//        pot.setSherd(DecoratedPot.Side.FRONT, Material.MINER_POTTERY_SHERD);
//        pot.setSherd(DecoratedPot.Side.BACK, Material.MINER_POTTERY_SHERD);
//        pot.setSherd(DecoratedPot.Side.RIGHT, Material.MINER_POTTERY_SHERD);
//        pot.setSherd(DecoratedPot.Side.LEFT, Material.MINER_POTTERY_SHERD);
//        pot.update(true, false);
//
//        Location potLocation = pot.getLocation();
//        player.playSound(potLocation, Sound.BLOCK_DECORATED_POT_INSERT, 10.0f, 1.0f);
//        player.spawnParticle(Particle.DUST_PLUME, potLocation.clone().add(0.5, 1, 0.5),
//                10, 0, 0, 0, 0);
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