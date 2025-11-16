package com.gotze.spellcasting.merchant;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MerchantManager implements Listener {

    private final Map<UUID, Merchant> merchants = new HashMap<>();

    public MerchantManager() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        Merchant oreMerchant = new OreMerchant();
        merchants.put(oreMerchant.getVillager().getUniqueId(), oreMerchant);

        Merchant tokenMerchant = new TokenMerchant();
        merchants.put(tokenMerchant.getVillager().getUniqueId(), tokenMerchant);

//        registerVillager(world, new Location(world, -3.5, 97, 21.5),
//                Villager.Type.SAVANNA, Villager.Profession.LIBRARIAN, "Token Seller");
//        registerVillager(world, new Location(world, 0.5, 97, 21.5),
//                Villager.Type.SNOW, Villager.Profession.CARTOGRAPHER, "Ore Merchant");
//        registerVillager(world, new Location(world, 4.5, 97, 21.5),
//                Villager.Type.TAIGA, Villager.Profession.ARMORER, "Machine Seller");
    }

    @EventHandler
    public void onRightClickMerchant(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;

        Merchant merchant = merchants.get(villager.getUniqueId());
        if (merchant == null) return;

        event.setCancelled(true);

        merchant.open(event.getPlayer());
    }
}