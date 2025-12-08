package com.gotze.spellcasting.merchants;

import com.gotze.spellcasting.util.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import static net.kyori.adventure.text.Component.text;

public abstract class Merchant extends Menu {
    private final String merchantName;
    private final Location merchantLocation;
    private final Villager.Type villagerType;
    private final Villager.Profession villagerProfession;
    private Villager villager;

    public Merchant(int rows, Component menuTitle, boolean interactable,
                    String merchantName, Location merchantLocation,
                    Villager.Type villagerType, Villager.Profession villagerProfession) {
        super(rows, menuTitle, interactable);
        this.merchantName = merchantName;
        this.villagerType = villagerType;
        this.villagerProfession = villagerProfession;
        this.merchantLocation = merchantLocation;
        this.villager = spawnVillager();
    }

    private Villager spawnVillager() {
        World world = merchantLocation.getWorld();
        if (world == null) return null;

        if (!world.isChunkLoaded(merchantLocation.getChunk())) {
            world.loadChunk(merchantLocation.getChunk());
        }

        // Check if the villager is already present in the world
        for (Villager existing : world.getEntitiesByClass(Villager.class)) {
            if (existing.getLocation().distanceSquared(merchantLocation) < 1) {
                return existing;
            }
        }
        villager = (Villager) world.spawnEntity(merchantLocation, EntityType.VILLAGER);
        villager.customName(text(merchantName));
        villager.setVillagerType(villagerType);
        villager.setProfession(villagerProfession);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCollidable(false);
        villager.setPersistent(true);
        villager.setSilent(true);
        villager.setCustomNameVisible(true);
        return villager;
    }

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

    public Villager getVillager() {
        return villager;
    }
}