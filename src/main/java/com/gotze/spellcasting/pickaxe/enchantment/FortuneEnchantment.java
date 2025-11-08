package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockBreakListener;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
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
        Loot lootEntry = BlockCategories.ORE_BLOCKS.get(block.getType());
        if (lootEntry == null) return;

        double random = ThreadLocalRandom.current().nextDouble();
        int multiplier = switch (getLevel()) {
            case 1 -> random < 0.33 ? 2 : 1;
            case 2 -> random < 0.25 ? 3 : random < 0.50 ? 2 : 1;
            case 3 -> random < 0.20 ? 4 : random < 0.40 ? 3 : random < 0.60 ? 2 : 1;
            default -> throw new IllegalStateException("Unexpected fortune level: " + getLevel());
        };
        if (multiplier == 1) return;

        ItemStack oreDrops = lootEntry.drop();
        oreDrops.setAmount((oreDrops.getAmount() * multiplier) - oreDrops.getAmount());
        // Subtract the base drop because the normal block break already drops it

        block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), oreDrops);
    }
}