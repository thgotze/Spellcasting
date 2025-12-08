package com.gotze.spellcasting.islands;

import com.gotze.spellcasting.data.PlayerProfileManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IslandData {
    private Location islandCenter;
    private Location islandHome;
    private final double islandRadius = 256;

    public IslandData(Location islandCenter) {
        this.islandCenter = islandCenter;
        this.islandHome = islandCenter.clone().add(0.5, 0, 0.5);
    }

    public IslandData(Location islandCenter, Location islandHome) {
        this.islandCenter = islandCenter;
        this.islandHome = islandHome;
    }

    public static IslandData fromPlayer(Player player) {
        return PlayerProfileManager.getPlayerProfile(player).getIslandData();
    }

    public Location getIslandCenter() {
        return islandCenter;
    }

    public void setIslandCenter(Location islandCenter) {
        this.islandCenter = islandCenter;
    }

    public double getIslandRadius() {
        return islandRadius;
    }

    public Location getIslandHome() {
        return islandHome;
    }

    public void setIslandHome(Location islandHome) {
        this.islandHome = islandHome;
    }
}