package com.gotze.spellcasting.util;


import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.gotze.spellcasting.util.StringUtils.convertToSmallFont;

public class GUIUtils {

    public static final ItemStack FRAME = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE)
            .hideTooltipBox()
            .build();

    public static void setFrames(Inventory gui) {
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, GUIUtils.FRAME);
        }
        for (int i = 36; i < 45; i++) {
            gui.setItem(i, GUIUtils.FRAME);
        }
    }

    public static final ItemStack RETURN_BUTTON = new ItemStackBuilder(Material.ARROW)
            .displayName(Component.text(convertToSmallFont("← return"))
                    .color(NamedTextColor.YELLOW))
            .build();


    public static ItemStack cloneItemWithoutDamage(ItemStack itemStack) {
        ItemStack clone = itemStack.clone();
        clone.resetData(DataComponentTypes.DAMAGE);
        return clone;
    }
}