package com.gotze.magicParticles;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class LaserSpell extends AbstractSpell {

    public LaserSpell(JavaPlugin plugin, Location location, Player player) {
        super(plugin, location, player);
        spawn();
    }

    @Override
    protected void spawn() {
        ArrayList<TextDisplay> displays = new ArrayList<>();

        Location spawnLocation = player.getLocation().getWorld().getBlockAt(player.getLocation()).getLocation();

        TextDisplayBuilder baseDisplay = new TextDisplayBuilder(spawnLocation);

        TextDisplay red = baseDisplay.copy()
                .scale(2,1,1)
                .translate(0, 0,-0.125f)
                .build();
        red.setBackgroundColor(Color.fromARGB(255,255, 0, 0));
        red.text(Component.text(" "));
        TextDisplay redB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(180)
                .translate(0, 0,-0.125f)
                .build();
        redB.setBackgroundColor(Color.fromARGB(255,255, 0, 0));
        redB.text(Component.text(" "));




        TextDisplay green = baseDisplay.copy()
                .scale(2,1,1)
                .translate(0,0,0.125f)
                .build();
        green.setBackgroundColor(Color.fromARGB(255,0, 255, 0));
        green.text(Component.text(" "));
        TextDisplay greenB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(180)
                .translate(0,0,0.125f)
                .build();
        greenB.setBackgroundColor(Color.fromARGB(255,0, 255, 0));
        greenB.text(Component.text(" "));



        TextDisplay blue = baseDisplay.copy()
                .scale(2,1,1)
                .rotateX(90)
                .translate(0,0.25f,-0.125f)
                .build();
        blue.setBackgroundColor(Color.fromARGB(255,0, 0, 255));
        blue.text(Component.text(" "));
        TextDisplay blueB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(180)
                .rotateX(90)
                .translate(0,0.25f,-0.125f)
                .build();
        blueB.setBackgroundColor(Color.fromARGB(255,0, 0, 255));
        blueB.text(Component.text(" "));



        TextDisplay black = baseDisplay.copy()
                .scale(2,1,1)
                .rotateX(90)
                .translate(0,0,-0.125f)
                .build();
        black.setBackgroundColor(Color.fromARGB(255,0, 0, 0));
        black.text(Component.text(" "));

        TextDisplay blackB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateX(90)
                .rotateZ(180)
                .translate(0,0,-0.125f)
                .build();
        blackB.setBackgroundColor(Color.fromARGB(255,0, 0, 0));
        blackB.text(Component.text(" "));




        TextDisplay white = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(90)
                .translate(0.125f,0f,0)
                .build();
        white.setBackgroundColor(Color.fromARGB(255,255, 255, 255));
        white.text(Component.text(" "));

        TextDisplay whiteB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(270)
                .translate( 0.125f,0f,0)
                .build();
        whiteB.setBackgroundColor(Color.fromARGB(255,255, 255, 255));
        whiteB.text(Component.text(" "));


        TextDisplay gray = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(90)
                .translate(-0.125f,0f,0)
                .build();
        gray.setBackgroundColor(Color.fromARGB(255,125, 125, 125));
        gray.text(Component.text(" "));

        TextDisplay grayB = baseDisplay.copy()
                .scale(2,1,1)
                .rotateY(270)
                .translate(-0.125f,0f,0)
                .build();
        grayB.setBackgroundColor(Color.fromARGB(255,125, 125, 125));
        grayB.text(Component.text(" "));


        displays.add(red);
        displays.add(redB);

        displays.add(green);
        displays.add(greenB);

        displays.add(blue);
        displays.add(blueB);

        displays.add(gray);
        displays.add(grayB);

        displays.add(black);
        displays.add(blackB);

        displays.add(white);
        displays.add(whiteB);

    }

    @Override
    protected void remove() {

    }
}
