package io.github.itskev.staffmodepp.events;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.protocollib.NicknameHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (dataHandler.isInStaffMode((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (dataHandler.isInStaffMode((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Server server = plugin.getServer();
        Player player = event.getPlayer();
        String vanishPermission = ConfigHelper.getStringFromConfig("Vanish.Permission-To-View");
        if (!player.hasPermission(vanishPermission)) {
            dataHandler.getVanishModule().getVanishedPlayers().forEach(uuid -> {
                Player serverPlayer = server.getPlayer(uuid);
                if (serverPlayer != null) {
                    player.hidePlayer(serverPlayer);
                }
            });
        }
        if (dataHandler.getVanishModule().isVanished(player)) {
            String prefix = ConfigHelper.getStringFromConfig("Vanish.Prefix-In-Vanish");
            TABAPI.setCustomTagNameTemporarily(player.getUniqueId(), prefix + player.getName());
            //NicknameHandler.getInstance(plugin).addCustomPlayerName(player, prefix + player.getName());
            plugin.getServer().getOnlinePlayers().forEach(o -> {
                if (!o.hasPermission(vanishPermission)) {
                    o.hidePlayer(player);
                } else {
                    o.hidePlayer(player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> o.showPlayer(player), 1);
                }
            });
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
