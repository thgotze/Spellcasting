package com.gotze.spellcasting.util;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public record Loot(ItemStack itemStack, int min, int max, double chance) {
    public Loot(ItemStack itemStack, int min, int max) {
        this(itemStack, min, max, 1.0);
    }

    public Loot(ItemStack itemStack, int amount, double chance) {
        this(itemStack, amount, amount, chance);
    }

    public Loot(ItemStack itemStack, double chance) {
        this(itemStack, 1, 1, chance);
    }

    public Loot(ItemStack itemStack, int amount) {
        this(itemStack, amount, amount, 1.0);
    }

    public Loot(ItemStack itemStack) {
        this(itemStack, 1, 1, 1.0);
    }

    public Optional<ItemStack> rollChance() {
        // Roll for chance to receive item
        if (chance < 1.0) {
            if (ThreadLocalRandom.current().nextDouble() > chance) {
                return Optional.empty();
            }
        }
        // Roll for amount to receive
        if (min > 1 && min == max ) {
            itemStack.setAmount(min);
        } else {
            itemStack.setAmount(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
        return Optional.of(itemStack);
    }
}