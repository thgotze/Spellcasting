package com.gotze.spellcasting.data;

import com.gotze.spellcasting.islands.Island;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;

public class PlayerProfile {
    private final LocalDateTime joinDate;
    private LocalDateTime lastSeen;
    private Duration playTime;
    private double balance;
    private Rank rank;
    private PickaxeData pickaxeData;
    private Island island;
//    private final Map<Integer, ItemStack[]> privateVaults;

    public PlayerProfile() {
        this.joinDate = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.playTime = Duration.ZERO;
        this.balance = 0.00;
        this.rank = Rank.A;
        this.pickaxeData = new PickaxeData();
        this.island = null;
//        this.privateVaults = new HashMap<>();
    }

    public PlayerProfile(LocalDateTime joinDate, LocalDateTime lastSeen, Duration playTime, double balance, Rank rank,
                         PickaxeData pickaxeData, Island island /*, Map<Integer, ItemStack[]> privateVaults*/) {
        this.joinDate = joinDate;
        this.lastSeen = lastSeen;
        this.playTime = playTime;
        this.balance = balance;
        this.rank = rank;
        this.pickaxeData = pickaxeData;
        this.island = island;
//        this.privateVaults = privateVaults;
    }

    public static PlayerProfile of(Player player) {
        return PlayerProfileManager.getPlayerProfile(player);
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Duration getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Duration playTime) {
        this.playTime = playTime;
    }

    public void addPlayTime(Duration duration) {
        setPlayTime(playTime.plus(duration));
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addBalance(double amount) {
        setBalance(balance + amount);
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public PickaxeData getPickaxeData() {
        return pickaxeData;
    }

    public void setPickaxeData(PickaxeData pickaxeData) {
        this.pickaxeData = pickaxeData;
    }

    public Island getIsland() {
        return island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

//    public Map<Integer, ItemStack[]> getPrivateVaults() {
//        return privateVaults;
//    }
//
//    public void setPrivateVault(int vaultNumber, ItemStack[] items) {
//        this.privateVaults.put(vaultNumber, items);
//    }
//
//    public ItemStack[] getPrivateVault(int vaultNumber) {
//        return this.privateVaults.getOrDefault(vaultNumber, new ItemStack[54]);
//    }
}