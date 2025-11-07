package com.gotze.spellcasting.pickaxe.enchantment;

import com.google.common.collect.Lists;
import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.joml.Matrix4f;

import java.util.EnumMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener {

    private static final BlockData TINTED_GLASS = BlockType.TINTED_GLASS.createBlockData();
    private static final EnumMap<Material, Material> applicableOreTypes = new EnumMap<>(Material.class);

    static {
        applicableOreTypes.put(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_COPPER_ORE, Material.RAW_COPPER_BLOCK);
        applicableOreTypes.put(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_IRON_ORE, Material.RAW_IRON_BLOCK);
        applicableOreTypes.put(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);
        applicableOreTypes.put(Material.DEEPSLATE_GOLD_ORE, Material.RAW_GOLD_BLOCK);
        applicableOreTypes.put(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);
    }

    public EnrichenEnchantment() {
        super(EnchantmentType.ENRICHEN);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        // 5% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;
        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
        candidateBlocks.remove(block);
        candidateBlocks.removeIf(candidate -> !applicableOreTypes.containsKey(candidate.getType()));

        if (candidateBlocks.isEmpty()) return;

        for (Block candidateBlock : candidateBlocks) {

            Location displayLocation = candidateBlock.getLocation().add(1 / 512f, 1 / 512f, 1 / 512f);
            BlockDisplay blockDisplay = (BlockDisplay) block.getWorld().spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
//                blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/8f, 1 - 1/8f, 1 - 1/8f));
//                blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/16f, 1 - 1/16f, 1 - 1/16f));
//                blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/32f, 1 - 1/32f, 1 - 1/32f));
//                blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/64f, 1 - 1/64f, 1 - 1/64f));
//                blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/64f, 1 - 1/64f, 1 - 1/64f));
            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 128f, 1 - 1 / 128f, 1 - 1 / 128f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/256f, 1 - 1/256f, 1 - 1/256f));
            blockDisplay.setBlock(TINTED_GLASS);
            blockDisplay.setGlowing(true);
            blockDisplay.setGlowColorOverride(Color.LIME);
            blockDisplay.setVisibleByDefault(false);
            player.showEntity(Spellcasting.getPlugin(), blockDisplay);
            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), blockDisplay::remove, 10);

            var points = getHollowCube(candidateBlock.getLocation(), 0.10);
            for (Location point : points) {
                player.spawnParticle(
                        Particle.DUST,
                        point,
                        0,
                        0, 0, 0,
                        10,
                        new Particle.DustOptions(Color.LIME, 1.00f)
                );
            }
            candidateBlock.setType(applicableOreTypes.get(candidateBlock.getType()));
        }
        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));
    }

    private List<Location> getHollowCube(Location loc, double particleDistance) {
        List<Location> result = Lists.newArrayList();
        World world = loc.getWorld();
        double minX = loc.getBlockX();
        double minY = loc.getBlockY();
        double minZ = loc.getBlockZ();
        double maxX = loc.getBlockX() + 1;
        double maxY = loc.getBlockY() + 1;
        double maxZ = loc.getBlockZ() + 1;

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }
}