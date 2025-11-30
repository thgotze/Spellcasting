package com.gotze.spellcasting.bossbar;

import com.gotze.spellcasting.util.Rarity;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class LootCrateFeature implements Listener {

    private static final Map<Player, BossBar> playerBossBar = new HashMap<>();
    private static final Map<Player, Integer> playerEnergy = new HashMap<>();
    private static final Map<Player, LootCrate> playerActiveLootCrate = new HashMap<>();
    private static final Map<Player, Set<Block>> playerLootCratesInWorld = new HashMap<>();

    public static void processBlockBreak(Player player, Block block) {
        int energyFromBlock = switch (block.getType()) {
            case COPPER_ORE -> 3;
            case DEEPSLATE_COPPER_ORE -> 6;
            case RAW_COPPER_BLOCK -> 9;
            case IRON_ORE -> 5;
            case DEEPSLATE_IRON_ORE -> 10;
            case RAW_IRON_BLOCK -> 15;
            case GOLD_ORE -> 7;
            case DEEPSLATE_GOLD_ORE -> 14;
            case RAW_GOLD_BLOCK -> 21;
            case DIAMOND_ORE -> 20;
            case DEEPSLATE_DIAMOND_ORE -> 40;
            default -> 1;
        };

        int currentEnergy = playerEnergy.getOrDefault(player, 0);
        int totalEnergy = currentEnergy + energyFromBlock;

        LootCrate activeLootCrate = playerActiveLootCrate.get(player);
        int requiredEnergy = activeLootCrate.getRequiredEnergy();

        if (totalEnergy >= requiredEnergy) {
            totalEnergy -= requiredEnergy;
            spawnLootCrate(player, activeLootCrate);
        }
        playerEnergy.put(player, totalEnergy);

        BossBar bossBar = playerBossBar.get(player);
        bossBar.name(activeLootCrate.computeBossBarName(totalEnergy));
        bossBar.progress((float) totalEnergy / activeLootCrate.getRequiredEnergy());
        bossBar.color(activeLootCrate.getBossBarColor());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BossBar bossBar = playerBossBar.get(player);
        if (bossBar != null) {
            player.showBossBar(bossBar);
            return;
        }

        LootCrate randomLootCrate = LootCrate.getRandom();
        bossBar = randomLootCrate.initializeBossBar();
        player.showBossBar(bossBar);

        playerBossBar.put(player, bossBar);
        playerEnergy.put(player, 0);
        playerActiveLootCrate.put(player, randomLootCrate);
        playerLootCratesInWorld.put(player, new HashSet<>());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLeftClickLootCrate(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (!playerLootCratesInWorld.get(event.getPlayer()).contains(block)) return;

        LootCrate lootCrate = switch (block.getType()) {
            case WHITE_SHULKER_BOX -> LootCrate.COMMON;
            case LIME_SHULKER_BOX -> LootCrate.UNCOMMON;
            case LIGHT_BLUE_SHULKER_BOX -> LootCrate.RARE;
            case MAGENTA_SHULKER_BOX -> LootCrate.EPIC;
            case ORANGE_SHULKER_BOX -> LootCrate.LEGENDARY;
            default -> null;
        };
        if (lootCrate == null) return;


        block.setType(Material.AIR);
        playerLootCratesInWorld.get(event.getPlayer()).remove(block);
        ItemStack loot = switch (ThreadLocalRandom.current().nextInt(4)) {
            case 0 -> ItemStack.of(Material.DIAMOND, 3);
            case 1 -> ItemStack.of(Material.EMERALD, 3);
            case 2 -> ItemStack.of(Material.LAPIS_LAZULI, 3);
            case 3 -> ItemStack.of(Material.REDSTONE, 3);
            default -> null;
        };

        Location blockCenterLocation = block.getLocation().toCenterLocation();

        block.getWorld().dropItemNaturally(blockCenterLocation, loot);
        block.getWorld().playSound(blockCenterLocation, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.35f, 1f);
        block.getWorld().playEffect(blockCenterLocation, Effect.STEP_SOUND, lootCrate.getShulkerBox());
    }

    private static void spawnLootCrate(Player player, LootCrate lootCrate) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.35f, 1f);
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 2f, 1f);

        player.showTitle(lootCrate.getCrateDroppedTitle());

        Block targetBlock = null;

        Location playerLoc = player.getLocation();

        // Try to find a valid location 25 times
        for (int i = 0; i < 25; i++) {

            // Random angle
            double angle = ThreadLocalRandom.current().nextDouble() * (2 * Math.PI);

            // Random distance between 5 and 7
            double radius = 5 + (ThreadLocalRandom.current().nextDouble() * 2.0);

            // Convert polar into block offsets
            int dx = (int) Math.round(Math.cos(angle) * radius);
            int dz = (int) Math.round(Math.sin(angle) * radius);

            // Small height variance (-2 to +2, optional)
            int dy = ThreadLocalRandom.current().nextInt(5) - 2;

            Block candidate = playerLoc.clone().add(dx, dy, dz).getBlock();
            Block below = candidate.getRelative(0, -1, 0);

            // Must be air, and must have solid ground
            if (candidate.getType() == Material.AIR && below.getType().isSolid()) {
                targetBlock = candidate;
                break;
            }
        }

        if (targetBlock == null) {
            Location base = player.getLocation();

            // Search all blocks in the ring 5–7 blocks away
            outer:
            for (int r = 5; r <= 7; r++) {
                for (int x = -r; x <= r; x++) {
                    for (int z = -r; z <= r; z++) {

                        double dist = Math.sqrt(x*x + z*z);
                        if (dist < 5 || dist > 7) continue;  // STRICT ENFORCEMENT

                        Block candidate = base.clone().add(x, 0, z).getBlock();
                        Block below = candidate.getRelative(0, -1, 0);

                        if (candidate.getType() == Material.AIR && below.getType().isSolid()) {
                            targetBlock = candidate;
                            break outer;
                        }
                    }
                }
            }
        }

        if (targetBlock == null) {
            player.sendMessage(text("Target block is null! :(", RED)); // TODO fix later
            return;
        }
        targetBlock.setType(lootCrate.getShulkerBox());
        playerLootCratesInWorld.get(player).add(targetBlock);

        Firework firework = targetBlock.getWorld().spawn(targetBlock.getLocation().add(0.5, 1.0, 0.5), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(lootCrate.getFireworkColor())
                .with(FireworkEffect.Type.BALL)
                .build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();

        Rarity randomRarity = Rarity.getRandom();
        LootCrate randomLootCrate = LootCrate.ofRarity(randomRarity);
        playerActiveLootCrate.put(player, randomLootCrate);
    }
}