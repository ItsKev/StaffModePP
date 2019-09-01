package io.github.itskev.staffmodepp.modules;

import io.github.itskev.staffmodepp.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VanishModule {

    private List<UUID> vanishedPlayers;

    public VanishModule() {
        vanishedPlayers = new ArrayList<>();
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
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish-Enter"));
        vanishedPlayers.add(player.getUniqueId());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> o.hidePlayer(player));
    }

    public void unVanishPlayer(Player player) {
        player.sendMessage(ConfigHelper.getStringFromConfig("Vanish-Leave"));
        vanishedPlayers.remove(player.getUniqueId());
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(o -> o.showPlayer(player));
    }
}
