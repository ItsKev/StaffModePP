package io.github.itskev.staffmodepp.events;

import io.github.itskev.staffmodepp.manager.PlayerManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerEvents implements Listener {

    private Plugin plugin;
    private PlayerManager playerManager;

    public PlayerEvents(Plugin plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (playerManager.isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Server server = plugin.getServer();
        Player player = event.getPlayer();
        playerManager.getVanishedPlayers().forEach(uuid -> player.hidePlayer(server.getPlayer(uuid)));
    }
}
