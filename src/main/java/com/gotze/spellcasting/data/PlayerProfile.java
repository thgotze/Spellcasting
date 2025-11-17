package com.gotze.spellcasting.data;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;

public class PlayerProfile {
    private final LocalDateTime joinDate;
    private LocalDateTime lastSeen;
    private Duration playTime;
    private double balance;
    private PickaxeData pickaxeData;

    public PlayerProfile() {
        this.joinDate = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.playTime = Duration.ZERO;
        this.balance = 0.00;
        this.pickaxeData = new PickaxeData();
    }

    public PlayerProfile(LocalDateTime joinDate, LocalDateTime lastSeen, Duration playTime, double balance, PickaxeData pickaxeData) {
        this.joinDate = joinDate;
        this.lastSeen = lastSeen;
        this.playTime = playTime;
        this.balance = balance;
        this.pickaxeData = pickaxeData;
    }

    public static PlayerProfile fromPlayer(Player player) {
        return PlayerProfileManager.getPlayerProfile(player);
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

    public PickaxeData getPickaxeData() {
        return pickaxeData;
    }

    public void setPickaxeData(PickaxeData pickaxeData) {
        this.pickaxeData = pickaxeData;
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
}