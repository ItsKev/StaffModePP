package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.datahandler.DataHandler;
import io.github.itskev.staffmodepp.inventory.StaffInventory;
import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class VanishModule {

    private List<UUID> vanishedPlayers;
    private StaffInventory staffInventory;

    public VanishModule(DataHandler dataHandler) {
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
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        String staffPermission = ConfigHelper.getStringFromConfig("Staffpermission");
        onlinePlayers.forEach(o -> {
            if (!o.hasPermission(staffPermission)) {
                o.hidePlayer(player);
            }
        });
    }

    public void unVanishPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish.Leave"));
        staffInventory.setOff(player, "Vanish Mode");
        vanishedPlayers.remove(player.getUniqueId());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> o.showPlayer(player));
    }
}
