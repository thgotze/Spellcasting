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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder {
    private ItemStack itemStack;
    private Material material;
    private int amount = 1;
    private Component name;
    private List<Component> lore;
    private boolean hideAdditionalTooltip = false;
    private boolean hideAttributes = false;
    private boolean hideTooltipBox = false;
    private boolean hideEnchantTooltip = false;
    private boolean enchantGlint = false;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Map<String, String> persistentDataContainer = new HashMap<>();
    private int durabilityDamage;
    private int maxDurability;
    private int maxStackSize;

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemStackBuilder(Material material) {
        this.material = material;
    }

    public ItemStackBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder name(Component name) {
        this.name = name;
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

    public ItemStackBuilder persistentDataContainer(String key, String value) {
        this.persistentDataContainer.put(key, value);
        return this;
    }

    public ItemStackBuilder setDurabilityDamage(int durabilityDamage) {
        this.durabilityDamage = durabilityDamage;
        return this;
    }

    public ItemStackBuilder setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
        return this;
    }

    public ItemStack build() {
        if (material != null) {
            this.itemStack = ItemStack.of(material);
        }

        if (name != null) {
            itemStack.setData(DataComponentTypes.ITEM_NAME, name);
        }

        if (amount > 1) {
            itemStack.setAmount(amount);
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

        if (!persistentDataContainer.isEmpty()) {
            itemStack.editPersistentDataContainer(pdc -> {
                for (Map.Entry<String, String> entry : persistentDataContainer.entrySet()) {
                    NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(Spellcasting.class), entry.getKey());
                    pdc.set(key, PersistentDataType.STRING, entry.getValue());
                }
            });
        }

        if (durabilityDamage > 0) {
            itemStack.setData(DataComponentTypes.DAMAGE, durabilityDamage);
        }

        if (maxDurability > 0) {
            itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxDurability);
        }

        return itemStack;
    }
}