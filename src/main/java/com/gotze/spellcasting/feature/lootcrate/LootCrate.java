package com.gotze.spellcasting.feature.lootcrate;

import com.gotze.spellcasting.util.Rarity;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Color;
import org.bukkit.Material;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public enum LootCrate {
    COMMON(Rarity.COMMON,
            500,
            Material.WHITE_SHULKER_BOX,
            Color.WHITE,
            NamedTextColor.WHITE,
            BossBar.Color.WHITE,
            20L
    ),
    UNCOMMON(Rarity.UNCOMMON,
            1000,
            Material.LIME_SHULKER_BOX,
            Color.LIME,
            NamedTextColor.GREEN,
            BossBar.Color.GREEN,
            40L
    ),
    RARE(Rarity.RARE,
            2000,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Color.AQUA,
            NamedTextColor.AQUA,
            BossBar.Color.BLUE,
            60L
    ),
    EPIC(Rarity.EPIC,
            4000,
            Material.MAGENTA_SHULKER_BOX,
            Color.FUCHSIA,
            NamedTextColor.LIGHT_PURPLE,
            BossBar.Color.PINK,
            80L
    ),
    LEGENDARY(Rarity.LEGENDARY,
            8000,
            Material.ORANGE_SHULKER_BOX,
            Color.ORANGE,
            NamedTextColor.GOLD,
            BossBar.Color.YELLOW,
            100L
    );

    private final Rarity rarity;
    private final int requiredEnergy;
    private final Material shulkerBox;
    private final Color fireworkColor;
    private final NamedTextColor textColor;
    private final BossBar.Color bossBarColor;
    private final long titleDurationTicks;

    LootCrate(Rarity rarity, int requiredEnergy, Material shulkerBox, Color fireworkColor,
              NamedTextColor textColor, BossBar.Color bossBarColor, long titleDurationTicks) {
        this.rarity = rarity;
        this.requiredEnergy = requiredEnergy;
        this.shulkerBox = shulkerBox;
        this.fireworkColor = fireworkColor;
        this.textColor = textColor;
        this.bossBarColor = bossBarColor;
        this.titleDurationTicks = titleDurationTicks;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getRequiredEnergy() {
        return requiredEnergy;
    }

    public Material getShulkerBox() {
        return shulkerBox;
    }

    public Color getFireworkColor() {
        return fireworkColor;
    }

    public NamedTextColor textColor() {
        return textColor;
    }

    public BossBar.Color getBossBarColor() {
        return bossBarColor;
    }

    public long getTitleDurationTicks() {
        return titleDurationTicks;
    }

    public static LootCrate getRandom() {
        return ofRarity(Rarity.getRandom());
    }

    public static LootCrate ofRarity(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON;
            case UNCOMMON -> UNCOMMON;
            case RARE -> RARE;
            case EPIC -> EPIC;
            case LEGENDARY -> LEGENDARY;
        };
    }

    public BossBar initializeBossBar() {
        return BossBar.bossBar(
                computeBossBarName(0),
                0.0f,
                bossBarColor,
                BossBar.Overlay.PROGRESS
        );
    }

    public Component computeBossBarName(int energy) {
        return text(rarity + " Loot Crate: ", textColor)
                .append(text(energy + " / " + requiredEnergy, WHITE));
    }

    public Title getCrateDroppedTitle() {
        return Title.title(
                text(""),
                text(rarity + " Loot Crate Dropped!", textColor, BOLD),
                Title.Times.times(Ticks.duration(0), Ticks.duration(titleDurationTicks), Ticks.duration(5))
        );
    }
}
