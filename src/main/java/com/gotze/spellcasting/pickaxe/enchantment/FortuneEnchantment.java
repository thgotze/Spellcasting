package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class FortuneEnchantment extends Enchantment implements BlockBreakListener {

    public FortuneEnchantment() {
        super(EnchantmentType.FORTUNE);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {
        Loot loot = BlockCategories.ORE_BLOCKS.get(block.getType());
        if (loot == null) return;

        ItemStack itemStack = loot.drop();
        Location blockLocation = block.getLocation().toCenterLocation();

        double random = ThreadLocalRandom.current().nextDouble();
        int multiplier = switch (getLevel()) {
            case 1 -> {
                if (random < 0.33) yield 2;
                else yield 1;
            }
            case 2 -> {
                if (random < 0.25) yield 3;
                else if (random < 0.50) yield 2;
                else yield 1;
            }
            case 3 -> {
                if (random < 0.20) yield 4;
                else if (random < 0.40) yield 3;
                else if (random < 0.60) yield 2;
                else yield 1;
            }
            default -> throw new IllegalStateException("Unexpected fortune level: " + getLevel());
        };
        if (multiplier == 1) return;

        itemStack.setAmount((itemStack.getAmount() * multiplier) - itemStack.getAmount());
        block.getWorld().dropItemNaturally(blockLocation, itemStack);
    }
}