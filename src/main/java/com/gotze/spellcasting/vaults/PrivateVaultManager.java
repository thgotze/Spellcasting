package com.gotze.spellcasting.vaults;

import com.gotze.spellcasting.Spellcasting;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;

public class PrivateVaultManager implements Listener {

    private static final Map<Player, HashMap<Integer, Inventory>> playerPrivateVault = new HashMap<>();

    public static Inventory getPlayerPrivateVault(Player player, int vaultNumber) {
        return playerPrivateVault.get(player).get(vaultNumber);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PersistentDataContainer playerPDC = player.getPersistentDataContainer();

        HashMap<Integer, Inventory> playerVaults = playerPrivateVault.getOrDefault(player, new HashMap<>());
        playerPrivateVault.put(player, playerVaults);

        for (int vaultNumber = 1; vaultNumber <= 4; vaultNumber++) {
            Inventory vaultInventory = Bukkit.createInventory(
                    null,
                    9 * 6,
                    text("Vault #" + vaultNumber)
            );

            NamespacedKey vaultKey = new NamespacedKey(Spellcasting.getPlugin(), "private-vault-" + vaultNumber);

            if (playerPDC.has(vaultKey, PersistentDataType.STRING)) {
                String privateVaultString = playerPDC.get(vaultKey, PersistentDataType.STRING);
                ItemStack[] privateVaultItems = deserializeItems(privateVaultString);
                vaultInventory.setStorageContents(privateVaultItems);

            } else {
                playerPDC.set(vaultKey, PersistentDataType.STRING, "");
            }
            playerVaults.put(vaultNumber, vaultInventory);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Map<Integer, Inventory> vaults = playerPrivateVault.remove(player);
        if (vaults == null) return;

        PersistentDataContainer playerPDC = player.getPersistentDataContainer();

        for (Map.Entry<Integer, Inventory> entry : vaults.entrySet()) {
            int vaultNumber = entry.getKey();
            Inventory vaultInventory = entry.getValue();

            ItemStack[] vaultStorageContents = vaultInventory.getStorageContents();
            String serializedVaultItems = serializeItems(vaultStorageContents);

            NamespacedKey vaultKey = new NamespacedKey(Spellcasting.getPlugin(), "private-vault-" + vaultNumber);
            playerPDC.set(vaultKey, PersistentDataType.STRING, serializedVaultItems);
        }
    }

    public static String serializeItems(ItemStack[] items) {
        byte[] bytes = ItemStack.serializeItemsAsBytes(items);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String serializeItem(ItemStack itemStack) {
        byte[] bytes = itemStack.serializeAsBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static ItemStack[] deserializeItems(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return ItemStack.deserializeItemsFromBytes(bytes);
    }

    public static ItemStack deserializeItem(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return ItemStack.deserializeBytes(bytes);
    }
}