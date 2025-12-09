package com.gotze.spellcasting.feature.machines;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.menu.Button;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

import static net.kyori.adventure.text.Component.text;

public class Centrifuge extends Machine {

    private static final int[] EMPTY_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int INPUT_SLOT = 11;
    private static final int LEFT_ARROW_SLOT = 12;
    private static final int CENTRIFUGE_ICON_SLOT = 13;
    private static final int RIGHT_ARROW_SLOT = 14;
    private static final int OUTPUT_SLOT = 15;

    public Centrifuge(Location location, Player player) {
        super(MachineType.CENTRIFUGE, location, player);
        populate();
    }

    @Override
    protected void populate() {
        for (int slot : EMPTY_SLOTS) {
            setButton(new Button(slot, new ItemStackBuilder(Material.PAPER)
                    .itemModel(Material.AIR)
                    .hideTooltipBox()
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    // Do nothing
                }
            });
        }

        setButton(new Button(LEFT_ARROW_SLOT, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_left_arrow00"))
                .hideTooltipBox()
                .build()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                // Do nothing
            }
        });

        setButton(new Button(CENTRIFUGE_ICON_SLOT, new ItemStackBuilder(Material.SMOKER)
                .hideTooltipBox()
                .build()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                // Do nothing
            }
        });

        setButton(new Button(RIGHT_ARROW_SLOT, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_right_arrow00"))
                .hideTooltipBox()
                .build()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                // Do nothing
            }
        });
    }

    @Override
    public void tick() {
        // Check if there is something in the input slot
        ItemStack inputItem = getInputItem();
        if (inputItem == null) {
            resetProcess();
            return;
        }

        // Check if the input is a valid centrifuge recipe ingredient
        CentrifugeRecipe centrifugeRecipe = CentrifugeRecipe.fromIngredient(inputItem);
        if (centrifugeRecipe == null) {
            resetProcess();
            return;
        }

        // Check if the output item matches the result item
        ItemStack resultItem = centrifugeRecipe.getResultItem();
        ItemStack outputItem = getOutputItem();
        if (outputItem != null) {
            // Output slot has items - check if we can add more
            if (!outputItem.isSimilar(resultItem)) {
                resetProcess(); // Different item type
                return;

            } else if (outputItem.getAmount() + resultItem.getAmount() + 1 > outputItem.getMaxStackSize()) {
                resetProcess(); // Would overflow
                return;
            }
        }
        // ---------------
        // At this point we know that the output slot is empty or that it already
        // holds some of the result item, so we can start processing
        // ---------------
        this.progress++;

        if (progress < getProcessingTime() / 2) {
            int leftArrowFrame = (progress * 14) / (getProcessingTime() / 2);
            String leftFrameStr = String.format("%02d", leftArrowFrame);

            getInventory().getItem(LEFT_ARROW_SLOT).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_left_arrow" + leftFrameStr)));

        } else if (progress < getProcessingTime()) {
            int rightArrowFrame = ((progress - getProcessingTime() / 2) * 14) / (getProcessingTime() / 2);
            String rightFrameStr = String.format("%02d", rightArrowFrame);

            getInventory().getItem(RIGHT_ARROW_SLOT).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_right_arrow" + rightFrameStr)));

        } else if (progress >= getProcessingTime()) {
            inputItem.subtract(1);

            if (ThreadLocalRandom.current().nextFloat() < centrifugeRecipe.doublingChance) {
                resultItem.add(1);
            }

            if (outputItem != null) {
                outputItem.add(resultItem.getAmount());
            } else {
                setOutputItem(resultItem);
            }
            resetProcess();
        }
    }

    private void resetProcess() {
        progress = 0;
        getInventory().getItem(LEFT_ARROW_SLOT).editMeta(itemMeta ->
                itemMeta.setItemModel(NamespacedKey.minecraft("crusher_left_arrow00")));
        getInventory().getItem(RIGHT_ARROW_SLOT).editMeta(itemMeta ->
                itemMeta.setItemModel(NamespacedKey.minecraft("crusher_right_arrow00")));
    }

    @Override
    public @Nullable ItemStack getInputItem() {
        return getInventory().getItem(INPUT_SLOT);
    }

    @Override
    public void setInputItem(@Nullable ItemStack inputItem) {
        getInventory().setItem(INPUT_SLOT, inputItem);
    }

    @Override
    public @Nullable ItemStack getOutputItem() {
        return getInventory().getItem(OUTPUT_SLOT);
    }

    @Override
    public void setOutputItem(@Nullable ItemStack outputItem) {
        getInventory().setItem(OUTPUT_SLOT, outputItem);
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event) {
        if (event.getRawSlots().contains(OUTPUT_SLOT)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }

    public enum CentrifugeRecipe {
        COPPER_DUST(Sifter.SiftingRecipe.PURIFIED_COPPER_DUST.getResultItem(),
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Copper Dust"))
                        .itemModel(NamespacedKey.minecraft("copper_dust"))
                        .build(),
                0.25f
        ),
        IRON_DUST(Sifter.SiftingRecipe.PURIFIED_IRON_DUST.getResultItem(),
                new ItemStackBuilder(Material.PAPER)
                        .itemModel(NamespacedKey.minecraft("iron_dust"))
                        .name(text("Iron Dust"))
                        .build(),
                0.25f
        ),
        GOLD_DUST(Sifter.SiftingRecipe.PURIFIED_GOLD_DUST.getResultItem(),
                new ItemStackBuilder(Material.PAPER)
                        .itemModel(NamespacedKey.minecraft("gold_dust"))
                        .name(text("Gold Dust"))
                        .build(),
                0.25f
        );

        private final ItemStack ingredient;
        private final ItemStack resultItem;
        private final float doublingChance;

        CentrifugeRecipe(ItemStack ingredient, ItemStack resultItem, float doublingChance) {
            this.ingredient = ingredient;
            this.resultItem = resultItem;
            this.doublingChance = doublingChance;
        }

        public ItemStack getResultItem() {
            return resultItem.clone();
        }

        public static @Nullable CentrifugeRecipe fromIngredient(ItemStack ingredientItem) {
            for (CentrifugeRecipe recipe : values()) {
                if (ingredientItem.isSimilar(recipe.ingredient)) {
                    return recipe;
                }
            }
            return null;
        }
    }
}
