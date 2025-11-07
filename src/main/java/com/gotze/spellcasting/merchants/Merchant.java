package com.gotze.spellcasting.merchants;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Merchant implements Listener {

    private static final Map<UUID, Inventory> VILLAGER_INVENTORY_MAP = new HashMap<>();

    public Merchant() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        registerVillager(world, new Location(world, 630.5, 245, 586.5),
                Villager.Type.TAIGA, Villager.Profession.ARMORER, "Machine Seller");
        registerVillager(world, new Location(world, 626.5, 245, 586.5),
                Villager.Type.SNOW, Villager.Profession.CARTOGRAPHER, "Ore Merchant");
        registerVillager(world, new Location(world, 622.5, 245, 586.5),
                Villager.Type.SAVANNA, Villager.Profession.LIBRARIAN, "Token Seller");
    }

    private void registerVillager(World world, Location loc, Villager.Type type, Villager.Profession prof, String name) {
        if (!world.isChunkLoaded(loc.getChunk())) {
            world.loadChunk(loc.getChunk());
        }
        for (Villager existing : world.getEntitiesByClass(Villager.class)) {
            if (existing.getLocation().distanceSquared(loc) < 1 && existing.customName() != null &&
                    PlainTextComponentSerializer.plainText().serialize(existing.customName()).equals(name)) {

                Inventory villagerInventory = fillVillagerInventory(existing, name);
                VILLAGER_INVENTORY_MAP.put(existing.getUniqueId(), villagerInventory);
                return;
            }
        }

        Villager villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
        villager.customName(text(name));
        villager.setVillagerType(type);
        villager.setProfession(prof);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setCollidable(false);
        villager.setPersistent(true);
        villager.setSilent(true);
        villager.setCustomNameVisible(true);
        villager.setAware(true);

        Inventory villagerInventory = fillVillagerInventory(villager, name);

        VILLAGER_INVENTORY_MAP.put(villager.getUniqueId(), villagerInventory);
    }

    private Inventory fillVillagerInventory(Villager villager, String name) {
        Inventory villagerInventory = Bukkit.createInventory(villager, 27, text(name));

        villagerInventory.setItem(0, ItemStack.of(Material.PAPER));
        villagerInventory.setItem(3, ItemStack.of(Material.PAPER));
        villagerInventory.setItem(4, ItemStack.of(Material.PAPER));
        villagerInventory.setItem(7, ItemStack.of(Material.PAPER));
        villagerInventory.setItem(25, ItemStack.of(Material.PAPER));

        return villagerInventory;
    }

    @EventHandler
    public void onRightClickVillager(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;

        Inventory villagerInventory = VILLAGER_INVENTORY_MAP.get(villager.getUniqueId());
        if (villagerInventory == null) return;

        event.setCancelled(true);

        String villagerName = PlainTextComponentSerializer.plainText().serialize(villager.customName());

        if (villagerName.equals("Ore Merchant")) {
            new OreMerchantMenu(event.getPlayer());
            return;

        } else if (villagerName.equals("Token Seller"))  {
            event.getPlayer().sendMessage("you clicked the " + villagerName);

        } else if (villagerName.equals("Ore Merchant")) {
            event.getPlayer().sendMessage("you clicked the " + villagerName);
        }

            Inventory playerInv = Bukkit.createInventory(villager, villagerInventory.getSize(), text(villagerName));
        playerInv.setContents(villagerInventory.getContents().clone());

        event.getPlayer().openInventory(playerInv);
    }
}