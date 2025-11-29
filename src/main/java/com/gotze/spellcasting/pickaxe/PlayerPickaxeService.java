package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.data.PickaxeData;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.MenuUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
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
                .enchantmentGlint(true)
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

        Enchantment glaciate = pickaxeData.getEnchantment(Enchantment.EnchantmentType.GLACIATE);
        if (glaciate != null) {
            Tool defaultData = pickaxe.getData(DataComponentTypes.TOOL);
            if (defaultData != null) {
                int defaultDamagePerBlock = defaultData.damagePerBlock();
                float defaultMiningSpeed = defaultData.defaultMiningSpeed();
                boolean canDestroyBlocksInCreative = defaultData.canDestroyBlocksInCreative();

                // Create a new tool builder object
                Tool.Builder toolBuilder = Tool.tool();

                // Give it the default values
                toolBuilder.damagePerBlock(defaultDamagePerBlock);
                toolBuilder.defaultMiningSpeed(defaultMiningSpeed);
                toolBuilder.canDestroyBlocksInCreative(canDestroyBlocksInCreative);

                // Modify default rules and take out the blue ice rule
                List<Tool.Rule> modifiedRules = new ArrayList<>();

                for (Tool.Rule defaultRule : defaultData.rules()) {
                    Float defaultRuleSpeed = defaultRule.speed();
                    TriState defaultCorrectForDrops = defaultRule.correctForDrops();

                    List<TypedKey<BlockType>> newBlocksInRule = new ArrayList<>();

                    for (TypedKey<BlockType> blockKey : defaultRule.blocks()) {
                        if (!blockKey.key().equals(BlockType.BLUE_ICE.key().key())) {
                            newBlocksInRule.add(blockKey);
                        }
                    }

                    Tool.Rule modifiedRule = Tool.rule(
                            RegistrySet.keySet(RegistryKey.BLOCK, newBlocksInRule),
                            defaultRuleSpeed,
                            defaultCorrectForDrops
                    );
                    modifiedRules.add(modifiedRule);
                }
                toolBuilder.addRules(modifiedRules);

                // Add the blue ice rule back in with higher speed value
                List<TypedKey<BlockType>> blueIceRuleBlock = new ArrayList<>();
                TypedKey<BlockType> blueIce = RegistryKey.BLOCK.typedKey("blue_ice");
                blueIceRuleBlock.add(blueIce);

                float blueIceMiningSpeed;
                if (pickaxeMaterial == PickaxeMaterial.WOOD || pickaxeMaterial == PickaxeMaterial.STONE) {
                    blueIceMiningSpeed = 70f;
                } else {
                    blueIceMiningSpeed = 74f;
                }

                Tool.Rule blueIceRule = Tool.rule(
                        RegistrySet.keySet(RegistryKey.BLOCK, blueIceRuleBlock),
                        blueIceMiningSpeed,
                        TriState.TRUE
                );
                toolBuilder.addRule(blueIceRule);

                // Finally, build the tool builder and set it to the pickaxe's tool data
                pickaxe.setData(DataComponentTypes.TOOL, toolBuilder.build());
            }
        }
        return pickaxe;
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