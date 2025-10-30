package com.gotze.spellcasting.pickaxe;

import com.gotze.spellcasting.Spellcasting;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.MenuUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

public class PlayerPickaxeService {
    private static final Map<Player, PickaxeData> PLAYER_PICKAXE_DATA_MAP = new HashMap<>();

    public static PickaxeData pickaxeData(Player player) {
        return PLAYER_PICKAXE_DATA_MAP.get(player);
    }

    public static void resetPickaxeData(Player player) {
        PLAYER_PICKAXE_DATA_MAP.put(player, new PickaxeData());
    }

    public static void loadPickaxeDataFromYAML(Player player) {
        PickaxeData pickaxeData = pickaxeData(player);
        if (pickaxeData != null) return;

        pickaxeData = new PickaxeData();
        PLAYER_PICKAXE_DATA_MAP.put(player, pickaxeData);

        UUID uuid = player.getUniqueId();
        File playerFile = new File(JavaPlugin.getPlugin(Spellcasting.class).getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        if (!playerFile.exists()) {
            savePickaxeDataToYAML(player);
            return;
        }

        pickaxeData.setPickaxeMaterial(PickaxeMaterial.valueOf(yamlConfiguration.getString("pickaxe-type")));
        pickaxeData.setDurabilityDamage(yamlConfiguration.getInt("durability-damage"));
        pickaxeData.addBlocksBroken(yamlConfiguration.getInt("blocks-broken"));

        for (String string : yamlConfiguration.getStringList("enchantments")) {
            String[] split = string.split(" ");

            Enchantment.EnchantmentType enchantmentType = Enchantment.EnchantmentType.valueOf(split[0]);
            int level = Integer.parseInt(split[1]);

            try {
                Enchantment enchantment = enchantmentType.getEnchantmentClass().getDeclaredConstructor().newInstance();
                enchantment.setLevel(level);
                pickaxeData.addEnchantment(enchantment);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load enchantment " + enchantmentType.name() + " from " + playerFile.getPath(), e);
            }
        }

        for (String string : yamlConfiguration.getStringList("abilities")) {
            String[] split = string.split(" ");

            Ability.AbilityType abilityType = Ability.AbilityType.valueOf(split[0]);
            int level = Integer.parseInt(split[1]);

            try {
                Ability ability = abilityType.getAbilityClass().getDeclaredConstructor().newInstance();
                ability.setLevel(level);
                pickaxeData.addAbility(ability);

            } catch (Exception e) {
                throw new RuntimeException("Failed to load ability " + abilityType.name() + " from " + playerFile.getPath(), e);
            }
        }
    }

    public static void savePickaxeDataToYAML(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(JavaPlugin.getPlugin(Spellcasting.class).getDataFolder() + "/playerdata", uuid + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        PickaxeData pickaxeData = pickaxeData(player);

        yamlConfiguration.set("pickaxe-type", pickaxeData.getPickaxeMaterial().name());
        yamlConfiguration.set("durability-damage", pickaxeData.getDurabilityDamage());
        yamlConfiguration.set("blocks-broken", pickaxeData.getBlocksBroken());

        List<String> enchantmentsSerialized = pickaxeData.getEnchantments().stream()
                .map(enchantment -> enchantment.getEnchantmentType().name() + " " + enchantment.getLevel())
                .toList();
        yamlConfiguration.set("enchantments", enchantmentsSerialized);

        List<String> abilitiesSerialized = pickaxeData.getAbilities().stream()
                .map(ability -> ability.getAbilityType().name() + " " + ability.getLevel())
                .toList();
        yamlConfiguration.set("abilities", abilitiesSerialized);

        try {
            yamlConfiguration.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack playerPickaxe(Player player) {
        PickaxeData pickaxeData = pickaxeData(player);

        PickaxeMaterial pickaxeMaterial = pickaxeData.getPickaxeMaterial();

        ItemStackBuilder builder = new ItemStackBuilder(pickaxeData.getPickaxeMaterial().getPickaxeType())
                .name(pickaxeMaterial.getFormattedPickaxeTypeName().color(pickaxeMaterial.getRarity().getColor()))
                .persistentDataContainer("owner", player.getUniqueId().toString())
                .lore(pickaxeLore(pickaxeData))
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

    public static List<Component> pickaxeLore(PickaxeData pickaxeData) {
        List<Component> lore = new ArrayList<>();

        Set<Enchantment> enchantments = pickaxeData.getEnchantments();
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

        Set<Ability> abilities = pickaxeData.getAbilities();
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

    public static boolean isItemStackPlayerOwnPickaxe(ItemStack itemStack, Player player) {
        NamespacedKey ownerKey = new NamespacedKey(JavaPlugin.getPlugin(Spellcasting.class), "owner");
        String owner = itemStack.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        return player.getUniqueId().toString().equals(owner);
    }

    public static ItemStack pickaxeCloneWithoutDurability(Player player) {
        return MenuUtils.cloneItemWithoutDamage(playerPickaxe(player));
    }
}