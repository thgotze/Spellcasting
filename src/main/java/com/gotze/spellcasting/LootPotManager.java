//package com.gotze.spellcasting;
//
//import com.gotze.spellcasting.pickaxe.ability.Ability;
//import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
//import com.gotze.spellcasting.pickaxe.PlayerPickaxeService;
//import com.gotze.spellcasting.util.ItemStackBuilder;
//import com.gotze.spellcasting.util.Loot;
//import com.gotze.spellcasting.util.Rarity;
//import com.gotze.spellcasting.util.block.BlockUtils;
//import net.kyori.adventure.text.Component;
//import org.bukkit.*;
//import org.bukkit.block.Block;
//import org.bukkit.block.DecoratedPot;
//import org.bukkit.entity.ExperienceOrb;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static net.kyori.adventure.text.Component.text;
//import static net.kyori.adventure.text.format.NamedTextColor.*;
//
//public class LootPotManager implements Listener {
//    private static final float POT_SPAWN_CHANCE = 1.0f / 300; // 1 in 300 chance
//    private static final List<List<Loot>> LOOT_POT_LOOT = List.of(
//            // Raw Ore
//            List.of(new Loot(ItemStack.of(Material.RAW_COPPER), 21, 34),
//                    new Loot(ItemStack.of(Material.RAW_IRON), 6, 10),
//                    new Loot(ItemStack.of(Material.RAW_GOLD), 6, 10)),
//            // Ability tokens
//            Stream.of(Ability.AbilityType.values()) // TODO: will need to filter all the types and their rarity and their weight
//                    .map(abilityType -> new Loot(abilityType.getUpgradeToken(), abilityType.getRarity().getWeight()))
//                    .toList(),
//            // Enchantment tokens
//            Stream.of(Enchantment.EnchantmentType.values())
//                    .map(enchantmentType -> new Loot(enchantmentType.getUpgradeToken(), enchantmentType.getRarity().getWeight()))
//                    .toList(),
//            // Machine parts
//            List.of(new Loot(new ItemStackBuilder(Material.REPEATER)
//                            .name(text("Machine Part 1", RED))
//                            .build(), 0.33),
//                    new Loot(new ItemStackBuilder(Material.COMPARATOR)
//                            .name(text("Machine Part 2", RED))
//                            .build(), 0.33),
//                    new Loot(new ItemStackBuilder(Material.POWERED_RAIL)
//                            .name(text("Machine Part 3", RED))
//                            .build(), 0.33)
//            ),
//            // Money loot
//            List.of(new Loot(new ItemStackBuilder(Material.AMETHYST_SHARD)
//                            .name(text("Valuable item $$$", YELLOW))
//                            .build(), 0.10),
//                    new Loot(new ItemStackBuilder(Material.ECHO_SHARD)
//                            .name(text("Valuable item $$$", YELLOW))
//                            .build(), 0.10),
//                    new Loot(new ItemStackBuilder(Material.PRISMARINE_CRYSTALS)
//                            .name(text("Valuable item $$$", YELLOW))
//                            .build(), 0.10),
//                    new Loot(new ItemStackBuilder(Material.PRISMARINE_SHARD)
//                            .name(text("Valuable item $$$", YELLOW))
//                            .build(), 0.10),
//                    new Loot(new ItemStackBuilder(Material.RESIN_CLUMP)
//                            .name(text("Valuable item $$$", YELLOW))
//                            .build(), 0.10)
//            ),
//            // Lootboxes
//            List.of(new Loot(new ItemStackBuilder(Material.WHITE_SHULKER_BOX)
//                            .name(text("Common Loot Box", WHITE))
//                            .build(),
//                            Rarity.COMMON.getWeight()),
////                    Rarity.COMMON.getWeight() / Rarity.values().length), // TODO: reduce chance
//                    new Loot(new ItemStackBuilder(Material.LIME_SHULKER_BOX)
//                            .name(text("Uncommon Loot Box", GREEN))
//                            .build(), Rarity.UNCOMMON.getWeight()),
//                    new Loot(new ItemStackBuilder(Material.LIGHT_BLUE_SHULKER_BOX)
//                            .name(text("Rare Loot Box", AQUA))
//                            .build(), Rarity.RARE.getWeight()),
//                    new Loot(new ItemStackBuilder(Material.MAGENTA_SHULKER_BOX)
//                            .name(text("Epic Loot Box", LIGHT_PURPLE))
//                            .build(), Rarity.EPIC.getWeight()),
//                    new Loot(new ItemStackBuilder(Material.ORANGE_SHULKER_BOX)
//                            .name(text("Legendary Loot Box", GOLD))
//                            .build(), Rarity.LEGENDARY.getWeight())),
//            // Pot sherds
//            // ---------------
//            List.of(new Loot(new ItemStackBuilder(Material.MINER_POTTERY_SHERD)
//                    .lore(text("Collect 4 to craft a lootpot!", YELLOW))
//                    .build(), 0.50)
//            ),
//            // Enchantment tokens again? // TODO: figure out which one to use, this or the one above
//            Stream.of(Enchantment.EnchantmentType.values())
//                    .collect(Collectors.collectingAndThen(
//                            Collectors.groupingBy(Enchantment.EnchantmentType::getRarity, Collectors.counting()),
//                            counts -> Stream.of(Enchantment.EnchantmentType.values())
//                                    .map(enchantmentType -> {
//                                        Rarity rarity = enchantmentType.getRarity();
//                                        double chance = rarity.getWeight() / counts.get(rarity);
//                                        return new Loot(enchantmentType.getUpgradeToken(), chance);
//                                    }).toList()
//                    ))
//    );
//
//    @EventHandler
//    public void onLootPotBreak(BlockBreakEvent event) {
//        Block block = event.getBlock();
//        if (block.getType() != Material.DECORATED_POT) return;
//
//        DecoratedPot pot = (DecoratedPot) block.getState();
//        Material sherdType = pot.getSherd(DecoratedPot.Side.FRONT);
//        if (sherdType != Material.MINER_POTTERY_SHERD) return;
//
//        Player player = event.getPlayer();
//
//        event.setDropItems(false);
//
//        List<ItemStack> itemsInPot = new ArrayList<>();
//
//        for (List<Loot> lootList : LOOT_POT_LOOT) {
//            for (Loot loot : lootList) {
//                loot.rollDrop().ifPresent(itemsInPot::add);
//            }
//        }
//
//        player.sendMessage(text("You uncovered a lootpot!", GREEN));
//
//        Location blockCenterLocation = block.getLocation().toCenterLocation();
//        World world = block.getWorld();
//        for (ItemStack itemStack : itemsInPot) {
//            world.dropItemNaturally(blockCenterLocation, itemStack);
//            Component displayName = itemStack.displayName();
//            player.sendMessage(displayName);
//        }
//
//        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
//            Enchantment.EnchantmentType[] enchantmentTypes = Enchantment.EnchantmentType.values();
//            int enchantmentIndex = ThreadLocalRandom.current().nextInt(enchantmentTypes.length);
//            Enchantment.EnchantmentType enchantmentType = enchantmentTypes[enchantmentIndex];
//            player.sendMessage(text("Your pickaxe has been enchanted with " + enchantmentType + " for 30 seconds!", GREEN));
//        }
//
//        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
//            Ability.AbilityType[] abilityTypes = Ability.AbilityType.values();
//            int abilityIndex = ThreadLocalRandom.current().nextInt(abilityTypes.length);
//            Ability.AbilityType abilityType = abilityTypes[abilityIndex];
//            player.sendMessage(text("Your have received 1 usage of the " + abilityType + " ability!", GREEN));
//        }
//
//        world.spawn(blockCenterLocation, ExperienceOrb.class, orb -> orb.setExperience(10));
//    }
//
//    @EventHandler
//    public void onBlockBreakTrySpawnLootPot(BlockBreakEvent event) {
//        Player player = event.getPlayer();
//        ItemStack pickaxe = PlayerPickaxeService.getPlayerPickaxeFromMainHand(player, false);
//        if (pickaxe == null) return;
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
//        // cracked will disallow picking up the pot
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
//    }
//
//    @EventHandler
//    public void onLootPotInteract(PlayerInteractEvent event) {
//        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
//
//        Block block = event.getClickedBlock();
//        if (block == null || block.getType() != Material.DECORATED_POT) return;
//
//        DecoratedPot pot = (DecoratedPot) block.getState();
//        Material sherd = pot.getSherd(DecoratedPot.Side.FRONT);
//        if (sherd != Material.MINER_POTTERY_SHERD) return;
//
//        event.setCancelled(true);
//    }
//}