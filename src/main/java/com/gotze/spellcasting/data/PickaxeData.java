package com.gotze.spellcasting.data;

import com.gotze.spellcasting.pickaxe.PickaxeMaterial;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PickaxeData {
    private PickaxeMaterial pickaxeMaterial;
    private int durabilityDamage;
    private int blocksBroken;
    private final List<Enchantment> enchantments;
    private final List<Ability> abilities;

    public PickaxeData() {
        this.pickaxeMaterial = PickaxeMaterial.WOOD;
        this.blocksBroken = 0;
        this.durabilityDamage = 0;
        this.enchantments = new ArrayList<>();
        this.abilities = new ArrayList<>();
    }

    public static PickaxeData fromPlayer(Player player) {
        return PlayerProfileManager.getPlayerProfile(player).getPickaxeData();
    }

    public PickaxeMaterial getPickaxeMaterial() {
        return pickaxeMaterial;
    }

    public void setPickaxeMaterial(PickaxeMaterial pickaxeMaterial) {
        this.pickaxeMaterial = pickaxeMaterial;
    }

    public int getDurabilityDamage() {
        return durabilityDamage;
    }

    public void setDurabilityDamage(int amount) {
        this.durabilityDamage = amount;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void addBlocksBroken(int amount) {
        this.blocksBroken += amount;
    }

    public List<Enchantment> getEnchantments() {
        return enchantments;
    }

    public void addEnchantment(Enchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public Enchantment getEnchantment(Enchantment enchantment) {
        return getEnchantment(enchantment.getEnchantmentType());
    }

    public Enchantment getEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchantment : enchantments) {
            if (enchantmentType == enchantment.getEnchantmentType()) {
                return enchantment;
            }
        }
        return null;
    }

    public boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return hasEnchantment(enchantment.getEnchantmentType());
    }

    public boolean hasEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchant : enchantments) {
            if (enchantmentType == enchant.getEnchantmentType()) {
                return true;
            }
        }
        return false;
    }

    public void removeEnchantments() {
        enchantments.clear();
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    public Ability getAbility(Ability ability) {
        return getAbility(ability.getAbilityType());
    }

    public Ability getAbility(Ability.AbilityType abilityType) {
        for (Ability ability : abilities) {
            if (abilityType == ability.getAbilityType()) {
                return ability;
            }
        }
        return null;
    }

    public boolean hasAbilities() {
        return !abilities.isEmpty();
    }

    public boolean hasAbility(Ability ability) {
        return hasAbility(ability.getAbilityType());
    }

    public boolean hasAbility(Ability.AbilityType abilityType) {
        for (Ability ability : abilities) {
            if (abilityType == ability.getAbilityType()) {
                return true;
            }
        }
        return false;
    }

    public void removeAbilities() {
        abilities.clear();
    }
}