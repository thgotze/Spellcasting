package com.gotze.spellcasting.util;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackCreator {
    /**Creates an ItemStack with customizable display name, lore, and flags.
    *@param material The material type for the ItemStack
    *@param displayName The display name of the ItemStack (null if undesired)
    *@param lore The lore of the ItemStack (null if undesired)
    *@param hideAdditionalTooltip Flag to hide additional tooltips (e.g., music disc info)
    *@param hideAttributes Flag to hide item attributes (e.g., damage, armor values)
    *@param hideTooltipBox Flag to hide the tooltip box when hovering over the item
    *@return A fully customized ItemStack**/
    public static ItemStack createItemStack(Material material, TextComponent displayName, List<TextComponent> lore, boolean hideAdditionalTooltip, boolean hideAttributes, boolean hideTooltipBox) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (displayName != null) itemMeta.displayName(displayName);
        if (lore != null) itemMeta.lore(lore);
        if (hideAdditionalTooltip) itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        if (hideAttributes) itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (hideTooltipBox) itemMeta.setHideTooltip(true);

        item.setItemMeta(itemMeta);
        return item;
    }

    // Convenience overloaded methods in descending order of parameters provided
    public static ItemStack createItemStack(Material material, TextComponent displayName, List<TextComponent> lore, boolean hideAdditionalTooltip, boolean hideAttributes) {
        return createItemStack(material, displayName, lore, hideAdditionalTooltip, hideAttributes, false);
    }

    public static ItemStack createItemStack(Material material, TextComponent displayName, List<TextComponent> lore, boolean hideAdditionalTooltip) {
        return createItemStack(material, displayName, lore, hideAdditionalTooltip, false, false);
    }

    public static ItemStack createItemStack(Material material, TextComponent displayName, List<TextComponent> lore) {
        return createItemStack(material, displayName, lore, false, false, false);
    }

    public static ItemStack createItemStack(Material material, TextComponent displayName) {
        return createItemStack(material, displayName, null, false, false, false);
    }

    public static ItemStack createItemStack(Material material) {
        return createItemStack(material, null, null, false, false, false);
    }
}