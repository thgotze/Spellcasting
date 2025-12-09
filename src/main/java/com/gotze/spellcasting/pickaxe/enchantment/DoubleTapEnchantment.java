package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.feature.lootcrate.LootCrateManager;
import com.gotze.spellcasting.feature.mines.MineManager;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.capability.BlockDropItemListener;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.gotze.spellcasting.Spellcasting.plugin;

public class DoubleTapEnchantment extends Enchantment implements BlockBreakListener, BlockDamageListener, BlockBreaker {

    private BlockFace blockFace;
    private long cooldown;

    public DoubleTapEnchantment() {
        super(EnchantmentType.DOUBLE_TAP);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!isNaturalBreak) return;
        if (player.isSneaking()) return;
        if (System.currentTimeMillis() < cooldown) return;

        Block blockBehind = block.getRelative(blockFace.getOppositeFace());
        if (blockBehind.isEmpty()) return;
        if (!MineManager.isInAnyMine(blockBehind)) return;

        this.cooldown = System.currentTimeMillis() + (11000L - getLevel() * 1000L); // 10s, 9s, 8s, 7s, 6s

        new BukkitRunnable() {
            @Override
            public void run() {
                Location blockBehindLocation = blockBehind.getLocation();
                for (Location blockOutlineForParticle : BlockUtils.getBlockOutlineForParticles(blockBehindLocation, 0.10)) {
                    player.spawnParticle(Particle.OMINOUS_SPAWNING, blockOutlineForParticle, 0, 0, 0, 0, 0);
                }

                player.playSound(blockBehindLocation, Sound.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED, 1f,1f);

                for (Enchantment enchantment : pickaxeData.getEnchantments()) {
                    if (enchantment instanceof BlockBreakListener blockBreakListener) {
                        blockBreakListener.onBlockBreak(player, blockBehind, pickaxeData, true);
                    }
                }

                for (Ability ability : pickaxeData.getAbilities()) {
                    if (ability instanceof BlockBreakListener blockBreakListener) {
                        blockBreakListener.onBlockBreak(player, blockBehind, pickaxeData, true);
                    }
                }
                pickaxeData.addBlocksBroken(1);

                LootCrateManager.applyEnergyFromBlockBreak(player, blockBehind);

                List<Item> droppedItems = new ArrayList<>();

                World world = blockBehind.getWorld();
                Location blockLocation = blockBehindLocation.toCenterLocation();
                BlockState blockState = blockBehind.getState();

                Loot oreLoot = BlockCategories.ORE_BLOCKS.get(blockBehind.getType());
                if (oreLoot != null) {
                    Item itemEntity = world.dropItemNaturally(blockLocation, oreLoot.drop());
                    droppedItems.add(itemEntity);

                } else {
                    for (ItemStack drop : blockBehind.getDrops()) {
                        Item itemEntity = world.dropItemNaturally(blockLocation, drop);
                        droppedItems.add(itemEntity);
                    }
                }

                world.playEffect(blockLocation, Effect.STEP_SOUND, blockBehind.getBlockData());
                blockBehind.setType(Material.AIR, false);

                for (Enchantment enchantment : pickaxeData.getEnchantments()) {
                    if (enchantment instanceof BlockDropItemListener listener) {
                        listener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
                    }
                }

                for (Ability ability : pickaxeData.getAbilities()) {
                    if (ability instanceof BlockDropItemListener listener) {
                        listener.onBlockDropItem(player, blockState, droppedItems, pickaxeData);
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        this.blockFace = event.getBlockFace();
    }
}
