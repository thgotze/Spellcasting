package com.gotze.spellcasting.feature.recipes;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.feature.machines.Centrifuge;
import com.gotze.spellcasting.feature.machines.Crusher;
import com.gotze.spellcasting.feature.machines.Sifter;
import com.gotze.spellcasting.feature.machines.Washer;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class RecipeRegistry {

    public static void registerRecipes() {
        for (Crusher.CrushingRecipe crushingRecipe : Crusher.CrushingRecipe.values()) {
            ItemStack resultItem = crushingRecipe.getResultItem();

            String baseKey = PlainTextComponentSerializer.plainText().serialize(resultItem.getData(DataComponentTypes.ITEM_NAME))
                    .toLowerCase()
                    .replace(" ", "_");

            // Register Furnace recipe
            NamespacedKey furnaceRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_smelt");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                    furnaceRecipeKey,
                    new ItemStack(getCorrespondingIngot(crushingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(crushingRecipe),
                    200 // 10 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(furnaceRecipe);

            // Register Blast Furnace recipe
            NamespacedKey blastingRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_blast");
            BlastingRecipe blastingRecipe = new BlastingRecipe(
                    blastingRecipeKey,
                    new ItemStack(getCorrespondingIngot(crushingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(crushingRecipe),
                    100 // 5 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(blastingRecipe);
        }

        for (Washer.WashingRecipe washingRecipe : Washer.WashingRecipe.values()) {
            ItemStack resultItem = washingRecipe.getResultItem();

            String baseKey = PlainTextComponentSerializer.plainText().serialize(resultItem.getData(DataComponentTypes.ITEM_NAME))
                    .toLowerCase()
                    .replace(" ", "_");

            // Register Furnace recipe
            NamespacedKey furnaceRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_smelt");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                    furnaceRecipeKey,
                    new ItemStack(getCorrespondingIngot(washingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(washingRecipe),
                    200 // 10 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(furnaceRecipe);

            // Register Blast Furnace recipe
            NamespacedKey blastingRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_blast");
            BlastingRecipe blastingRecipe = new BlastingRecipe(
                    blastingRecipeKey,
                    new ItemStack(getCorrespondingIngot(washingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(washingRecipe),
                    100 // 5 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(blastingRecipe);
        }

        for (Sifter.SiftingRecipe siftingRecipe : Sifter.SiftingRecipe.values()) {
            ItemStack resultItem = siftingRecipe.getResultItem();

            String baseKey = PlainTextComponentSerializer.plainText().serialize(resultItem.getData(DataComponentTypes.ITEM_NAME))
                    .toLowerCase()
                    .replace(" ", "_");

            // Register Furnace recipe
            NamespacedKey furnaceRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_smelt");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                    furnaceRecipeKey,
                    new ItemStack(getCorrespondingIngot(siftingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(siftingRecipe),
                    200 // 10 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(furnaceRecipe);

            // Register Blast Furnace recipe
            NamespacedKey blastingRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_blast");
            BlastingRecipe blastingRecipe = new BlastingRecipe(
                    blastingRecipeKey,
                    new ItemStack(getCorrespondingIngot(siftingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(siftingRecipe),
                    100 // 5 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(blastingRecipe);
        }

        for (Centrifuge.CentrifugeRecipe centrifugeRecipe : Centrifuge.CentrifugeRecipe.values()) {
            ItemStack resultItem = centrifugeRecipe.getResultItem();

            String baseKey = PlainTextComponentSerializer.plainText().serialize(resultItem.getData(DataComponentTypes.ITEM_NAME))
                    .toLowerCase()
                    .replace(" ", "_");

            // Register Furnace recipe
            NamespacedKey furnaceRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_smelt");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                    furnaceRecipeKey,
                    new ItemStack(getCorrespondingIngot(centrifugeRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(centrifugeRecipe),
                    200 // 10 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(furnaceRecipe);

            // Register Blast Furnace recipe
            NamespacedKey blastingRecipeKey = new NamespacedKey(Spellcasting.getPlugin(), baseKey + "_blast");
            BlastingRecipe blastingRecipe = new BlastingRecipe(
                    blastingRecipeKey,
                    new ItemStack(getCorrespondingIngot(centrifugeRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(centrifugeRecipe),
                    100 // 5 seconds
            );
            Spellcasting.getPlugin().getServer().addRecipe(blastingRecipe);
        }
    }

    private static Material getCorrespondingIngot(Crusher.CrushingRecipe crushingRecipe) {
        return switch (crushingRecipe) {
            case CRUSHED_COPPER_ORE -> Material.COPPER_INGOT;
            case CRUSHED_IRON_ORE -> Material.IRON_INGOT;
            case CRUSHED_GOLD_ORE -> Material.GOLD_INGOT;
        };
    }

    private static Material getCorrespondingIngot(Washer.WashingRecipe washingRecipe) {
        return switch (washingRecipe) {
            case PURIFIED_COPPER_ORE -> Material.COPPER_INGOT;
            case PURIFIED_IRON_ORE -> Material.IRON_INGOT;
            case PURIFIED_GOLD_ORE -> Material.GOLD_INGOT;
        };
    }

    private static Material getCorrespondingIngot(Sifter.SiftingRecipe siftingRecipe) {
        return switch (siftingRecipe) {
            case PURIFIED_COPPER_DUST -> Material.COPPER_INGOT;
            case PURIFIED_IRON_DUST -> Material.IRON_INGOT;
            case PURIFIED_GOLD_DUST -> Material.GOLD_INGOT;
        };
    }

    private static Material getCorrespondingIngot(Centrifuge.CentrifugeRecipe centrifugeRecipe) {
        return switch (centrifugeRecipe) {
            case COPPER_DUST -> Material.COPPER_INGOT;
            case IRON_DUST -> Material.IRON_INGOT;
            case GOLD_DUST -> Material.GOLD_INGOT;
        };
    }

    // Smelting crushed ores gives half the amount of exp as the raw ore would've given
    public static float getExperienceAmount(Crusher.CrushingRecipe crushingRecipe) {
        return switch (crushingRecipe) {
            case CRUSHED_COPPER_ORE -> 0.35f;
            case CRUSHED_IRON_ORE -> 0.35f;
            case CRUSHED_GOLD_ORE -> 0.5f;
        };
    }

    public static float getExperienceAmount(Washer.WashingRecipe washingRecipe) {
        return switch (washingRecipe) {
            case PURIFIED_COPPER_ORE -> 0.35f;
            case PURIFIED_IRON_ORE -> 0.35f;
            case PURIFIED_GOLD_ORE -> 0.5f;
        };
    }

    public static float getExperienceAmount(Sifter.SiftingRecipe siftingRecipe) {
        return switch (siftingRecipe) {
            case PURIFIED_COPPER_DUST -> 0.35f;
            case PURIFIED_IRON_DUST -> 0.35f;
            case PURIFIED_GOLD_DUST -> 0.5f;
        };
    }

    public static float getExperienceAmount(Centrifuge.CentrifugeRecipe centrifugeRecipe) {
        return switch (centrifugeRecipe) {
            case COPPER_DUST -> 0.35f;
            case IRON_DUST -> 0.35f;
            case GOLD_DUST -> 0.5f;
        };
    }

    private RecipeRegistry() {
    }
}
