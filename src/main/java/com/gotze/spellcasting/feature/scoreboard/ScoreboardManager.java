package com.gotze.spellcasting.feature.scoreboard;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class ScoreboardManager implements Listener {

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        ObjectComponent playerHeadSprite = Component.object(ObjectContents.playerHead(event.getPlayer().getUniqueId()));
        ObjectComponent diamondSprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("item/diamond")));
        ObjectComponent cobblestoneSprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("block/cobblestone")));

        ObjectComponent fire0Sprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("block/fire_0")));
        ObjectComponent fire1Sprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("block/fire_1")));

        ObjectComponent soulFire0Sprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("block/soul_fire_0")));
        ObjectComponent soulFire1Sprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("block/soul_fire_1")));

        Component combinedSprites = playerHeadSprite
                .append(diamondSprite)
                .append(cobblestoneSprite)
                .append(fire0Sprite)
                .append(fire1Sprite)
                .append(soulFire0Sprite)
                .append(soulFire1Sprite);

        event.getPlayer().sendMessage(combinedSprites);

        event.getPlayer().sendActionBar(combinedSprites);

        event.getPlayer().showTitle(Title.title(combinedSprites, combinedSprites));

        Objective objective = scoreboard.registerNewObjective(
                "sidebar",
                Criteria.DUMMY,
                text("Abilty Energy")
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score topObjectiveScoreLine = objective.getScore(" ");
//        topObjectiveScoreLine.customName();
        topObjectiveScoreLine.setScore(999);
        topObjectiveScoreLine.numberFormat(NumberFormat.blank());

        ObjectComponent maceSprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("item/mace"))).color(WHITE);
        Component component1 = text(" 250", LIGHT_PURPLE).append(text(" / ",WHITE).append(text("400 ", LIGHT_PURPLE)).decoration(BOLD, false));
        Component component2 = text("⚡", RED, BOLD);

        Component combinedComponentsLine1 = maceSprite
                .append(component1)
                .append(component2);

        Score objectiveScoreLine1 = objective.getScore(" ".repeat(2));
        objectiveScoreLine1.customName(combinedComponentsLine1);
        objectiveScoreLine1.setScore(1);
        objectiveScoreLine1.numberFormat(NumberFormat.blank());

        ObjectComponent tridentSprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("item/trident"))).color(WHITE);
        Component componentLine2dot1 = text(" 125", AQUA).append(text(" / ",WHITE).append(text("300 ", AQUA)).decoration(BOLD, false));

        Component combinedComponentsLine2 = tridentSprite
                .append(componentLine2dot1)
                .append(component2);

        Score objectiveScoreLine3 = objective.getScore(" ".repeat(3));
        objectiveScoreLine3.customName(combinedComponentsLine2);
        objectiveScoreLine3.setScore(3);
        objectiveScoreLine3.numberFormat(NumberFormat.blank());

        ObjectComponent fireworkSprite = Component.object(ObjectContents.sprite(Key.key("minecraft:blocks"), Key.key("item/firework_rocket"))).color(WHITE);
        Component componentLine3dot1 = text(" 350", GOLD).append(text(" / ",WHITE).append(text("500 ", GOLD)).decoration(BOLD, false));

        Component combinedComponentsLine3 = fireworkSprite
                .append(componentLine3dot1)
                .append(component2);

        Score objectiveScoreLine4 = objective.getScore(" ".repeat(4));
        objectiveScoreLine4.customName(combinedComponentsLine3);
        objectiveScoreLine4.setScore(4);
        objectiveScoreLine4.numberFormat(NumberFormat.blank());
//
//        Score objectiveScoreLine5 = objective.getScore(" ".repeat(5));
//        objectiveScoreLine5.customName(whiteAndBlackConcreteX4);
//        objectiveScoreLine5.setScore(5);
//        objectiveScoreLine5.numberFormat(NumberFormat.blank());
//
//        Score objectiveScoreLine6 = objective.getScore(" ".repeat(6));
//        objectiveScoreLine6.customName(blackAndWhiteConcreteX4);
//        objectiveScoreLine6.setScore(6);
//        objectiveScoreLine6.numberFormat(NumberFormat.blank());
//
//        Score objectiveScoreLine7 = objective.getScore(" ".repeat(7));
//        objectiveScoreLine7.customName(whiteAndBlackConcreteX4);
//        objectiveScoreLine7.setScore(7);
//        objectiveScoreLine7.numberFormat(NumberFormat.blank());
//
//        Score objectiveScoreLine8 = objective.getScore(" ".repeat(8));
//        objectiveScoreLine8.customName(blackAndWhiteConcreteX4);
//        objectiveScoreLine8.setScore(8);
//        objectiveScoreLine8.numberFormat(NumberFormat.blank());

        event.getPlayer().setScoreboard(scoreboard);
    }
}
