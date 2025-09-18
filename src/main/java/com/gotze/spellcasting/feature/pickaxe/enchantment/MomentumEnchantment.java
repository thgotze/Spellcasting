package com.gotze.spellcasting.feature.pickaxe.enchantment;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class MomentumEnchantment extends Enchantment {

    public MomentumEnchantment() {
        super(EnchantmentType.MOMENTUM);
    }

    @Override
    public void activate(Player player, BlockBreakEvent blockBreakEvent, PickaxeData pickaxeData) {

    }
}