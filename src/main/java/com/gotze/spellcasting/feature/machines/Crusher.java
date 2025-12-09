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

import static net.kyori.adventure.text.Component.text;

public class Crusher extends Machine {

    private static final int[] EMPTY_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private static final int INPUT_SLOT = 11;
    private static final int LEFT_ARROW_SLOT = 12;
    private static final int CRUSHER_SAWS_SLOT = 13;
    private static final int RIGHT_ARROW_SLOT = 14;
    private static final int OUTPUT_SLOT = 15;
//    private static final String[] LEFT_ARROW_SPRITES = {
//            "crusher_left_arrow00", "crusher_left_arrow01", "crusher_left_arrow02",
//            "crusher_left_arrow03", "crusher_left_arrow04", "crusher_left_arrow05",
//            "crusher_left_arrow06", "crusher_left_arrow07", "crusher_left_arrow08",
//            "crusher_left_arrow09", "crusher_left_arrow10", "crusher_left_arrow11",
//            "crusher_left_arrow12", "crusher_left_arrow13", "crusher_left_arrow14"
//    };
//
//    private static final String[] RIGHT_ARROW_SPRITES = {
//            "crusher_right_arrow00", "crusher_right_arrow01", "crusher_right_arrow02",
//            "crusher_right_arrow03", "crusher_right_arrow04", "crusher_right_arrow05",
//            "crusher_right_arrow06", "crusher_right_arrow07", "crusher_right_arrow08",
//            "crusher_right_arrow09", "crusher_right_arrow10", "crusher_right_arrow11",
//            "crusher_right_arrow12", "crusher_right_arrow13", "crusher_right_arrow14"
//    };

    public Crusher(Location location, Player player) {
        super(MachineType.CRUSHER, location, player);
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

        setButton(new Button(CRUSHER_SAWS_SLOT, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_saws0"))
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

        // Check if the input is a valid crushing recipe ingredient
        ItemStack resultItem = CrushingRecipe.fromIngredient(inputItem);
        if (resultItem == null) {
            resetProcess();
            return;
        }

        // Check if the output item matches the result item
        ItemStack outputItem = getOutputItem();
        if (outputItem != null) {
            // Output slot has items - check if we can add more
            if (!outputItem.isSimilar(resultItem)) {
                resetProcess(); // Different item type
                return;

            } else if (outputItem.getAmount() + resultItem.getAmount() > outputItem.getMaxStackSize()) {
                resetProcess(); // Would overflow
                return;
            }
        }

        // ---------------
        // At this point we know that the output slot is empty or that it already
        // holds some of the result item, so we can start processing
        // ---------------
        this.progress++;

        int frame = progress % 3;
        getInventory().getItem(CRUSHER_SAWS_SLOT).editMeta(itemMeta ->
                itemMeta.setItemModel(NamespacedKey.minecraft("crusher_saws" + frame)));

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

    public enum CrushingRecipe {
        CRUSHED_COPPER_ORE(ItemStack.of(Material.RAW_COPPER),
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Copper Ore"))
                        .itemModel(NamespacedKey.minecraft("crushed_copper_ore"))
                        .amount(2)
                        .build()),
        CRUSHED_IRON_ORE(ItemStack.of(Material.RAW_IRON),
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Iron Ore"))
                        .itemModel(NamespacedKey.minecraft("crushed_iron_ore"))
                        .amount(2)
                        .build()),
        CRUSHED_GOLD_ORE(ItemStack.of(Material.RAW_GOLD),
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Gold Ore"))
                        .itemModel(NamespacedKey.minecraft("crushed_gold_ore"))
                        .amount(2)
                        .build()),
        ;

        private final ItemStack ingredientItem;
        private final ItemStack resultItem;

        CrushingRecipe(ItemStack ingredientItem, ItemStack resultItem) {
            this.ingredientItem = ingredientItem;
            this.resultItem = resultItem;
        }

        public ItemStack getResultItem() {
            return resultItem.clone();
        }

        public static @Nullable ItemStack fromIngredient(ItemStack ingredientItem) {
            for (CrushingRecipe recipe : values()) {
                if (ingredientItem.isSimilar(recipe.ingredientItem)) {
                    return recipe.resultItem.clone();
                }
            }
            return null;
        }
    }
}
