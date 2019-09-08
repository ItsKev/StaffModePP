package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.protocollib.NicknameHandler;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VanishModule {

    private Plugin plugin;
    private List<UUID> vanishedPlayers;
    private StaffInventory staffInventory;

    public VanishModule(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        vanishedPlayers = new ArrayList<>();
        staffInventory = dataHandler.getStaffInventory();
    }

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public void toggleVanish(Player player) {
        if (isVanished(player)) {
            unVanishPlayer(player);
        } else {
            vanishPlayer(player);
        }
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void vanishPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish.Enter"));
        staffInventory.setOn(player, "Vanish Mode");
        vanishedPlayers.add(player.getUniqueId());
        String prefix = ConfigHelper.getStringFromConfig("Vanish.Prefix-In-Vanish");
        NicknameHandler.getInstance(plugin).addCustomPlayerName(player, prefix + player.getName());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        String vanishPermission = ConfigHelper.getStringFromConfig("Vanish.Permission-To-View");
        onlinePlayers.forEach(o -> {
            if (!o.hasPermission(vanishPermission)) {
                o.hidePlayer(player);
            } else {
                o.hidePlayer(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> o.showPlayer(player), 1);
            }
        });
    }

    public void unVanishPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish.Leave"));
        staffInventory.setOff(player, "Vanish Mode");
        vanishedPlayers.remove(player.getUniqueId());
        NicknameHandler.getInstance(plugin).removeCustomPlayerName(player);
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> {
            o.hidePlayer(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> o.showPlayer(player), 1);
        });
    }
}
