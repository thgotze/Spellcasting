package com.gotze.spellcasting.machine;

import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;

public abstract class Machine extends Menu {
    private final MachineType machineType;
    private final Location location;
    private final UUID placedBy;
    protected int progress;

    public Machine(MachineType machineType, Location location, Player player) {
        super(machineType.menuRows, machineType.menuTitle, true);
        this.machineType = machineType;
        this.location = location;
        this.placedBy = player.getUniqueId();
        this.progress = 0;
    }

    public abstract void tick();
    public abstract @Nullable ItemStack getInputItem();
    public abstract void setInputItem(@Nullable ItemStack inputItem);
    public abstract @Nullable ItemStack getOutputItem();
    public abstract void setOutputItem(@Nullable ItemStack outputItem);

    public MachineType getMachineType() {
        return machineType;
    }

    public Location getLocation() {
        return location.clone();
    }

    public UUID getWhoPlaced() {
        return placedBy;
    }

    public int getProcessingTime() {
        return machineType.getProcessingTime();
    }

    private static final String NEG_SPACE = "\uF001";
    private static final String NEG_SPACE_8 = NEG_SPACE.repeat(8);
    private static final String NEG_SPACE_169 = NEG_SPACE.repeat(169);

    public enum MachineType {
        CRUSHER(Crusher.class,
                new ItemStackBuilder(Material.STONECUTTER)
                        .name(text("Crusher"))
                        .persistentDataContainer("machine", "crusher")
                        .build(),
                3,
                text(NEG_SPACE_8 + "\uD027", WHITE)
                        .append(text(NEG_SPACE_169 + "Crusher", color(64, 64, 64))),
                200),
        WASHER(Washer.class,
                new ItemStackBuilder(Material.CAULDRON)
                        .name(text("Washer"))
                        .persistentDataContainer("machine", "washer")
                        .build(),
                3,
                text(NEG_SPACE_8 + "\uD027", WHITE)
                        .append(text(NEG_SPACE_169 + "Washer", color(64, 64, 64))),
                200),
        SIFTER(Sifter.class,
                new ItemStackBuilder(Material.LOOM)
                        .name(text("Sifter"))
                        .persistentDataContainer("machine", "sifter")
                        .build(),
                3,
                text(NEG_SPACE_8 + "\uD027", WHITE)
                        .append(text(NEG_SPACE_169 + "Sifter", color(64, 64, 64))),
                200),
        CENTRIFUGE(Centrifuge.class,
                new ItemStackBuilder(Material.SMOKER)
                        .name(text("Centrifuge"))
                        .persistentDataContainer("machine", "centrifuge")
                        .build(),
                3,
                text(NEG_SPACE_8 + "\uD027", WHITE)
                        .append(text(NEG_SPACE_169 + "Centrifuge", color(64, 64, 64))),
                200),
        ;

        private final Class<? extends Machine> machineClass;
        private final ItemStack machineItem;
        private final int menuRows;
        private final Component menuTitle;
        private final int processingTime;

        MachineType(Class<? extends Machine> machineClass, ItemStack machineItem, int menuRows, Component menuTitle, int processingTime) {
            this.machineClass = machineClass;
            this.machineItem = machineItem;
            this.menuRows = menuRows;
            this.menuTitle = menuTitle;
            this.processingTime = processingTime;
        }

        public Class<? extends Machine> getMachineClass() {
            return machineClass;
        }

        public ItemStack getMachineItem() {
            return machineItem.clone();
        }

        public Component getFormattedName() {
            return text(StringUtils.toTitleCase(name()));
        }

        public int getProcessingTime() {
            return processingTime;
        }
    }
}