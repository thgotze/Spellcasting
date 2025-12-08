package com.gotze.spellcasting.pickaxe.ability;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.mines.MineManager;
import com.gotze.spellcasting.util.block.BlockCategories;
import com.gotze.spellcasting.util.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class WindBurstAbility extends Ability {

    private boolean isActive;
    private int maxWindChargeShots;
    private int windChargesShot;

    public WindBurstAbility() {
        super(AbilityType.WIND_BURST);
    }

    @Override
    public void activateAbility(Player player, PickaxeData pickaxeData) {
        if (!this.isActive || this.windChargesShot >= this.maxWindChargeShots) {
            this.windChargesShot = 0;
            this.maxWindChargeShots = getLevel() * 3; // 3, 6, 9, 12, 15
            this.isActive = true;
        }

        player.playSound(player, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1f, 1f);
        player.swingMainHand();

        WindCharge windCharge = player.launchProjectile(WindCharge.class);
        this.windChargesShot++;

        int remainingShots = this.maxWindChargeShots - this.windChargesShot;

        player.sendActionBar(getAbilityType().getFormattedName()
                .append(text(" activated! ", YELLOW))
                .append(text(remainingShots + "/" + maxWindChargeShots)));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!windCharge.isValid()) {
                    List<Block> blocksInSquarePattern = BlockUtils.getBlocksInSquarePattern(windCharge.getLocation().getBlock(), 3, 3, 3);
                    for (Block block : blocksInSquarePattern) {
                        if (!MineManager.isInAnyMine(block)) continue;
                        if (!BlockCategories.FILLER_BLOCKS.contains(block.getType())) continue;

                        block.setType(Material.AIR);
                    }
                    cancel();
                    return;

                } else if (ticks >= 100) {
                    cancel();
                    return;
                }
                ticks++;
            }
        }.runTaskTimer(Spellcasting.getPlugin(), 0L, 1L);
    }
}