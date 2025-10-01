package com.gotze.spellcasting.lootpot;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
import com.gotze.spellcasting.util.BlockUtils;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.Rarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LootPotManager implements Listener {
    private static final float POT_SPAWN_CHANCE = 1.0f / 300; // 1 in 300 chance
    private static final List<List<Loot>> LOOT_POT_LOOT = List.of(
            // Raw Ore
            List.of(new Loot(ItemStack.of(Material.RAW_COPPER), 21, 34),
                    new Loot(ItemStack.of(Material.RAW_IRON), 6, 10),
                    new Loot(ItemStack.of(Material.RAW_GOLD), 6, 10)),
            // Ability tokens
            Stream.of(Ability.AbilityType.values()) // TODO: will need to filter all the types and their rarity and their weight
            .map(abilityType -> new Loot(abilityType.getUpgradeToken(), abilityType.getRarity().getWeight()))
            .toList(),
            // Enchant tokens
            Stream.of(Enchantment.EnchantmentType.values())
            .map(enchantmentType -> new Loot(enchantmentType.getUpgradeToken(), enchantmentType.getRarity().getWeight()))
            .toList(),
            // Machine parts
            List.of( // TODO: placeholder
            new Loot(new ItemStackBuilder(Material.REPEATER)
                    .name(Component.text("Machine Part 1")
                            .color(NamedTextColor.RED))
                    .build(), 0.33),
            new Loot(new ItemStackBuilder(Material.COMPARATOR)
                    .name(Component.text("Machine Part 2")
                            .color(NamedTextColor.RED))
                    .build(), 0.33),
            new Loot(new ItemStackBuilder(Material.POWERED_RAIL)
                    .name(Component.text("Machine Part 3")
                            .color(NamedTextColor.RED))
                    .build(), 0.33)),
            // Money loot
            List.of( // TODO: placeholder
//            new Loot(new ItemStackBuilder(Material.DIAMOND)
//                    .name(Component.text("Valuable item $$$")
//                            .color(NamedTextColor.YELLOW))
//                    .build(), 0.10),
//
//            new Loot(new ItemStackBuilder(Material.EMERALD)
//                    .name(Component.text("Valuable item $$$")
//                            .color(NamedTextColor.YELLOW))
//                    .build(), 0.10),
//
//            new Loot(new ItemStackBuilder(Material.QUARTZ)
//                    .name(Component.text("Valuable item $$$")
//                            .color(NamedTextColor.YELLOW))
//                    .build(), 0.10),

            new Loot(new ItemStackBuilder(Material.AMETHYST_SHARD)
                    .name(Component.text("Valuable item $$$")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.10),

//            new Loot(new ItemStackBuilder(Material.DISC_FRAGMENT_5)
//                    .name(Component.text("Valuable item $$$")
//                            .color(NamedTextColor.YELLOW))
//                    .build(), 0.10),

            new Loot(new ItemStackBuilder(Material.ECHO_SHARD)
                    .name(Component.text("Valuable item $$$")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.10),

            new Loot(new ItemStackBuilder(Material.PRISMARINE_CRYSTALS)
                    .name(Component.text("Valuable item $$$")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.10),

            new Loot(new ItemStackBuilder(Material.PRISMARINE_SHARD)
                    .name(Component.text("Valuable item $$$")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.10),

            new Loot(new ItemStackBuilder(Material.RESIN_CLUMP)
                    .name(Component.text("Valuable item $$$")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.10)),
            List.of(
            new Loot(new ItemStackBuilder(Material.WHITE_SHULKER_BOX)
                    .name(Component.text("Common Loot Box")
                            .color(NamedTextColor.WHITE))
                    .build(), Rarity.COMMON.getWeight()),
//                    .build(), Rarity.COMMON.getWeight() / Rarity.values().length), // TODO: reduce chance

            new Loot(new ItemStackBuilder(Material.LIME_SHULKER_BOX)
                    .name(Component.text("Uncommon Loot Box")
                            .color(NamedTextColor.GREEN))
                    .build(), Rarity.UNCOMMON.getWeight()),

            new Loot(new ItemStackBuilder(Material.LIGHT_BLUE_SHULKER_BOX)
                    .name(Component.text("Rare Loot Box")
                            .color(NamedTextColor.AQUA))
                    .build(), Rarity.RARE.getWeight()),

            new Loot(new ItemStackBuilder(Material.MAGENTA_SHULKER_BOX)
                    .name(Component.text("Epic Loot Box")
                            .color(NamedTextColor.LIGHT_PURPLE))
                    .build(), Rarity.EPIC.getWeight()),

            new Loot(new ItemStackBuilder(Material.ORANGE_SHULKER_BOX)
                    .name(Component.text("Legendary Loot Box")
                            .color(NamedTextColor.GOLD))
                    .build(), Rarity.LEGENDARY.getWeight())
    );
    private static final List<Loot> POT_SHERDS_LOOT = List.of(
            new Loot(new ItemStackBuilder(Material.MINER_POTTERY_SHERD)
                    .lore(Component.text("Collect 4 to craft a lootpot!")
                            .color(NamedTextColor.YELLOW))
                    .build(), 0.50)
    );
    private static final List<Loot> ENCHANTMENT_TOKEN_LOOT = Stream.of(Enchantment.EnchantmentType.values())
            .collect(Collectors.collectingAndThen(
                    Collectors.groupingBy(Enchantment.EnchantmentType::getRarity, Collectors.counting()),
                    counts -> Stream.of(Enchantment.EnchantmentType.values())
                            .map(type -> {
                                Rarity rarity = type.getRarity();
                                double chance = rarity.getWeight() / counts.get(rarity);
                                return new Loot(type.getUpgradeToken(), chance);
                            })
                            .toList()
            ));

    @EventHandler
    public void onLootPotBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DECORATED_POT) return;

        DecoratedPot pot = (DecoratedPot) block.getState();
        Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);
        if (sherdType != Material.MINER_POTTERY_SHERD) return;

        event.setDropItems(false);

        List<ItemStack> itemsInPot = new ArrayList<>();

        for (Loot loot : RAW_ORE_LOOT) {
            loot.rollChance().ifPresent(itemsInPot::add);
        }

        for (Loot loot : ABILITY_TOKENS) {
            var token = loot.rollChance();
            if (token.isPresent()) {
                itemsInPot.add(token.get());
                break;
            }
        }

        for (Loot loot : ENCHANT_TOKENS) {
            var token = loot.rollChance();
            if (token.isPresent()) {
                itemsInPot.add(token.get());
                break;
            }
        }

        for (Loot loot : MACHINE_PARTS) {
            loot.rollChance().ifPresent(itemsInPot::add);
        }

        for (Loot loot : MONEY_LOOT) {
            loot.rollChance().ifPresent(itemsInPot::add);
        }

        for (Loot loot : LOOT_BOXES) {
            loot.rollChance().ifPresent(itemsInPot::add);
        }

        for (Loot loot : POT_SHERDS_LOOT) {
            loot.rollChance().ifPresent(itemsInPot::add);
        }

        for (Loot loot : ENCHANTMENT_TOKEN_LOOT) {
            var token = loot.rollChance();
            if (token.isPresent()) {
                itemsInPot.add(token.get());
                break;
            }
        }

        event.getPlayer().sendMessage(Component.text("You uncovered a lootpot!")
                .color(NamedTextColor.GREEN));

        for (ItemStack itemStack : itemsInPot) {
            block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), itemStack);
            Component displayName = itemStack.displayName();
            event.getPlayer().sendMessage(displayName);
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            Enchantment.EnchantmentType[] enchantmentTypes = Enchantment.EnchantmentType.values();
            int enchantmentIndex = ThreadLocalRandom.current().nextInt(enchantmentTypes.length);
            Enchantment.EnchantmentType enchantmentType = enchantmentTypes[enchantmentIndex];
            event.getPlayer().sendMessage(Component.text("Your pickaxe has been enchanted with " + enchantmentType + " for 30 seconds!")
                    .color(NamedTextColor.GREEN));
        }

        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            Ability.AbilityType[] abilityTypes = Ability.AbilityType.values();
            int abilityIndex = ThreadLocalRandom.current().nextInt(abilityTypes.length);
            Ability.AbilityType abilityType = abilityTypes[abilityIndex];
            event.getPlayer().sendMessage(Component.text("Your have received 1 usage of the " + abilityType + " ability!")
                    .color(NamedTextColor.GREEN));
        }

        event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class, orb -> orb.setExperience(10));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreakTrySpawnLootPot(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!PlayerPickaxeService.isPlayerHoldingOwnPickaxe(player, false)) return;
        if (ThreadLocalRandom.current().nextDouble() >= POT_SPAWN_CHANCE) return;

        event.isCancelled()
        Block block = event.getBlock();

        List<Block> candidates = BlockUtils.getBlocksInSquarePattern(block.getRelative(player.getFacing(), 7),
                5, 1, 5);

        Block chosenBlock = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        chosenBlock.setType(Material.DECORATED_POT);

        // Cracked will disallow picking up the pot
        org.bukkit.block.data.type.DecoratedPot potData = (org.bukkit.block.data.type.DecoratedPot) chosenBlock.getBlockData();
        potData.setCracked(true);
        chosenBlock.setBlockData(potData);

        DecoratedPot pot = (DecoratedPot) chosenBlock.getState();
        pot.setSherd(DecoratedPot.Side.FRONT, Material.MINER_POTTERY_SHERD);
        pot.setSherd(DecoratedPot.Side.BACK, Material.MINER_POTTERY_SHERD);
        pot.setSherd(DecoratedPot.Side.RIGHT, Material.MINER_POTTERY_SHERD);
        pot.setSherd(DecoratedPot.Side.LEFT, Material.MINER_POTTERY_SHERD);
        pot.update(true, false);

        Location potLocation = pot.getLocation();
        player.playSound(potLocation, Sound.BLOCK_DECORATED_POT_INSERT, 10.0f, 1.0f);
        player.spawnParticle(Particle.DUST_PLUME, potLocation.clone().add(0.5, 1, 0.5),
                10, 0, 0, 0, 0);
    }

    @EventHandler
    public void onLootPotInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.DECORATED_POT) return;

        DecoratedPot pot = (DecoratedPot) block.getState();
        Material sherd = pot.getSherd(DecoratedPot.Side.FRONT);
        if (sherd != Material.MINER_POTTERY_SHERD) return;

        event.setCancelled(true);
    }
}