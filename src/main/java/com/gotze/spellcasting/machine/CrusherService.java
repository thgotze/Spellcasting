package com.gotze.spellcasting.machine;

import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CrusherService {
    private final Map<Location, Crusher> crushers = new ConcurrentHashMap<>();

    public void tickCrushers() {
        for (Crusher crusher : crushers.values()) {
            crusher.tick();
        }
    }

    public Crusher getCrusher(Location location) {
        return crushers.get(location);
    }

    public void addCrusher(Crusher crusher) {
        crushers.put(crusher.getLocation(), crusher);
    }

    public void removeCrusher(Location location) {
        crushers.remove(location);
    }
}