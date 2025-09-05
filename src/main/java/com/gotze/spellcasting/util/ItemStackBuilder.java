package com.gotze.spellcasting.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStackBuilder {
    private final Material material;
    private Component displayName;
    private List<Component> lore;
    private boolean hideAdditionalTooltip = false;
    private boolean hideAttributes = false;
    private boolean hideTooltipBox = false;

    public ItemStackBuilder(Material material) {
        this.material = material;
    }

    public ItemStackBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemStackBuilder lore(Component lore) {
        this.lore = List.of(lore);
        return this;
    }

    public ItemStackBuilder lore(Component... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public ItemStackBuilder lore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder hideAdditionalTooltip() {
        this.hideAdditionalTooltip = true;
        return this;
    }

    public ItemStackBuilder hideAttributes() {
        this.hideAttributes = true;
        return this;
    }

    public ItemStackBuilder hideTooltipBox() {
        this.hideTooltipBox = true;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = ItemStack.of(material);

        if (displayName != null) {
            itemStack.setData(DataComponentTypes.ITEM_NAME, displayName);
        }

        if (lore != null) {
            List<Component> fixedLore = lore.stream()
                    .map(component -> component
                            .colorIfAbsent(NamedTextColor.WHITE)
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    ).toList();

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(fixedLore));
        }

        if (hideAdditionalTooltip) {
            itemStack.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        }

        if (hideAttributes) {
            ItemAttributeModifiers data = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (data != null) {
                data = data.showInTooltip(false);
                itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, data);
            }
        }

        if (hideTooltipBox) {
            itemStack.setData(DataComponentTypes.HIDE_TOOLTIP);
        }

        return itemStack;
    }
}