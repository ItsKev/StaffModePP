package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.util.ActionBar;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VanishModule {

    private Plugin plugin;
    private DataHandler dataHandler;
    private List<UUID> vanishedPlayers;
    private StaffInventory staffInventory;

    public VanishModule(Plugin plugin, DataHandler dataHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
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
        TABAPI.setCustomTagNameTemporarily(player.getUniqueId(), prefix + player.getName());
        //NicknameHandler.getInstance(plugin).addCustomPlayerName(player, prefix + player.getName());
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
        ActionBar.setActionBar(player, dataHandler);
    }

    public void unVanishPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish.Leave"));
        staffInventory.setOff(player, "Vanish Mode");
        vanishedPlayers.remove(player.getUniqueId());
        TABAPI.removeTemporaryCustomTagName(player.getUniqueId());
        //NicknameHandler.getInstance(plugin).removeCustomPlayerName(player);
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> {
            o.hidePlayer(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> o.showPlayer(player), 1);
        });
        ActionBar.setActionBar(player, dataHandler);
    }
}
