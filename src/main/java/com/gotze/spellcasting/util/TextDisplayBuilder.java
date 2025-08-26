package com.gotze.spellcasting.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.joml.Matrix4f;

@Deprecated
public class TextDisplayBuilder {

    // Base transformation that accounts for Minecraft's text positioning quirks
    // Modification happens in reverse order (Downscale -> Move -> Upscale)
    private Matrix4f transformation = new Matrix4f()
            .scale(0.125f, 0.25f, 1f) // Third -> Upscale
            .translate(-0.1f, 0f, 0f) // Second -> Move
            .scale(8.0f, 4.0f, 1f);   // First -> Downscale

    private final Location location;

    public TextDisplayBuilder(Location location) {
        this.location = location;
    }

    public TextDisplayBuilder copy() {
        TextDisplayBuilder copy = new TextDisplayBuilder(this.location);
        copy.transformation = new Matrix4f(this.transformation);
        return copy;
    }

    public TextDisplayBuilder scale(float scale) {
        this.transformation = new Matrix4f()
                .scale(scale)
                .mul(transformation);
        return this;
    }

    public TextDisplayBuilder scale(float x, float y, float z) {
        this.transformation = new Matrix4f()
                .scale(x, y, z)
                .mul(transformation);
        return this;
    }

    public TextDisplayBuilder rotateX(float degrees) {
        this.transformation = new Matrix4f()
                .rotateX((float) Math.toRadians(degrees))
                .mul(transformation);
        return this;
    }

    public TextDisplayBuilder rotateY(float degrees) {
        this.transformation = new Matrix4f()
                .rotateY((float) Math.toRadians(degrees))
                .mul(transformation);
        return this;
    }

    public TextDisplayBuilder rotateZ(float degrees) {
        this.transformation = new Matrix4f()
                .rotateZ((float) Math.toRadians(degrees))
                .mul(transformation);
        return this;
    }

    public TextDisplayBuilder translate(float x, float y, float z) {
        this.transformation = new Matrix4f()
                .translate(x, y, z)
                .mul(transformation);
        return this;
    }

    public TextDisplay build() {
        TextDisplay textDisplay = (TextDisplay) this.location.getWorld().spawnEntity(this.location, EntityType.TEXT_DISPLAY);

        textDisplay.setTransformationMatrix(this.transformation);

        // Apply default settings
        textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        textDisplay.setBrightness(new Display.Brightness(15, 15));
        textDisplay.setSeeThrough(false);
        textDisplay.setTextOpacity((byte) 255);

        return textDisplay;
    }
}