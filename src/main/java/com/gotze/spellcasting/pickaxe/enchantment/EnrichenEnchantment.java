package com.gotze.spellcasting.pickaxe.enchantment;

import com.destroystokyo.paper.ParticleBuilder;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener {
    private static final BlockData ENRICHEN_TINTED_GLASS = BlockType.TINTED_GLASS.createBlockData();
    private static final ParticleBuilder ENRICHEN_PARTICLE = new ParticleBuilder(Particle.DUST)
            .count(0)
            .data(new Particle.DustOptions(Color.ORANGE, 1.0f))
            .offset(0, 0, 0)
            .extra(0);
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

    private final Map<Block, BlockDisplay> markedBlocks = new HashMap<>();

    public EnrichenEnchantment() {
        super(EnchantmentType.ENRICHEN);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        markedBlocks.entrySet().removeIf(entry -> {
            if (entry.getKey().equals(block)) {
                BlockDisplay display = entry.getValue();
                display.remove();
                return true;
            }
            return false;
        });

        if (!isNaturalBreak) return;
        // 5% activation chance
//        if (ThreadLocalRandom.current().nextDouble() > 0.05) return;

        List<Block> candidateBlocks = BlockUtils.getBlocksInSquarePattern(block, 3, 3, 3);
        candidateBlocks.remove(block);
        candidateBlocks.removeIf(candidate -> !applicableOreTypes.containsKey(candidate.getType()));
        if (candidateBlocks.isEmpty()) return;

        for (Block candidateBlock : candidateBlocks) {
//            // Block display
//            Location displayLocation = candidateBlock.getLocation().add(1 / 512f, 1 / 512f, 1 / 512f);
//            BlockDisplay blockDisplay = (BlockDisplay) block.getWorld().spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 128f, 1 - 1 / 128f, 1 - 1 / 128f));
//
//            blockDisplay.setBlock(ENRICHEN_TINTED_GLASS);
//            blockDisplay.setGlowing(true);
//            blockDisplay.setGlowColorOverride(Color.ORANGE);
//
//            blockDisplay.setVisibleByDefault(false);
//            player.showEntity(Spellcasting.getPlugin(), blockDisplay);
//            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), blockDisplay::remove, 10);
//
//            markedBlocks.put(candidateBlock, blockDisplay);

            // Particle outline
            List<Location> particlePoints = BlockUtils.getBlockOutlineForParticles(candidateBlock.getLocation(), 0.10);
            for (Location point : particlePoints) {
                ENRICHEN_PARTICLE.clone()
                        .location(point)
                        .spawn();
            }
            candidateBlock.setType(applicableOreTypes.get(candidateBlock.getType()));

            // Particle line
//            Vector playerDirection = player.getLocation().getDirection();
//            Vector rightVector = playerDirection.crossProduct(new Vector(0, 1, 0)).normalize();
//            Vector rightOffset = rightVector.multiply(player.getWidth() / 1.8);
//            Location startLocation = player.getEyeLocation().add(rightOffset);
//
//            Location endLocation = candidateBlock.getLocation().toCenterLocation();
//
//            double lineDistance = startLocation.distance(endLocation);
//            double particleDistance = 0.05;
//            int particleCount = (int) (lineDistance / particleDistance);
//
//            Vector directionVector = endLocation.toVector().subtract(startLocation.toVector()).normalize();
//
//            for (int i = 0; i <= particleCount; i++) {
//                ENRICHEN_PARTICLE.clone()
//                        .location(startLocation.clone().add(directionVector.clone().multiply(i * particleDistance)))
//                        .spawn();
//            }
        }
    }
}