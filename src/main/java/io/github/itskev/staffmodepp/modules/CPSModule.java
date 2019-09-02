package io.github.itskev.staffmodepp.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CPSModule implements Listener {

    private Plugin plugin;
    private Map<UUID, CPSPlayer> cpsPlayers;

    public CPSModule(Plugin plugin) {
        this.plugin = plugin;
        cpsPlayers = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean isCPSActive(Player player) {
        return cpsPlayers.containsKey(player.getUniqueId());
    }

    public void startCPSForPlayer(Player observerPlayer, Player staffPlayer) {
        startCPSForPlayer(observerPlayer, staffPlayer, 10);
    }

    public void startCPSForPlayer(Player observedPlayer, Player staffPlayer, int duration) {
        if (!cpsPlayers.containsKey(observedPlayer.getUniqueId())) {
            cpsPlayers.put(observedPlayer.getUniqueId(),
                    new CPSPlayer(observedPlayer.getUniqueId(), staffPlayer.getUniqueId(), duration));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                CPSPlayer cpsPlayer = cpsPlayers.remove(observedPlayer.getUniqueId());
                if (cpsPlayer != null) {
                    staffPlayer.sendMessage("CPS: " + cpsPlayer.getTimesClicked() / cpsPlayer.getSecondsToObserve());
                }
            }, duration * 20);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isCPSActive(event.getPlayer())) return;
        cpsPlayers.get(event.getPlayer().getUniqueId()).increaseClickCount();
    }
}
