package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.MenuUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

public class PlayerPickaxeService {
    public static ItemStack getPlayerPickaxe(Player player) {
        PickaxeData pickaxeData = PickaxeData.fromPlayer(player);

        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getPickaxeMaterial().getPickaxeType())
                .name(pickaxeMaterial.getFormattedPickaxeTypeName().color(pickaxeMaterial.getRarity().getColor()))
                .persistentDataContainer("owner", player.getUniqueId().toString())
                .lore(getPickaxeLore(pickaxeData))
                .hideAttributes()
                .hideEnchantTooltip()
                .durabilityDamage(pickaxeData.getDurabilityDamage());

        Enchantment efficiency = pickaxeData.getEnchantment(Enchantment.EnchantmentType.EFFICIENCY);
        Enchantment unbreaking = pickaxeData.getEnchantment(Enchantment.EnchantmentType.UNBREAKING);

        if (efficiency != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.EFFICIENCY, efficiency.getLevel());
        }
        if (unbreaking != null) {
            builder.enchant(org.bukkit.enchantments.Enchantment.UNBREAKING, unbreaking.getLevel());
        }

        ItemStack pickaxe = builder.build();

        Enchantment reach = pickaxeData.getEnchantment(Enchantment.EnchantmentType.REACH);
        if (reach != null) {
            ItemAttributeModifiers defaultData = pickaxe.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (defaultData != null) {
                ItemAttributeModifiers.Builder itemAttributesBuilder = ItemAttributeModifiers.itemAttributes();

                for (ItemAttributeModifiers.Entry entry : defaultData.modifiers()) {
                    itemAttributesBuilder.addModifier(entry.attribute(), entry.modifier());
                }

                AttributeModifier reachModifier = new AttributeModifier(
                        new NamespacedKey(Spellcasting.getPlugin(), "extended-reach-modifier"),
                        reach.getLevel(),
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND
                );
                itemAttributesBuilder.addModifier(Attribute.BLOCK_INTERACTION_RANGE, reachModifier);

                pickaxe.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, itemAttributesBuilder.build());
            }
        }

        // ---------------
        // Tool rule shenanigans
        // ---------------
        ItemStack pickaxe = builder.build();

//        Tool
//        defaultPickaxeToolData = pickaxe.getType().getDefaultData(DataComponentTypes.TOOL);
//        List<Tool.Rule> defaultRules = defaultPickaxeToolData.rules();

//        List<Tool.Rule> modifiedRules = new ArrayList<>();
//        for (Tool.Rule rule : defaultRules) {
//            // Get all block keys from this rule
//            List<TypedKey<BlockType>> blocksWithoutBlueIce = new ArrayList<>();
//
//            for (TypedKey<BlockType> blockKey : rule.blocks()) {
//                // Check if this block is NOT blue ice
//                if (!blockKey.key().equals(BlockType.BLUE_ICE.key().key())) {
//                    blocksWithoutBlueIce.add(blockKey);
//                }
//            }
//
//            // Only add the rule if it still has blocks after removing blue ice
//            if (!blocksWithoutBlueIce.isEmpty()) {
//                Tool.Rule modifiedRule = Tool.rule(
//                        RegistrySet.keySet(RegistryKey.BLOCK, Set.copyOf(blocksWithoutBlueIce)),
//                        rule.speed(),
//                        rule.correctForDrops()
//                );
//                modifiedRules.add(modifiedRule);
//            }
//        }
//
//        Tool.Rule glassRule = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
//                        List.of(BlockType.GLASS)),
//                2f,
//                TriState.TRUE);
//
//        Tool.Rule dirtRule = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
//                        List.of(BlockType.DIRT)),
//                0.10f,
//                TriState.FALSE);

//        Tool.Rule blueIceRule = Tool.rule(RegistrySet.keySetFromValues(RegistryKey.BLOCK,
//                        List.of(BlockType.BLUE_ICE)),
//                2.0f,
//                TriState.TRUE);
//
//        Tool tool = Tool.tool()
//                .addRules(modifiedRules)
//                .addRule(glassRule)
//                .addRule(dirtRule)
//                .addRule(blueIceRule)
//                .build();

//        player.sendMessage("First: " + defaultRules.getFirst().blocks().size());
//        player.sendMessage("Last: " + defaultRules.getLast().blocks().size());
//        player.sendMessage(defaultRules.size() + " ");
//        pickaxe.setData(DataComponentTypes.TOOL, tool);

//        ItemMeta meta = pickaxe.getItemMeta();
//        ToolComponent toolComponent = meta.getTool();
//
//        toolComponent.addRule(Material.BLUE_ICE, 100.0f, Boolean.TRUE);
//
//        meta.setTool(toolComponent);
//        pickaxe.setItemMeta(meta);
        return pickaxe;
//        return builder.build();
    }

    public static List<Component> getPickaxeLore(PickaxeData pickaxeData) {
        List<Component> lore = new ArrayList<>();

        List<Enchantment> enchantments = pickaxeData.getEnchantments();
        if (!enchantments.isEmpty()) {
            // Sort enchantments by rarity in descending order (Legendary -> Common)
            List<Enchantment> sortedEnchantments = enchantments.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getEnchantmentType().getRarity().ordinal(),
                            e1.getEnchantmentType().getRarity().ordinal()
                    )).toList();

            for (Enchantment enchantment : sortedEnchantments) {
                Enchantment.EnchantmentType enchantmentType = enchantment.getEnchantmentType();
                lore.add(enchantmentType.getFormattedName()
                        .append(text(" " + StringUtils.toRomanNumeral(enchantment.getLevel())))
                        .color(enchantmentType.getRarity().getColor()));
            }
            lore.add(empty());
        }

        List<Ability> abilities = pickaxeData.getAbilities();
        if (!abilities.isEmpty()) {
            // Sort abilities by rarity in descending order (Legendary -> Common)
            List<Ability> sortedAbilities = abilities.stream()
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getRarity().ordinal(),
                            e1.getRarity().ordinal()
                    ))
                    .toList();

            for (Ability ability : sortedAbilities) {
                Ability.AbilityType abilityType = ability.getAbilityType();
                lore.add(abilityType.getFormattedName()
                        .append(text(" " + StringUtils.toRomanNumeral(ability.getLevel())))
                        .color(abilityType.getRarity().getColor()));
            }
            lore.add(empty());
        }

        lore.add(text("Blocks Broken: ", GRAY)
                .append(text(pickaxeData.getBlocksBroken(), GOLD)));
        lore.add(empty());

        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();
        int damage = pickaxeData.getDurabilityDamage();
        int remaining = pickaxeMaterial.getMaxDurability() - damage;
        double percentage = (double) remaining / pickaxeMaterial.getMaxDurability();

        NamedTextColor durabilityColor;
        if (percentage >= 0.60) durabilityColor = GREEN;
        else if (percentage >= 0.30) durabilityColor = YELLOW;
        else if (percentage >= 0.20) durabilityColor = GOLD;
        else if (percentage >= 0.15) durabilityColor = RED;
        else durabilityColor = DARK_RED;

        lore.add(text("Durability: ", GRAY)
                .append(text(remaining, durabilityColor))
                .append(text(" | ", DARK_GRAY))
                .append(text(pickaxeMaterial.getMaxDurability(), GRAY))
        );

        return lore.stream().map(component -> component
                .colorIfAbsent(WHITE)
                .decorationIfAbsent(ITALIC, FALSE)
        ).toList();
    }

    public static @Nullable ItemStack getPlayerPickaxeFromMainHand(Player player, boolean notifyOnError) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!isItemStackPlayerOwnPickaxe(heldItem, player)) {
            if (notifyOnError) {
                player.sendMessage(text("You are not holding your own pickaxe!", RED));
                SoundUtils.playErrorSound(player);
            }
            return null;
        }
        return heldItem;
    }

    public static boolean isItemStackPlayerOwnPickaxe(@NotNull ItemStack itemStack, Player player) {
        NamespacedKey ownerKey = new NamespacedKey(Spellcasting.getPlugin(), "owner");
        String owner = itemStack.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        return player.getUniqueId().toString().equals(owner);
    }

    public static ItemStack pickaxeCloneWithoutDurability(Player player) {
        return MenuUtils.cloneItemWithoutDamage(getPlayerPickaxe(player));
    }
}