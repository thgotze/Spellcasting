package com.gotze.spellcasting.mines;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedBlockSelector {
    private final List<WeightedBlock> blocks = new ArrayList<>();
    private final Random random = new Random();
    private double totalWeight = 0;

    public void addBlock(Material material, double weight) {
        blocks.add(new WeightedBlock(material, weight));
        totalWeight += weight;
    }

    public Material getRandomBlock() {
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (WeightedBlock block : blocks) {
            currentWeight += block.weight;
            if (randomValue <= currentWeight) {
                return block.material;
            }
        }

        return blocks.getLast().material; // Fallback
    }

    private static class WeightedBlock {
        Material material;
        double weight;

        WeightedBlock(Material material, double weight) {
            this.material = material;
            this.weight = weight;
        }
    }
}