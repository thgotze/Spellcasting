package com.gotze.spellcasting.util;

import com.gotze.spellcasting.Spellcasting;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder {
    private final Material material;
    private Component displayName;
    private List<Component> lore;
    private boolean hideAdditionalTooltip = false;
    private boolean hideAttributes = false;
    private boolean hideTooltipBox = false;
    private boolean hideEnchantTooltip = false;
    private boolean enchantGlint = false;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Map<String, String> persistentData = new HashMap<>();

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

    public ItemStackBuilder hideEnchantTooltip() {
        this.hideEnchantTooltip = true;
        return this;
    }

    public ItemStackBuilder toggleEnchantmentGlint() {
        this.enchantGlint = true;
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemStackBuilder setPersistentDataComponent(String key, String value) {
        this.persistentData.put(key, value);
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
                itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, data.showInTooltip(false));
            }
        }

        if (hideTooltipBox) {
            itemStack.setData(DataComponentTypes.HIDE_TOOLTIP);
        }

        if (hideEnchantTooltip) {
            ItemEnchantments enchantments = itemStack.getData(DataComponentTypes.ENCHANTMENTS);
            if (enchantments != null) {
                itemStack.setData(DataComponentTypes.ENCHANTMENTS, enchantments.showInTooltip(false));
            }
        }

        if (enchantGlint) {
            itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        if (!enchantments.isEmpty()) {
            itemStack.addEnchantments(enchantments);
        }

        if (!persistentData.isEmpty()) {
            itemStack.editPersistentDataContainer(persistentDataContainer -> {
                for (Map.Entry<String, String> entry : persistentData.entrySet()) {
                    NamespacedKey key = new NamespacedKey(Spellcasting.INSTANCE, entry.getKey());
                    persistentDataContainer.set(key, PersistentDataType.STRING, entry.getValue());
                }
            });
        }
        return itemStack;
    }
}