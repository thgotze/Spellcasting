package com.gotze.spellcasting.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class UncoverEnchantment extends Enchantment {

    public UncoverEnchantment() {
        super(EnchantmentType.UNCOVER);
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event, PickaxeData pickaxeData) {

    }
}