package com.gotze.spellcasting.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.gotze.spellcasting.util.ItemStackCreator.createItemStack;
import static com.gotze.spellcasting.util.StringUtils.convertToSmallFont;

public class GUIUtils {

    public static final ItemStack FRAME = createItemStack(
            Material.BLACK_STAINED_GLASS_PANE,
            null,
            null,
            false,
            false,
            true
    );

    public static void setFrames(Inventory gui) {
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, GUIUtils.FRAME);
        }
        for (int i = 36; i < 45; i++) {
            gui.setItem(i, GUIUtils.FRAME);
        }
    }

    public static final ItemStack RETURN_BUTTON = createItemStack(
            Material.ARROW,
            Component.text(convertToSmallFont("← return"))
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
    );
}