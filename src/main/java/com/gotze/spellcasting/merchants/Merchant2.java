package com.gotze.spellcasting.merchants;

import com.gotze.spellcasting.util.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public abstract class Merchant2 extends Menu {
    private final String merchantName;
    private final Location merchantLocation;
    private final Villager.Type villagerType;
    private final Villager.Profession villagerProfession;

    public Merchant2(int rows, Component menuTitle, String merchantName, Location merchantLocation,
                     Villager.Type villagerType, Villager.Profession villagerProfession) {
        super(rows, menuTitle, true);
        this.merchantName = merchantName;
        this.villagerType = villagerType;
        this.villagerProfession = villagerProfession;
        this.merchantLocation = merchantLocation;
    }

    protected abstract void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event);

    public String getMerchantName() {
        return merchantName;
    }

    public Location getMerchantLocation() {
        return merchantLocation;
    }

    public Villager.Type getVillagerType() {
        return villagerType;
    }

    public Villager.Profession getVillagerProfession() {
        return villagerProfession;
    }
}