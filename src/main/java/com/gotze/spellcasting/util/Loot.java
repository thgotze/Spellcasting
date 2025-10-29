package com.gotze.spellcasting.util;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Loot {
    private final ItemStack itemStack;
    private final int min;
    private final int max;
    private final double chance;

    public Loot(ItemStack itemStack, int min, int max, double chance) {
        this.itemStack = itemStack;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

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

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public double chance() {
        return chance;
    }

    public Optional<ItemStack> rollDrop() {
        // Roll for a chance to receive item
        if (ThreadLocalRandom.current().nextDouble() > chance) {
            return Optional.empty();
        }
        return Optional.of(drop());
    }

    public ItemStack drop() {
        ItemStack clone = this.itemStack.clone();
        // Roll for amount to receive
        if (min == max) {
            clone.setAmount(min);
        } else {
            clone.setAmount(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
        return clone;
    }
}