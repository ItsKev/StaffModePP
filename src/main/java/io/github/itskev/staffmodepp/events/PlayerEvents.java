package io.github.itskev.staffmodepp.events;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        dataHandler.getVanishModule().getVanishedPlayers().forEach(uuid -> {
            Player serverPlayer = server.getPlayer(uuid);
            if (serverPlayer != null) {
                player.hidePlayer(serverPlayer);
            }
        });
        if (dataHandler.getVanishModule().isVanished(player)) {
            plugin.getServer().getOnlinePlayers().forEach(p -> p.hidePlayer(player));
        }
        if (dataHandler.getFreezeModule().isFrozen(player)) {
            dataHandler.getFreezeModule().refreezePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (dataHandler.isInStaffMode(player)) {
            dataHandler.getNoClipModule().removeNoClipPlayer(player);
        }
        if (dataHandler.getFreezeModule().isFrozen(player)) {
            String stringFromConfig = ConfigHelper.getStringFromConfig("Freeze.CommandExecutedOnDisconnect", event.getPlayer().getDisplayName());
            if (!stringFromConfig.equals("")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                        stringFromConfig);
                dataHandler.getFreezeModule().unfreezePlayer(null, player);
            } else {
                dataHandler.getFreezeModule().unfreezePlayerTemp(player);
            }
        }
    }
}
