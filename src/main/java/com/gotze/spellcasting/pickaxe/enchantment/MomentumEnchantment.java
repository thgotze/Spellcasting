package com.gotze.spellcasting.pickaxe.enchantment;

import com.gotze.spellcasting.pickaxe.PickaxeData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MomentumEnchantment extends Enchantment {

    public MomentumEnchantment() {
        super(EnchantmentType.MOMENTUM);
    }

    @Override
    public void activate(Player player, BlockBreakEvent blockBreakEvent, PickaxeData pickaxeData) {
        double chance = getLevel() * 0.05;

        if (Math.random() < chance) {
            int amplifier = getLevel() - 1;
            if (Math.random() < 0.5) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * getLevel(), amplifier));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * getLevel(), amplifier));
            }
        }
    }
}