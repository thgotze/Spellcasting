package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.ability.Ability;
import com.gotze.spellcasting.enchantment.Enchantment;

import java.util.HashSet;
import java.util.Set;

public class PickaxeData {
    private PickaxeMaterial pickaxeMaterial;
    private Set<Enchantment> enchantments;
    private Set<Ability> abilities;
    private int blocksBroken;
    private int durabilityDamage;

    public PickaxeData() {
        this.pickaxeMaterial = PickaxeMaterial.WOOD;
        this.enchantments = new HashSet<>();
        this.abilities = new HashSet<>();
        this.blocksBroken = 0;
        this.durabilityDamage = 0;
    }

    public PickaxeMaterial pickaxeMaterial() {
        return pickaxeMaterial;
    }

    public void pickaxeMaterial(PickaxeMaterial pickaxeMaterial) {
        this.pickaxeMaterial = pickaxeMaterial;
    }

    public Set<Enchantment> enchantments() {
        return enchantments;
    }

    public void setEnchantments(Set<Enchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void addEnchantment(Enchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public Enchantment getEnchantment(Enchantment enchantment) {
        return getEnchantment(enchantment.enchantmentType());
    }

    public Enchantment getEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchantment : enchantments) {
            if (enchantmentType == enchantment.enchantmentType()) {
                return enchantment;
            }
        }
        return null;
    }

    public boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return hasEnchantment(enchantment.enchantmentType());
    }

    public boolean hasEnchantment(Enchantment.EnchantmentType enchantmentType) {
        for (Enchantment enchant : enchantments) {
            if (enchantmentType == enchant.enchantmentType()) {
                return true;
            }
        }
        return false;
    }

    public void removeEnchantments() {
        enchantments.clear();
    }

    public Set<Ability> abilities() {
        return abilities;
    }

    public void setAbilities(Set<Ability> abilities) {
        this.abilities = abilities;
    }

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    public Ability getAbility(Ability ability) {
        return getAbility(ability.abilityType());
    }

    public Ability getAbility(Ability.AbilityType abilityType) {
        for (Ability ability : abilities) {
            if (abilityType == ability.abilityType()) {
                return ability;
            }
        }
        return null;
    }

    public boolean hasAbilities() {
        return !abilities.isEmpty();
    }

    public boolean hasAbility(Ability ability) {
        return hasAbility(ability.abilityType());
    }

    public boolean hasAbility(Ability.AbilityType abilityType) {
        for (Ability ability : abilities) {
            if (abilityType == ability.abilityType()) {
                return true;
            }
        }
        return false;
    }

    public void removeAbilities() {
        abilities.clear();
    }

    public int blocksBroken() {
        return blocksBroken;
    }

    public void addBlocksBroken(int amount) {
        this.blocksBroken += amount;
    }

    public int durabilityDamage() {
        return durabilityDamage;
    }

    public void setDurabilityDamage(int amount) {
        this.durabilityDamage = amount;
    }

    public void addDurabilityDamage(int amount) {
        this.durabilityDamage += amount;
    }
}