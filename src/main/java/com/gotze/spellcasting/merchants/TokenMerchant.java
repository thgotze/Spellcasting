package com.gotze.spellcasting.merchants;

import com.gotze.spellcasting.data.PlayerProfile;
import com.gotze.spellcasting.pickaxe.ability.Ability;
import com.gotze.spellcasting.pickaxe.enchantment.Enchantment;
import com.gotze.spellcasting.util.ItemStackBuilder;
import com.gotze.spellcasting.util.SoundUtils;
import com.gotze.spellcasting.util.StringUtils;
import com.gotze.spellcasting.util.menu.Button;
import com.gotze.spellcasting.util.menu.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class TokenMerchant extends Merchant {

    public TokenMerchant() {
        super(5, text("Token Merchant"), false,
                "Token Merchant", new Location(Bukkit.getWorld("world"), 10.5, 97, 13.5, 22.5f, 0),
                Villager.Type.SAVANNA, Villager.Profession.LIBRARIAN);
        populate();
    }

    @Override
    protected void populate() {
        setItem(4, MenuUtils.FRAME_ITEM);
        setItem(13, MenuUtils.FRAME_ITEM);
        setItem(22, MenuUtils.FRAME_ITEM);
        setItem(31, MenuUtils.FRAME_ITEM);
        setItem(40, MenuUtils.FRAME_ITEM);

        int startingEnchantmentIndex = 0;
        for (Enchantment.EnchantmentType enchantmentType : Enchantment.EnchantmentType.values()) {
            if (startingEnchantmentIndex == 4 ||
                    startingEnchantmentIndex == 13 ||
                    startingEnchantmentIndex == 22 ||
                    startingEnchantmentIndex == 31) {
                startingEnchantmentIndex += 5;
            }
            setButton(new Button(startingEnchantmentIndex++, new ItemStackBuilder(enchantmentType.getUpgradeToken())
                    .lore(empty(),
                            text(StringUtils.convertToSmallFont("price: "), GOLD)
                                    .append(text("$250", WHITE)))
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    PlayerProfile profile = PlayerProfile.of(player);

                    double balance = profile.getBalance();
                    if (balance >= 250) {
                        profile.setBalance(balance - 250);
                        player.give(enchantmentType.getUpgradeToken());
                        player.sendMessage(text("You bought 1x [", GREEN)
                                .append(enchantmentType.getUpgradeTokenName())
                                .append(text("] for $250", GREEN)));
                        SoundUtils.playSuccessSound(player);

                    } else {
                        player.sendMessage(text("You cannot afford this", RED));
                        SoundUtils.playBassNoteBlockErrorSound(player);
                    }
                }
            });
        }

        int startingAbilityIndex = 5;
        for (Ability.AbilityType abilityType : Ability.AbilityType.values()) {
            if (startingAbilityIndex == 9 ||
                    startingAbilityIndex == 18 ||
                    startingAbilityIndex == 27 ||
                    startingAbilityIndex == 36) {
                startingAbilityIndex += 5;
            }
            setButton(new Button(startingAbilityIndex++, new ItemStackBuilder(abilityType.getUpgradeToken())
                    .lore(empty(),
                            text(StringUtils.convertToSmallFont("price: "), GOLD)
                                    .append(text("$2500", WHITE)))
                    .build()) {
                @Override
                public void onButtonClick(InventoryClickEvent event) {
                    Player player = (Player) event.getWhoClicked();
                    PlayerProfile profile = PlayerProfile.of(player);

                    double balance = profile.getBalance();
                    if (balance >= 2500) {
                        profile.setBalance(balance - 2500);
                        player.give(abilityType.getUpgradeToken());
                        player.sendMessage(text("You bought 1x [", GREEN)
                                .append(abilityType.getUpgradeTokenName())
                                .append(text("] for $2500", GREEN)));
                        SoundUtils.playSuccessSound(player);
                    } else {
                        player.sendMessage(text("You cannot afford this", RED));
                        SoundUtils.playBassNoteBlockErrorSound(player);
                    }
                }
            });
        }
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