package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.capability.BlockDropItemListener;
import com.gotze.spellcasting.util.Loot;
import com.gotze.spellcasting.util.block.BlockCategories;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FortuneEnchantment extends Enchantment implements BlockDropItemListener {

    public FortuneEnchantment() {
        super(EnchantmentType.FORTUNE);
    }

    @Override
    public void onBlockDropItem(Player player, BlockState blockState, List<Item> droppedItems, PickaxeData pickaxeData) {
        Loot lootEntry = BlockCategories.ORE_BLOCKS.get(blockState.getType());
        if (lootEntry == null) return;

        double random = ThreadLocalRandom.current().nextDouble();
        int multiplier = switch (getLevel()) {
            case 1 -> random < 0.33 ? 2 : 1;
            case 2 -> random < 0.25 ? 3 : random < 0.50 ? 2 : 1;
            case 3 -> random < 0.20 ? 4 : random < 0.40 ? 3 : random < 0.60 ? 2 : 1;
            default -> throw new IllegalStateException("Unexpected Fortune enchantment level: " + getLevel());
        };
        if (multiplier == 1) return;

        for (Item itemEntity : droppedItems) {
            ItemStack itemStack = itemEntity.getItemStack();
            itemStack.setAmount(itemStack.getAmount() * multiplier);
            itemEntity.setItemStack(itemStack);
        }
    }
}