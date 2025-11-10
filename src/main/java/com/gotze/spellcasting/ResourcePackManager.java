package com.gotze.spellcasting;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.URI;
import java.util.UUID;

public class ResourcePackManager implements Listener {

    private static final URI PACK_URI = URI.create("https://download.mc-packs.net/pack/1596d8f15753ebb16f6870979ad427df8deb880a.zip");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ResourcePackInfo.resourcePackInfo()
                .uri(PACK_URI)
                .id(UUID.randomUUID())
                .computeHashAndBuild()
                .thenAccept(packInfo -> {
                    ResourcePackRequest packRequest = ResourcePackRequest.resourcePackRequest()
                            .packs(packInfo)
                            .required(true)
                            .prompt(Component.text("This server uses a custom resource pack."))
                            .build();

                    player.sendResourcePacks(packRequest);
                });
    }
}