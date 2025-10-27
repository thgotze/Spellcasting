package com.gotze.spellcasting.machine.sifter;

import com.gotze.spellcasting.machine.Machine;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public class Sifter extends Machine {

    private static final int INPUT_SLOT = 11;
    private static final int OUTPUT_SLOT = 15;
    private static final int DEFAULT_PROCESSING_TIME_IN_TICKS = 100;

    public Sifter(Location location, Player player) {
        super(location, player);
    }

    @Override
    protected Inventory populate() {
        Inventory inventory = Bukkit.createInventory(this, 27,
                text("Sifter")
                        .color((TextColor.color(64, 64, 64))));

        return inventory;
    }

    @Override
    public void tick() {

    }

    @Override
    public ItemStack toItemStack() {
        return null;
    }

    private enum SiftingRecipe  {

    }
}
