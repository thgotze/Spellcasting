package com.gotze.spellcasting.data;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class PlayerPickaxeData {
    private Player owner;
    private ItemStack pickaxe; // TODO: might need to delete this later
    private Material material;
    private Set<Enchantment> enchantments;
    private Set<Ability> abilities;
    private int blocksBroken;

    public PlayerPickaxeData(Player player, ItemStack pickaxe) {
        this.owner = player;
        this.pickaxe = pickaxe;
        this.material = pickaxe.getType();
        this.enchantments = new HashSet<>();
        this.abilities = new HashSet<>();
        this.blocksBroken = 0;
    }

    public Player getOwner() {
        return owner;
    }

    public ItemStack getPickaxe() {
        return pickaxe;
    }

    public Material getType() {
        return material;
    }

    public Set<Enchantment> getEnchantments() {
        return enchantments;
    }

    public Enchantment getEnchantment(Enchantment enchantment) {
        for (Enchantment enchant : enchantments) {
            if (enchantment.getEnchantmentType() == enchant.getEnchantmentType()) {
                return enchant;
            }
        }
        return null;
    }

    public Enchantment getEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchantment : enchantments) {
            if (enchantmentType == enchantment.getEnchantmentType()) {
                return enchantment;
            }
        }
        return null;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        for (Enchantment enchant : enchantments) {
            if (enchantment.getEnchantmentType() == enchant.getEnchantmentType()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchant : enchantments) {
            if (enchantmentType == enchant.getEnchantmentType()) {
                return true;
            }
        }
        return false;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setType(Material material) {
        this.material = material;
    }

    public void setEnchantments(Set<Enchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void addEnchantment(Enchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public void setAbilities(Set<Ability> abilities) {
        this.abilities = abilities;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}