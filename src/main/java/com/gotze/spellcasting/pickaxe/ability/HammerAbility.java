package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.pickaxe.capability.BlockBreaker;
import com.gotze.spellcasting.pickaxe.capability.BlockDamageListener;
import com.gotze.spellcasting.pickaxe.capability.ItemModelManager;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class HammerAbility extends Ability implements BlockBreakListener, BlockDamageListener, BlockBreaker {

    boolean isActive;
    BlockFace blockFace;

    public HammerAbility() {
        super(AbilityType.HAMMER);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (this.isActive) return;
        if (ItemModelManager.hasActiveModification(player)) return;
        this.isActive = true;

        player.swingMainHand();
        player.sendActionBar(getAbilityType().getFormattedName().append(text(" activated!", YELLOW)));

        ItemModelManager.modifyItemModelTemporarily(player,
                player.getInventory().getItemInMainHand(),
                Material.MACE,
                300L,
                () -> {
                    player.sendActionBar(text("Hammer ability expired", RED));
                    this.isActive = false;
                }
        );
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        if (!this.isActive) return;
        if (!isNaturalBreak) return;
        if (blockFace == null) return;
        if (player.isSneaking()) return;

        List<Block> blocksToBreak = switch (blockFace) {
            case NORTH, SOUTH -> BlockUtils.getBlocksInSquarePattern(block, 3, 3, 1);
            case EAST, WEST -> BlockUtils.getBlocksInSquarePattern(block, 1, 3, 3);
            case UP, DOWN -> BlockUtils.getBlocksInSquarePattern(block, 3, 1, 3);
            default -> null;
        };
        if (blocksToBreak == null) return;
        blocksToBreak.remove(block);
        breakBlocks(player, blocksToBreak, pickaxeData);
    }

    @Override
    public void onBlockDamage(Player player, BlockDamageEvent event, PickaxeData pickaxeData) {
        if (!this.isActive) return;
        this.blockFace = event.getBlockFace();
    }
}