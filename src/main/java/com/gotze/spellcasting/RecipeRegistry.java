package com.gotze.spellcasting;

import com.gotze.spellcasting.machine.crusher.Crusher;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeRegistry {

    public static void registerRecipes() {
        Plugin plugin = JavaPlugin.getPlugin(Spellcasting.class);
        registerCrushedOreSmeltingRecipes(plugin);
    }

    private static void registerCrushedOreSmeltingRecipes(Plugin plugin) {
        for (Crusher.CrushingRecipe crushingRecipe : Crusher.CrushingRecipe.values()) {
            ItemStack resultItem = crushingRecipe.getResultItem();

            String baseKey = PlainTextComponentSerializer.plainText().serialize(resultItem.getData(DataComponentTypes.ITEM_NAME))
                    .toLowerCase()
                    .replace(" ", "_");

            // Register Furnace recipe
            NamespacedKey furnaceRecipeKey = new NamespacedKey(plugin, baseKey + "_smelt");
            FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                    furnaceRecipeKey,
                    new ItemStack(getCorrespondingIngot(crushingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(crushingRecipe),
                    200 // 10 seconds
            );
            plugin.getServer().addRecipe(furnaceRecipe);

            // Register Blast Furnace recipe
            NamespacedKey blastingRecipeKey = new NamespacedKey(plugin, baseKey + "_blast");
            BlastingRecipe blastingRecipe = new BlastingRecipe(
                    blastingRecipeKey,
                    new ItemStack(getCorrespondingIngot(crushingRecipe)),
                    new RecipeChoice.ExactChoice(resultItem),
                    getExperienceAmount(crushingRecipe),
                    100 // 5 seconds
            );
            plugin.getServer().addRecipe(blastingRecipe);
        }
    }

    private static Material getCorrespondingIngot(Crusher.CrushingRecipe crushingRecipe) {
        return switch (crushingRecipe) {
            case CRUSHED_COPPER -> Material.COPPER_INGOT;
            case CRUSHED_IRON -> Material.IRON_INGOT;
            case CRUSHED_GOLD -> Material.GOLD_INGOT;
        };
    }

    // Smelting crushed ores gives half the amount of exp as the raw ore would've given
    public static float getExperienceAmount(Crusher.CrushingRecipe crushingRecipe) {
        return switch (crushingRecipe) {
            case CRUSHED_COPPER -> 0.35f;
            case CRUSHED_IRON -> 0.35f;
            case CRUSHED_GOLD -> 0.5f;
        };
    }
}