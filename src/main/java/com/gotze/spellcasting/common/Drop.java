package com.gotze.spellcasting.common;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public record Drop(Material material, int min, int max, double chance) {
    public Drop(Material material, int amount, double chance) {
        this(material, amount, amount, chance);
    }

    public Drop(Material material, int min, int max) {
        this(material, min, max, 1.0);
    }

    public Drop(Material material, int amount) {
        this(material, amount, amount, 1.0);
    }

    public Optional<ItemStack> toItemStack() {
        if (chance < 1.0) {
            if (ThreadLocalRandom.current().nextDouble() > chance) {
                return Optional.empty();
            }
        }

        int amount = min;
        if (min != max) {
            amount = ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        return Optional.of(ItemStack.of(material, amount));
    }
}