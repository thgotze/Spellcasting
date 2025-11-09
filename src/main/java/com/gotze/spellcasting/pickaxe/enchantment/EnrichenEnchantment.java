package com.gotze.spellcasting.pickaxe.enchantment;

import com.destroystokyo.paper.ParticleBuilder;
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
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.EnumMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class EnrichenEnchantment extends Enchantment implements BlockBreakListener {
    private static final BlockData ENRICHEN_TINTED_GLASS = BlockType.TINTED_GLASS.createBlockData();
    private static final ParticleBuilder ENRICHEN_PARTICLE = new ParticleBuilder(Particle.DUST)
            .count(0)
            .data(new Particle.DustOptions(Color.LIME, 1.0f))
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
            // Block display
            Location displayLocation = candidateBlock.getLocation().add(1 / 512f, 1 / 512f, 1 / 512f);
            BlockDisplay blockDisplay = (BlockDisplay) block.getWorld().spawnEntity(displayLocation, EntityType.BLOCK_DISPLAY);
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 8f, 1 - 1 / 8f, 1 - 1 / 8f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 16f, 1 - 1 / 16f, 1 - 1 / 16f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 32f, 1 - 1 / 32f, 1 - 1 / 32f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 64f, 1 - 1 / 64f, 1 - 1 / 64f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 64f, 1 - 1 / 64f, 1 - 1 / 64f));
            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1 / 128f, 1 - 1 / 128f, 1 - 1 / 128f));
//            blockDisplay.setTransformationMatrix(new Matrix4f().scale(1 - 1/256f, 1 - 1/256f, 1 - 1/256f));
            blockDisplay.setBlock(ENRICHEN_TINTED_GLASS);
            blockDisplay.setGlowing(true);
            blockDisplay.setGlowColorOverride(Color.LIME);
            blockDisplay.setVisibleByDefault(false);
            player.showEntity(Spellcasting.getPlugin(), blockDisplay);
            Bukkit.getScheduler().runTaskLater(Spellcasting.getPlugin(), blockDisplay::remove, 10);

            // Particle outline
            List<Location> particlePoints = BlockUtils.getBlockOutlineForParticles(candidateBlock.getLocation(), 0.10);
            for (Location point : particlePoints) {
                ENRICHEN_PARTICLE.clone()
                        .location(point)
                        .receivers(player)
                        .spawn();
            }
            candidateBlock.setType(applicableOreTypes.get(candidateBlock.getType()));

            // Particle line
            Location playerEyeLocation = player.getEyeLocation().clone();
            playerEyeLocation.add(playerEyeLocation.getDirection().multiply(1)); // offset forward a bit

            Location target = candidateBlock.getLocation().toCenterLocation();

            Vector direction = target.toVector().subtract(playerEyeLocation.toVector());
            double distance = direction.length();
            direction.normalize();

            double step = 0.1; // distance between particles
            int particles = (int) (distance / step);

            for (int i = 0; i < particles; i++) {
                Vector point = playerEyeLocation.toVector().add(direction.clone().multiply(i * step));
                Location particleLocation = point.toLocation(player.getWorld());
                ENRICHEN_PARTICLE.clone()
                        .location(particleLocation)
                        .receivers(player)
                        .spawn();
            }
        }
        player.sendActionBar(getEnchantmentType().getFormattedName().append(text(" activated")));
    }
}