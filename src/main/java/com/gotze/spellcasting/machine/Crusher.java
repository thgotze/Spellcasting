package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.util.ItemStackBuilder;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public class Crusher extends Machine {

    private static final int INPUT_SLOT = 11;
    private static final int OUTPUT_SLOT = 15;
    private static final int DEFAULT_PROCESSING_TIME_IN_TICKS = 200;

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
        for (int i = 0; i <= 10; i++) {
            getInventory().setItem(i, new ItemStackBuilder(Material.PAPER)
                    .itemModel(NamespacedKey.minecraft("air"))
                    .hideTooltipBox()
                    .build());
        }

        for (int i = 16; i <= 26; i++) {
            getInventory().setItem(i, new ItemStackBuilder(Material.PAPER)
                    .itemModel(NamespacedKey.minecraft("air"))
                    .hideTooltipBox()
                    .build());
        }

        getInventory().setItem(12, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_left_arrow00"))
                .hideTooltipBox()
                .build());

        getInventory().setItem(13, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_saws0"))
                .hideTooltipBox()
                .build());

        getInventory().setItem(14, new ItemStackBuilder(Material.PAPER)
                .itemModel(NamespacedKey.minecraft("crusher_right_arrow00"))
                .hideTooltipBox()
                .build());
    }

    @Override
    public void tick() {
        if (progress == 0) {
            getInventory().getItem(12).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_left_arrow00")));

            getInventory().getItem(14).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_right_arrow00")));
        } else {
            int frame = progress % 3;
            getInventory().getItem(13).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_saws" + frame)));
        }

        // Check if there is something in the input slot
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

        // Check if the output item matches the result item
        ItemStack resultItem = crushingRecipe.getResultItem();
        ItemStack outputItem = getOutputItem();
        if (outputItem != null) {
            // Output slot has items - check if we can add more
            if (!outputItem.isSimilar(resultItem)) {
                progress = 0;
                return; // Different item type
            }
            if (outputItem.getAmount() + resultItem.getAmount() > outputItem.getMaxStackSize()) {
                progress = 0;
            }
            return; // Would overflow
        }

        // ---------------
        // At this point we know that the machine can process the item
        // ---------------
        if (progress < 100) {
            progress++;

            int leftArrowFrame = (progress * 14) / 100;
            String leftFrameStr = String.format("%02d", leftArrowFrame);

            getInventory().getItem(12).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_left_arrow" + leftFrameStr)));

        } else if (progress < 200) {
            progress++;

            int rightArrowFrame = ((progress - 100) * 14) / 100;
            String rightFrameStr = String.format("%02d", rightArrowFrame);

            getInventory().getItem(14).editMeta(itemMeta ->
                    itemMeta.setItemModel(NamespacedKey.minecraft("crusher_right_arrow" + rightFrameStr)));

        } else if (progress >= DEFAULT_PROCESSING_TIME_IN_TICKS) {
            progress = 0;

            inputItem.subtract();

            if (outputItem != null) {
                outputItem.add(2);
            } else {
                setOutputItem(resultItem);
            }
            getLocation().getWorld().playEffect(getLocation(), Effect.STEP_SOUND, crushingRecipe.getBlockData());
        }
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
        CRUSHED_COPPER(Material.RAW_COPPER,
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Copper"))
                        .itemModel(NamespacedKey.minecraft("crushed_copper"))
                        .amount(2)
                        .build(),
                Material.RAW_COPPER_BLOCK.createBlockData()),
        CRUSHED_IRON(Material.RAW_IRON,
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Iron"))
                        .itemModel(NamespacedKey.minecraft("crushed_iron"))
                        .amount(2)
                        .build(),
                Material.RAW_IRON_BLOCK.createBlockData()),
        CRUSHED_GOLD(Material.RAW_GOLD,
                new ItemStackBuilder(Material.PAPER)
                        .name(text("Crushed Gold"))
                        .itemModel(NamespacedKey.minecraft("crushed_gold"))
                        .amount(2)
                        .build(),
                Material.RAW_GOLD_BLOCK.createBlockData()),
        ;

        private final Material ingredient;
        private final ItemStack resultItem;
        private final BlockData blockData;

        CrushingRecipe(Material ingredient, ItemStack resultItem, BlockData blockData) {
            this.ingredient = ingredient;
            this.resultItem = resultItem;
            this.blockData = blockData;
        }

        public Material getIngredient() {
            return ingredient;
        }

        public ItemStack getResultItem() {
            return resultItem.clone();
        }

        public BlockData getBlockData() {
            return blockData;
        }

        public static @Nullable CrushingRecipe fromMaterial(Material material) {
            for (CrushingRecipe recipe : values()) {
                if (recipe.getIngredient() == material) {
                    return recipe;
                }
            }
            return null;
        }
    }
}