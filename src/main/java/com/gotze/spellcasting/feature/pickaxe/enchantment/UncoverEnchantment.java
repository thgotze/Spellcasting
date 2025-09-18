package com.gotze.spellcasting.feature.pickaxe.enchantment;

import com.gotze.spellcasting.feature.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class UncoverEnchantment extends Enchantment {

    public UncoverEnchantment() {
        super(EnchantmentType.UNCOVER);
    }

    @Override
    public void activate(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {

    }
}