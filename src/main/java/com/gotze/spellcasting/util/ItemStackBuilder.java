package com.gotze.spellcasting.util;

import com.gotze.spellcasting.Spellcasting;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

public class ItemStackBuilder {
    private ItemStack itemStack;
    private Material material;
    private int amount = 1;
    private Component name;
    private List<Component> lore;
    private Key itemModel;
    private boolean hideAttributes;
    private boolean hideTooltipBox;
    private boolean hideEnchantTooltip;
    private boolean enchantmentGlint;
    private Map<Enchantment, Integer> enchantments;
    private Map<String, String> persistentDataContainer;
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
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.add(lore);
        return this;
    }

    public ItemStackBuilder lore(Component... lore) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemStackBuilder lore(List<Component> lore) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.addAll(lore);
        return this;
    }

    public ItemStackBuilder itemModel(Material itemModel) {
        this.itemModel = itemModel.getKey();
        return this;
    }

    public ItemStackBuilder itemModel(Key itemModel) {
        this.itemModel = itemModel;
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
        this.enchantmentGlint = true;
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<>();
        }
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemStackBuilder persistentDataContainer(String key, String value) {
        if (this.persistentDataContainer == null) {
            this.persistentDataContainer = new HashMap<>();
        }
        this.persistentDataContainer.put(key, value);
        return this;
    }

    public ItemStackBuilder durabilityDamage(int durabilityDamage) {
        this.durabilityDamage = durabilityDamage;
        return this;
    }

    public ItemStackBuilder maxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
        return this;
    }

    public ItemStackBuilder maxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public ItemStack build() {
        if (material != null) {
            this.itemStack = ItemStack.of(material);
        }

        if (name != null) {
            Component fixedName = name.colorIfAbsent(WHITE);
            itemStack.setData(DataComponentTypes.ITEM_NAME, fixedName);
        }

        if (amount > 1) {
            itemStack.setAmount(amount);
        }

        if (lore != null) {
            List<Component> fixedLore = lore.stream()
                    .map(component -> component
                            .colorIfAbsent(WHITE)
                            .decorationIfAbsent(ITALIC, FALSE)
                    ).toList();

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(fixedLore));
        }

        if (itemModel != null) {
            itemStack.setData(DataComponentTypes.ITEM_MODEL, itemModel);
        }

        if (hideAttributes || hideEnchantTooltip || hideTooltipBox) {
            TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();

            if (hideAttributes) {
                tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            }
            if (hideEnchantTooltip) {
                tooltipBuilder.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
            }
            if (hideTooltipBox) {
                tooltipBuilder.hideTooltip(true);
            }

            itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());
        }

        if (enchantmentGlint) {
            itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        if (enchantments != null) {
            itemStack.addEnchantments(enchantments);
        }

        if (persistentDataContainer != null) {
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

        if (maxStackSize > 0) {
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
        }

        return itemStack;
    }
}