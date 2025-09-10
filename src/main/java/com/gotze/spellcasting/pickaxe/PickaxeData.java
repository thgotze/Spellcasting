package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PickaxeData {
    private Material material;
    private Set<Enchantment> enchantments;
    private Set<Ability> abilities;
    private int blocksBroken;
    private int damage;

    public PickaxeData() {
        this.material = Material.WOODEN_PICKAXE;
        this.enchantments = new HashSet<>();
        this.abilities = new HashSet<>();
        this.blocksBroken = 0;
        this.damage = 0;
    }

    public PickaxeData(PickaxeData other) {
        this.material = other.material;
        this.enchantments = new HashSet<>(other.enchantments);
        this.abilities = new HashSet<>(other.abilities);
        this.blocksBroken = other.blocksBroken;
        this.damage = other.damage;
    }

    public Material getType() {
        return material;
    }

    public void setType(Material material) {
        this.material = material;
    }

    public Set<Enchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Set<Enchantment> enchantments) {
        this.enchantments = enchantments;
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

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(Set<Ability> abilities) {
        this.abilities = abilities;
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

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void addBlocksBroken(int amount) {
        this.blocksBroken += amount;
    }

    public int getDamage() {
        return damage;
    }

    public void addDamage(int amount) {
        this.damage += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickaxeData that)) return false;
        return blocksBroken == that.blocksBroken &&
                damage == that.damage &&
                material == that.material &&
                Objects.equals(enchantments, that.enchantments) &&
                Objects.equals(abilities, that.abilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, enchantments, abilities, blocksBroken, damage);
    }

    @Override
    public String toString() {
        return "PickaxeData{" +
                "material=" + material +
                ", enchantments=" + enchantments +
                ", abilities=" + abilities +
                ", blocksBroken=" + blocksBroken +
                ", damage=" + damage +
                '}';
    }
}