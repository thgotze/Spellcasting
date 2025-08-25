package com.gotze.spellcasting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerPickaxeManager {
    private final Player player;
    private ItemStack playerPickaxe;

    public PlayerPickaxeManager(Player player) {
        this.player = player;
        this.playerPickaxe = loadPickaxeDataFromFile();
    }

    public ItemStack getPickaxe() {
        return playerPickaxe;
    }

    public void setPickaxe(ItemStack playerPickaxe) {
        this.playerPickaxe = playerPickaxe;
    }

    private ItemStack loadPickaxeDataFromFile() {
        return new ItemStack(Material.WOODEN_PICKAXE); // TODO
    }
}
