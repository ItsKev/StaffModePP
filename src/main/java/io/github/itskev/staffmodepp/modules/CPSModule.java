package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CPSModule implements Listener {

    private Plugin plugin;
    private DataHandler dataHandler;
    private Map<UUID, CPSPlayer> cpsPlayers;

    public CPSModule(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
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
            staffPlayer.sendMessage(ConfigHelper.getStringFromConfig("CPS-Start", observedPlayer.getDisplayName()));
            CPSPlayer player = new CPSPlayer(observedPlayer.getUniqueId(), staffPlayer.getUniqueId(), duration);
            cpsPlayers.put(observedPlayer.getUniqueId(), player);
            player.startCounter(plugin);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                CPSPlayer cpsPlayer = cpsPlayers.remove(observedPlayer.getUniqueId());
                if (cpsPlayer != null) {
                    List<Integer> cps = player.stopTaskAndGetResult();
                    staffPlayer.sendMessage("CPS Results:");
                    List<String> values = new ArrayList<>();
                    cps.forEach(integer -> {
                        if (integer > 15) {
                            values.add("§c" + integer);
                        } else {
                            values.add("§a" + integer);
                        }
                    });
                    staffPlayer.sendMessage(String.join(", ", values));
                    float average = (float) cpsPlayer.getTimesClicked() / cpsPlayer.getSecondsToObserve();
                    staffPlayer.sendMessage("CPS Average: " + (average > 15.0f ? "§c" : "§a") + String.format("%.2f", average));
                }
            }, duration * 20);
        }
    }

    @EventHandler
    public void onPlayerRightClickPlayer(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        if (dataHandler.isInStaffMode(event.getPlayer())) {
            Player playerToTest = (Player) event.getRightClicked();
            startCPSForPlayer(playerToTest, event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isCPSActive(event.getPlayer())) return;
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            cpsPlayers.get(event.getPlayer().getUniqueId()).increaseClickCount();
        }
    }
}
