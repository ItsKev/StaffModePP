package io.github.itskev.staffmodepp.events;

import io.github.itskev.staffmodepp.manager.DataHandler;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerEvents implements Listener {

    private Plugin plugin;
    private DataHandler dataHandler;

    public PlayerEvents(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (dataHandler.isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Server server = plugin.getServer();
        Player player = event.getPlayer();
        dataHandler.getVanishedPlayers().forEach(uuid -> player.hidePlayer(server.getPlayer(uuid)));
    }
}
