package com.gotze.spellcasting.util.menu;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuUtils {

    public static final ItemStack FRAME_ITEM = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE)
            .hideTooltipBox()
            .build();

    public static void setFrames(Inventory inventory) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, MenuUtils.FRAME_ITEM);
        }
        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, MenuUtils.FRAME_ITEM);
        }
    }

    public static final ItemStack RETURN_ITEM = new ItemStackBuilder(Material.ARROW)
            .name(Component.text(StringUtils.convertToSmallFont("← return"))
                    .color(NamedTextColor.YELLOW))
            .build();

    public static ItemStack cloneItemWithoutDamage(ItemStack itemStack) {
        ItemStack clone = itemStack.clone();
        clone.resetData(DataComponentTypes.DAMAGE);
        return clone;
    }
}