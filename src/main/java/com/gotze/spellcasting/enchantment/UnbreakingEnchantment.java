package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import com.gotze.spellcasting.util.block.BlockBreakListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class UnbreakingEnchantment extends Enchantment implements BlockBreakListener {

    public UnbreakingEnchantment() {
        super(EnchantmentType.UNBREAKING);
    }

    @Override
    public void onBlockBreak(Player player, Block block, PickaxeData pickaxeData, boolean isNaturalBreak) {

    }
}