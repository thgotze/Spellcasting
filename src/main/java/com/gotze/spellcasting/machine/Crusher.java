package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.util.ItemStackBuilder;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Crusher implements InventoryHolder {
    private static final int INPUT_SLOT = 20;
    private static final int MIDDLE_SLOT = 22;
    private static final int OUTPUT_SLOT = 24;
    private static final int DEFAULT_PROCESSING_TIME_IN_TICKS = 10;
    private static final Map<Material, List<Integer>> FRAME_LOCATIONS = Map.of(
            Material.BLACK_STAINED_GLASS_PANE, List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 26, 27, 31, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44),
            Material.GRAY_STAINED_GLASS_PANE, List.of(10, 11, 12, 14, 15, 16, 19, 21, 23, 25, 28, 29, 30, 32, 33, 34));

    private final @NotNull Location location;
    private final @NotNull UUID placedBy;
    private Inventory inventory;
    private int progress;

    public Crusher(@NotNull Location location, @NotNull Player player) {
        this.location = location;
        this.placedBy = player.getUniqueId();
        this.inventory = defaultInventory();
    }

    private Inventory defaultInventory() {
        this.inventory = Bukkit.createInventory(this, 45);

        for (Map.Entry<Material, List<Integer>> entry : FRAME_LOCATIONS.entrySet()) {
            Material paneColor = entry.getKey();
            for (int slot : entry.getValue()) {
                inventory.setItem(slot, new ItemStackBuilder(paneColor)
                        .hideTooltipBox()
                        .build());
            }
        }

        inventory.setItem(MIDDLE_SLOT, new ItemStackBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name(text("Click to crush ores!"))
                .build());

        ItemStack itemStack = ItemStack.of(Material.PAPER);
        itemStack.editMeta(itemMeta -> itemMeta.setItemModel(NamespacedKey.minecraft("shredder_blade_left")));
        inventory.setItem(13, itemStack);
        inventory.setItem(31, itemStack);
        inventory.clear(21);
        inventory.clear(22);
        inventory.clear(23);
        return inventory;
    }

    public Location getLocation() {
        return location.clone();
    }

    public @NotNull UUID getWhoPlaced() {
        return placedBy;
    }

    public @Nullable ItemStack getInputItem() {
        return inventory.getItem(INPUT_SLOT);
    }

    public void setInputItem(@Nullable ItemStack inputItem) {
        inventory.setItem(INPUT_SLOT, inputItem);
    }

    public @Nullable ItemStack getOutputItem() {
        return inventory.getItem(OUTPUT_SLOT);
    }

    public void setOutputItem(@Nullable ItemStack outputItem) {
        inventory.setItem(OUTPUT_SLOT, outputItem);
    }

    public @NotNull ItemStack toItemStack() {
        return new ItemStackBuilder(Material.STONECUTTER)
                .name(text("Crusher"))
                .persistentDataContainer("machine", "crusher") // TODO: DEBUG
                .build();
    }

    public void tick() {
        ItemStack inputItem = getInputItem();
        if (inputItem == null) {
            progress = 0;
            return;
        }

        // Check if the input is a valid crushable material
        CrushingRecipe crushingRecipe = CrushingRecipe.fromMaterial(inputItem.getType());
        if (crushingRecipe == null) {
            progress = 0;
            return;
        }

        World world = location.getWorld();
        BlockData blockData = Material.RAW_COPPER_BLOCK.createBlockData();
//        world.playEffect(location, Effect.STEP_SOUND, blockData);
//        SoundGroup soundGroup = blockData.getSoundGroup();
//        world.playSound(location, soundGroup.getBreakSound(), soundGroup.getVolume(), soundGroup.getPitch());

        if (progress < DEFAULT_PROCESSING_TIME_IN_TICKS) {
            progress++;

            if (progress % 2 == 0) {
                inventory.setItem(12, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(14, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(30, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(32, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));

                inventory.setItem(13, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(21, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(23, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(31, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
            } else {
                inventory.setItem(13, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(21, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(23, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));
                inventory.setItem(31, ItemStack.of(Material.WHITE_STAINED_GLASS_PANE));

                inventory.setItem(12, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(14, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(30, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
                inventory.setItem(32, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
            }

            return;
        }
        progress = 0;

        ItemStack resultItem = crushingRecipe.getResultItem();
        ItemStack outputItem = getOutputItem();

        if (outputItem != null) {
            // Output slot has items - check if we can add more
            if (outputItem.getType() != resultItem.getType()) return; // Different item type
            if (outputItem.getAmount() + resultItem.getAmount() > outputItem.getMaxStackSize()) return; // Would overflow
            outputItem.add(2);
        } else {
            // Output slot is empty - set output item
            setOutputItem(resultItem);
        }
        world.playEffect(location, Effect.STEP_SOUND, blockData);

        inputItem.subtract();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public enum CrushingRecipe {
        CRUSHED_COPPER(Material.RAW_COPPER, new ItemStackBuilder(Material.GUNPOWDER)
                .name(text("Crushed Copper")).amount(2).build()),
        CRUSHED_IRON(Material.RAW_IRON, new ItemStackBuilder(Material.SUGAR)
                .name(text("Crushed Iron")).amount(2).build()),
        CRUSHED_GOLD(Material.RAW_GOLD, new ItemStackBuilder(Material.GLOWSTONE_DUST)
                .name(text("Crushed Gold")).amount(2).build()),
        ;

        private final Material ingredient;
        private final ItemStack resultItem;

        CrushingRecipe(Material ingredient, ItemStack resultItem) {
            this.ingredient = ingredient;
            this.resultItem = resultItem;
        }

        public Material getIngredient() {
            return ingredient;
        }

        public ItemStack getResultItem() {
            return resultItem.clone();
        }

        public static @Nullable CrushingRecipe fromMaterial(Material material) {
            for (CrushingRecipe recipe : values()) {
                if (recipe.ingredient == material) {
                    return recipe;
                }
            }
            return null;
        }
    }
}