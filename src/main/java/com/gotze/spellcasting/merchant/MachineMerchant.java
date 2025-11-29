package com.gotze.spellcasting.merchant;

import com.gotze.spellcasting.data.PlayerProfile;
import com.gotze.spellcasting.machine.Machine;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class MachineMerchant extends Merchant {

    public MachineMerchant() {
        super(5, text("Machine Merchant"), false,
                "Machine Merchant", new Location(Bukkit.getWorld("world"), 4.5, 97, 21.5),
                Villager.Type.TAIGA, Villager.Profession.ARMORER);
        populate();
    }

    @Override
    protected void populate() {
        setButton(new Button(20, Machine.MachineType.CRUSHER.getMachineItem()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                PlayerProfile profile = PlayerProfile.fromPlayer(player);

                double balance = profile.getBalance();
                if (balance >= 10000) {
                    profile.setBalance(balance - 10000);
                    player.getInventory().addItem(Machine.MachineType.CRUSHER.getMachineItem());
                    player.sendMessage(text("You bought 1x [", GREEN)
                            .append(Machine.MachineType.CRUSHER.getFormattedName())
                            .append(text("] for $10000", GREEN)));
                    SoundUtils.playSuccessSound(player);

                } else {
                    player.sendMessage(text("You cannot afford this", RED));
                    SoundUtils.playBassNoteBlockErrorSound(player);
                }
            }
        });

        setButton(new Button(22, Machine.MachineType.SIFTER.getMachineItem()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                PlayerProfile profile = PlayerProfile.fromPlayer(player);

                double balance = profile.getBalance();
                if (balance >= 10000) {
                    profile.setBalance(balance - 10000);
                    player.getInventory().addItem(Machine.MachineType.SIFTER.getMachineItem());
                    player.sendMessage(text("You bought 1x [", GREEN)
                            .append(Machine.MachineType.SIFTER.getFormattedName())
                            .append(text("] for $10000", GREEN)));
                    SoundUtils.playSuccessSound(player);
                } else {
                    player.sendMessage(text("You cannot afford this", RED));
                    SoundUtils.playBassNoteBlockErrorSound(player);
                }
            }
        });

        setButton(new Button(24, Machine.MachineType.WASHER.getMachineItem()) {
            @Override
            public void onButtonClick(InventoryClickEvent event) {
                Player player = (Player) event.getWhoClicked();
                PlayerProfile profile = PlayerProfile.fromPlayer(player);

                double balance = profile.getBalance();
                if (balance >= 10000) {
                    profile.setBalance(balance - 10000);
                    player.getInventory().addItem(Machine.MachineType.WASHER.getMachineItem());
                    player.sendMessage(text("You bought 1x [", GREEN)
                            .append(Machine.MachineType.WASHER.getFormattedName())
                            .append(text("] for $10000", GREEN)));
                    SoundUtils.playSuccessSound(player);
                } else {
                    player.sendMessage(text("You cannot afford this", RED));
                    SoundUtils.playBassNoteBlockErrorSound(player);
                }
            }
        });
    }

    @Override
    protected void onInventoryOpen(InventoryOpenEvent event) {

    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event) {

    }

    @Override
    protected void onTopInventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void onBottomInventoryClick(InventoryClickEvent event) {

    }
}
