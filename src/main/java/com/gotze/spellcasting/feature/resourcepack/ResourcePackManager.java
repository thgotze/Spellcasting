//package com.gotze.spellcasting;
//
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerJoinEvent;
//
//import java.net.URI;
//
//public class ResourcePackManager implements Listener {
//
//    private static final URI PACK_URI = URI.create("https://download.mc-packs.net/pack/5fd7390b899bc98f6e917f6de67c54fba10fd469.zip");
//
//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//
////        ResourcePackInfo.resourcePackInfo()
////                .uri(PACK_URI)
////                .id(UUID.randomUUID())
////                .computeHashAndBuild()
////                .thenAccept(packInfo -> {
////                    ResourcePackRequest packRequest = ResourcePackRequest.resourcePackRequest()
////                            .packs(packInfo)
////                            .required(true)
////                            .prompt(Component.text("This server uses a custom resource pack."))
////                            .build();
////
////                    player.sendResourcePacks(packRequest);
////                });
//    }
//}